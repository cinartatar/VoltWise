package com.voltwise.energy_monitor.core;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.voltwise.energy_monitor.model.*;
import com.voltwise.energy_monitor.repository.*;
import com.voltwise.energy_monitor.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotifModule {
    // AI notif
        //prompt orchestration
            //gather home metric, billing totals, active anomaly data from apa ig and pgsql
            //asemble the context into a prompt layout and transmit to LLM to synthesize an advisory textin turkish
        //email dispatch and logging
            //persist ai generate recommendation into postgresql
            //dispatch the tailored email notif to the contact email address defined for home


    //important stuff -> module gets told ->  handle notif
    private final Client client=new Client(); //Gemini's client
    private final HomeRepository homeRepository;
    private final ApplianceRepository applianceRepository;
    private final HomeMetricsRepository homeMetricsRepository;
    private final ApplianceMetricsRepository applianceMetricsRepository;
    private final RecommendationRepository recommendationRepository;
    private final EmailService emailService;
    @Value("${app.tariff.normal}")
    private double NORMAL_TARIFF_RATE;
    @Value("${app.tariff.penalty}")
    private double PENALTY_TARIFF_RATE;

    public NotifModule(HomeRepository homeRepository, ApplianceRepository applianceRepository, HomeMetricsRepository homeMetricsRepository, ApplianceMetricsRepository applianceMetricsRepository, RecommendationRepository recommendationRepository, EmailService emailService) {
        this.homeRepository = homeRepository;
        this.applianceRepository = applianceRepository;
        this.homeMetricsRepository = homeMetricsRepository;
        this.applianceMetricsRepository = applianceMetricsRepository;
        this.recommendationRepository = recommendationRepository;
        this.emailService = emailService;
    }


    public String generateAdvice(String prompt){
        //call gemini
        try{
            System.out.println("GOOGLE_API_KEY visible to JVM: " +
                    (System.getenv("GOOGLE_API_KEY") != null));

            GenerateContentResponse response=client.models.generateContent("gemini-3.6-flash",prompt,null);

            //return generated text
            return response.text();
        }catch (Exception e){
            System.err.println("Gemini unavailable: "+e.getMessage());
            return """
                    Enerji tüketiminiz için önemli bir uyarı oluşturuldu.
                    Lütfen mevcut tüketim değerlerinizi kontrol edin.
                    Ayrıntılı yapay zeka önerisi şu anda kullanılamıyor.
                    """;
        }

    }

    public String budgetWarningPrompt(Home home) {
        HomeMetrics metrics =
                homeMetricsRepository.getByHomeId(home.getId());

        if (metrics == null) return null;

        return """
            Bir akıllı ev enerji izleme sistemi için kullanıcıya kısa bir uyarı mesajı yaz.

            Ev ID: %d
            Mevcut toplam maliyet: %.2f
            Aylık bütçe limiti: %.2f
            Bütçe kullanım oranı: %.1f%%

            Kullanıcı aylık bütçesinin yüzde 80 sınırını geçti.
            Mesaj Türkçe olmalı.
            Kullanıcıyı bütçe sınırına yaklaştığı konusunda bilgilendir.
            Enerji tüketimini azaltmak için 2-3 kısa ve uygulanabilir öneri ver.
            Mesaj profesyonel, açık ve kısa olsun.
            """.formatted(
                home.getId(),
                metrics.getAccumulatedCost(),
                home.getMonthlyBudgetLimit(),
                metrics.getBudgetPercentage() * 100
        );
    }

    public String penaltyPrompt(Home home) {
        HomeMetrics metrics =
                homeMetricsRepository.getByHomeId(home.getId());

        if (metrics == null) return null;

        return """
            Bir akıllı ev enerji izleme sistemi için kullanıcıya önemli bir bildirim yaz.

            Ev ID: %d
            Mevcut toplam maliyet: %.2f
            Aylık bütçe limiti: %.2f
            Normal tarife oranı: %.2f
            Ceza tarife oranı: %.2f

            Kullanıcı aylık bütçe limitinin yüzde 100'üne ulaştı veya bu limiti geçti.
            Sistem bu nedenle ceza tarifesini etkinleştirdi.

            Mesaj Türkçe olmalı.
            Ceza tarifesinin etkinleştiğini açıkça belirt.
            Bundan sonraki tüketimin daha yüksek tarife üzerinden hesaplanacağını açıkla.
            Enerji maliyetini azaltmak için 2-3 uygulanabilir öneri ver.
            Mesaj profesyonel, açık ve kısa olsun.
            """.formatted(
                home.getId(),
                metrics.getAccumulatedCost(),
                home.getMonthlyBudgetLimit(),
                NORMAL_TARIFF_RATE,
                PENALTY_TARIFF_RATE
        );
    }

    public String applianceAnomalyPrompt(Appliance appliance) {
        ApplianceMetrics metrics =
                applianceMetricsRepository.getByApplianceId(appliance.getId());

        if (metrics == null) return null;

        return """
            Bir akıllı ev enerji izleme sistemi için kullanıcıya cihaz anomalisi uyarısı yaz.

            Ev ID: %d
            Cihaz ID: %d
            Cihaz adı: %s
            Mevcut güç tüketimi: %d W
            Tanımlı güç sınırı: %.0f W

            Bu cihaz tanımlı güç sınırını arka arkaya en az 3 ölçüm boyunca aştı
            ve sistem tarafından anomalili olarak işaretlendi.

            Mesaj Türkçe olmalı.
            Hangi cihazda anomali tespit edildiğini açıkça belirt.
            Kullanıcıya cihazı kontrol etmesini öner.
            Kesin bir teknik arıza teşhisi koyma.
            Gerekirse bir uzmana kontrol ettirmesini öner.
            Mesaj profesyonel, açık ve kısa olsun.
            """.formatted(
                appliance.getHomeId(),
                appliance.getId(),
                appliance.getName(),
                metrics.getCurrentPowerWatts(),
                appliance.getPowerThreshold()
        );
    }

    //send budget warning
    @Async
    public void sendBudgetWarning(int homeId){
        Home home=homeRepository.findById(homeId).orElse(null);
        if (home==null) return;
        String prompt=budgetWarningPrompt(home);
        if (prompt==null) return;
        String advice=generateAdvice(prompt);
        if (advice==null) return;

        Recommendation recommendation=
                new Recommendation(homeId,null,"BUDGET_WARNING",advice);

        try {
            recommendationRepository.save(recommendation);
        }catch (Exception e){
            System.err.println("Recommendation save failed: "+e.getMessage());
        }
        try {
        emailService.send(home.getContactEmail(),"Enerji Bütçe Uyarısı",advice);
        }catch (Exception e){
            System.err.println("Email sending failed: "+ e.getMessage());
        }
    }
    //send penalty alert
    @Async
    public void sendPenaltyAlert(int homeId){

        Home home=homeRepository.findById(homeId).orElse(null);


        if (home==null) return;
        String prompt=penaltyPrompt(home);
        if (prompt==null) return;

        String advice=generateAdvice(prompt);


        if (advice==null) return;

        Recommendation recommendation=
                new Recommendation(homeId,null,"PENALTY",advice);

        try {
            recommendationRepository.save(recommendation);
        }catch (Exception e){
            System.err.println("Recommendation save failed: "+e.getMessage());
        }
        try {
        emailService.send(home.getContactEmail(),"Enerji Bütçesi Aşıldı",advice);
        }catch (Exception e){
            System.err.println("Email sending failed: "+ e.getMessage());
        }
    }
    //send appliance alert
    @Async
    public void sendApplianceAlert(int applianceId){
        Appliance appliance=applianceRepository.findById(applianceId).orElse(null);
        if (appliance==null) return;
        String prompt=applianceAnomalyPrompt(appliance);
        if (prompt==null) return;
        String advice=generateAdvice(prompt);
        if (advice==null) return;
        Home home = homeRepository.findById(appliance.getHomeId()).orElse(null);
        if (home == null) return;

        Recommendation recommendation=
                new Recommendation(appliance.getHomeId(),applianceId,"APPLIANCE_ANOMALY",advice);

        try {
            recommendationRepository.save(recommendation);
        }catch (Exception e){
            System.err.println("Recommendation save failed: "+e.getMessage());
        }
        try {
            emailService.send(home.getContactEmail(),"Cihaz Tüketim Anomalisi",advice);
        }catch (Exception e){
            System.err.println("Email sending failed: "+ e.getMessage());
        }
    }

}
