import {ApplianceCard} from "./ApplianceCard.js";

export class ApplianceList{

    constructor(appliances) {
        this.appliances=appliances;
    }

    render(){
        const list= document.createElement("section");
        list.className="appliance-list";

        this.appliances.forEach(appliance => {
            const applianceCard= new ApplianceCard(appliance);
            list.appendChild(applianceCard.render());
        });

        return list;
    }
}