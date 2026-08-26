import {ApplianceList} from "./ApplianceList.js";
import {ConsumptionChart} from "./ConsumptionChart.js";
export class ModalHome{
    constructor(home) {
        this.home=home;
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
                `Budget Used: ${metrics.budgetPercentage.toFixed(2)}%`;
            modal.appendChild(this.percentageElement);

            this.stateElement = document.createElement("p");
            this.stateElement.textContent =
                `Budget State: ${metrics.budgetState}`;
            modal.appendChild(this.stateElement);

            this.startPolling()
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

        const applianceList = new ApplianceList(combinedAppliances);
        modal.appendChild(applianceList.render());


        const consumption= await this.loadConsumption();
        const chart=new ConsumptionChart(consumption);
        modal.appendChild(chart.render())



        overlay.appendChild(modal);


        return overlay;
    }

    startPolling(){
        this.pollingId = setInterval(async ()=> {
            const metrics = await this.loadHomeMetrics();
            if (!metrics) return;

            this.costElement.textContent =
                `Current Cost: ${metrics.accumulatedCost.toFixed(2)}`;

            this.energyElement.textContent =
                `Energy Used: ${metrics.accumulatedEnergyKWh.toFixed(2)} kWh`;

            this.percentageElement.textContent =
                `Budget Used: ${metrics.budgetPercentage.toFixed(2)}%`;

            this.stateElement.textContent =
                `Budget State: ${metrics.budgetState}`;
        },5000)
    }

    async loadAppliances(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getAppliances/${this.home.id}`);

        const appliances=await response.json();
        return appliances;
    }
    async loadConsumption(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getDailyConsumption/${this.home.id}`);

        const consumption=await response.json();
        return consumption;
    }
    async loadHomeMetrics(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getHomeMetrics/${this.home.id}`);

        const text = await response.text();

        if (!text)
            return null;

        return JSON.parse(text);
    }

    async loadApplianceMetrics(appliances){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later


        const requests= appliances.map(appliance =>
            fetch(`/metric/getApplianceMetrics/${appliance.id}`).then(response => response.json()))

        return await Promise.all(requests);
    }
}