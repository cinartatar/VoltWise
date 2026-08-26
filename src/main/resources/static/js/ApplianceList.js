import {ApplianceCard} from "./ApplianceCard.js";

export class ApplianceList{

    constructor(appliances) {
        this.appliances=appliances;
    }

    render(){
        const list= document.createElement("section");
        list.className="appliance-list";

        if (this.appliances.length === 0){
            const message=document.createElement("p");
            message.textContent=`No registered appliances`;
            list.appendChild(message);
        }

        this.appliances.forEach(appliance => {
            const applianceCard= new ApplianceCard(appliance);
            list.appendChild(applianceCard.render());
        });

        return list;
    }
}