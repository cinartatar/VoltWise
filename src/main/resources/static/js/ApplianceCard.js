
export class ApplianceCard{
    constructor(appliance) {
        this.appliance = appliance;
        this.pollingId = null;
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
            this.appliance.currentPowerWatts !== undefined
                ? `Current Power: ${this.appliance.currentPowerWatts.toFixed(2)} W`
                : "Current Power: unavailable";
        card.appendChild(this.currentPowerWatts);

        this.accumulatedEnergyKWh = document.createElement("p");
        this.accumulatedEnergyKWh.textContent =
            this.appliance.accumulatedEnergyKWh !== undefined
                ? `Energy Used: ${this.appliance.accumulatedEnergyKWh.toFixed(2)} kWh`
                : "Energy Used: unavailable";
        card.appendChild(this.accumulatedEnergyKWh);

        this.anomaly = document.createElement("p");
        this.anomaly.textContent =
            `Anomalous: ${this.appliance.anomalous}`;
        card.appendChild(this.anomaly);

        this.startPolling(card);

        return card;
    }

    async loadMetrics(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getApplianceMetrics/${this.appliance.id}`);

        if (!response.ok){
            throw new Error(`Failed to load appliance metrics ${this.appliance.id}`);
        }

        const text = await response.text();

        if (!text)
            return null;

        return JSON.parse(text);
    }

    startPolling(card){
        this.pollingId = setInterval(async ()=> {
            try{
                const metrics = await this.loadMetrics();
                if (!metrics) return;

                this.currentPowerWatts.textContent =
                    metrics.currentPowerWatts !== undefined
                        ? `Current Power: ${metrics.currentPowerWatts.toFixed(2)} W`
                        : "Current Power: unavailable";

                this.accumulatedEnergyKWh.textContent =
                    metrics.accumulatedEnergyKWh !== undefined
                        ? `Energy Used: ${metrics.accumulatedEnergyKWh.toFixed(2)} kWh`
                        : "Energy Used: unavailable";

                this.anomaly.textContent=
                    `Anomalous: ${metrics.anomalous}`;

                if (metrics.anomalous) {
                    card.classList.add("anomalous");
                } else {
                    card.classList.remove("anomalous");
                }
            }
            catch (error){
                console.error("Appliance polling failed: ", error);
            }

        },2000)
    }

    stopPolling(){
        if (this.pollingId!==null){
            clearInterval(this.pollingId);
            this.pollingId = null;
        }
    }
}