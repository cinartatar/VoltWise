import {ApplianceList} from "./ApplianceList.js";
import {ConsumptionChart} from "./ConsumptionChart.js";
export class ModalHome{
    constructor(home) {
        this.home=home;

        this.pollingId = null;
        this.applianceList = null;

        this.costElement = null;
        this.energyElement = null;
        this.percentageElement = null;
        this.stateElement = null;
    }

    async render() {
        const overlay = document.createElement("div");
        overlay.className = "modal-overlay";
        //full screen layer before the popup

        const modal = document.createElement("div");
        modal.className = "modal-home";

        const metrics = await this.loadHomeMetrics();
        console.log("HOME METRICS:", metrics);

        const title = document.createElement("h2");
        title.textContent = `Home ${this.home.id}`;
        modal.appendChild(title);

        const budget = document.createElement("p");
        budget.textContent = `Budget ${this.home.monthlyBudgetLimit}`;
        modal.appendChild(budget);

        const email = document.createElement("p");
        email.textContent = this.home.contactEmail;
        modal.appendChild(email);

        if (metrics){
            this.costElement = document.createElement("p");
            this.costElement.textContent = `Current Cost: ${metrics.accumulatedCost.toFixed(2)}`;
            modal.appendChild(this.costElement);

            this.energyElement = document.createElement("p");
            this.energyElement.textContent =
                `Energy Used: ${metrics.accumulatedEnergyKWh.toFixed(2)} kWh`;
            modal.appendChild(this.energyElement);

            this.percentageElement = document.createElement("p");
            this.percentageElement.textContent =
                `Budget Used: ${(metrics.budgetPercentage*100).toFixed(2)}%`;
            modal.appendChild(this.percentageElement);

            this.stateElement = document.createElement("p");
            this.stateElement.textContent =
                `Budget State: ${metrics.budgetState}`;
            modal.appendChild(this.stateElement);

        } else {
            const unavailable = document.createElement("p");
            unavailable.textContent = "No live metrics available yet"
            modal.appendChild(unavailable);
        }

        const appliances = await this.loadAppliances();

        const applianceMetrics =
            await this.loadApplianceMetrics(appliances);

        const combinedAppliances = appliances.map((appliance,index)=>({
            ...appliance, ...applianceMetrics[index]
        }))

        this.applianceList = new ApplianceList(combinedAppliances);
        modal.appendChild(this.applianceList.render());


        const consumption= await this.loadConsumption();
        const chart=new ConsumptionChart(consumption);
        modal.appendChild(chart.render())



        overlay.appendChild(modal);

        if (metrics)
            this.startPolling();


        return overlay;
    }

    startPolling(){
        this.pollingId = setInterval(async ()=> {
            try{
                const metrics = await this.loadHomeMetrics();
                if (!metrics) return;

                this.costElement.textContent =
                    `Current Cost: ${metrics.accumulatedCost.toFixed(2)}`;

                this.energyElement.textContent =
                    `Energy Used: ${metrics.accumulatedEnergyKWh.toFixed(2)} kWh`;

                this.percentageElement.textContent =
                    `Budget Used: ${(metrics.budgetPercentage*100).toFixed(2)}%`;

                this.stateElement.textContent =
                    `Budget State: ${metrics.budgetState}`;
            }
            catch (error){
                console.error("Home polling failed: ", error);
            }
        },2000)
    }

    stopPolling(){
        if (this.pollingId!==null){
            clearInterval(this.pollingId);
            this.pollingId=null;
        }
        if (this.applianceList!==null)
            this.applianceList.stopPolling();
    }



    async loadAppliances(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getAppliances/${this.home.id}`);

        if (!response.ok){
            throw new Error(`Failed to load appliances: ${response.status}`);
        }

        return await response.json();
    }
    async loadConsumption(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getDailyConsumption/${this.home.id}`);

        if (!response.ok){
            throw new Error(`Failed to load consumption: ${response.status}`);
        }

        return await response.json();
    }
    async loadHomeMetrics(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getHomeMetrics/${this.home.id}`);

        if (!response.ok){
            throw new Error(`Failed to load home metrics: ${response.status}`);
        }

        const text = await response.text();

        if (!text)
            return null;

        return JSON.parse(text);
    }

    async loadApplianceMetrics(appliances){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later


        const requests= appliances.map(async appliance => {
            const response = await fetch(`/metric/getApplianceMetrics/${appliance.id}`);
            if (!response.ok){
                throw new Error(`Failed to load appliance: ${appliance.id}`);

            }
            const text = await response.text();

            if (!text)
                return null;

            return JSON.parse(text);
        })
        return await Promise.all(requests)
    }
}