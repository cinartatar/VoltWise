
export class ApplianceCard{
    constructor(appliance) {
        this.appliance = appliance;
    }
    //render into html code
    render(){

        const card= document.createElement("article");
        card.className = "appliance-card";

        const title= document.createElement("h2");
        title.textContent = `Appliance ${this.appliance.name}`;
        card.appendChild(title);

        if (this.appliance.anomalous)
            card.classList.add("anomalous")

        const id=document.createElement("p");
        id.textContent=`Appliance ID ${this.appliance.id}`;
        card.appendChild(id);

        const threshold = document.createElement("p");
        threshold.textContent = `Power threshold: ${this.appliance.powerThreshold} W`;
        card.appendChild(threshold);

        this.currentPowerWatts = document.createElement("p");
        this.currentPowerWatts.textContent =
            `Current Cost: ${this.appliance.currentPowerWatts.toFixed(2)} W`;
        card.appendChild(this.currentPowerWatts);

        this.accumulatedEnergyKWh = document.createElement("p");
        this.accumulatedEnergyKWh.textContent =
            `Energy Used: ${this.appliance.accumulatedEnergyKWh.toFixed(2)} kWh`;
        card.appendChild(this.accumulatedEnergyKWh);

        this.anomaly = document.createElement("p");
        this.anomaly.textContent =
            `Anomalous: ${this.appliance.anomalous} kWh`;
        card.appendChild(this.anomaly);

        this.startPolling(card);

        return card;
    }

    async loadMetrics(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getApplianceMetrics/${this.appliance.id}`);

        const text = await response.text();

        if (!text)
            return null;

        return JSON.parse(text);
    }

    startPolling(card){
        this.pollingId = setInterval(async ()=> {
            const metrics = await this.loadMetrics();
            if (!metrics) return;

            this.currentPowerWatts.textContent =
                `Current Cost: ${metrics.accumulatedCost.toFixed(2)}`;

            this.accumulatedEnergyKWh.textContent =
                `Energy Used: ${metrics.accumulatedEnergyKWh.toFixed(2)} kWh`;

            this.anomaly.textContent=
                `Anomalous: ${metrics.anomalous}`;

            if (metrics.anomalous) {
                card.classList.add("anomalous");
            } else {
                card.classList.remove("anomalous");
            }

        },5000)
    }
}