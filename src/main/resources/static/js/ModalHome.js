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

        const title = document.createElement("h2");
        title.textContent = `Home ${this.home.id}`;
        modal.appendChild(title);

        const budget = document.createElement("p");
        budget.textContent = `Budget ${this.home.monthlyBudgetLimit}`;
        modal.appendChild(budget);

        const email = document.createElement("p");
        email.textContent = this.home.contactEmail;
        modal.appendChild(email);

        const appliances = await this.loadAppliances();

        const applianceList = new ApplianceList(appliances);
        modal.appendChild(applianceList.render());

        const consumption= await this.loadConsumption();
        const chart=new ConsumptionChart(consumption);
        modal.appendChild(chart.render())

        overlay.appendChild(modal);
        return overlay;
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
}