
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

        const id=document.createElement("p");
        id.textContent=`Appliance ID ${this.appliance.id}`;
        card.appendChild(id);

        const threshold = document.createElement("p");
        threshold.textContent = `Power threshold: ${this.appliance.powerThreshold} W`;
        card.appendChild(threshold);

        return card;
    }
}