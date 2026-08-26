import {ModalHome} from "./ModalHome.js";

export class HomeCard{
    constructor(home) {
        this.home = home;
    }
    //render into html code
    render(){

        const card= document.createElement("article");
        card.className = "home-card";

        const title= document.createElement("h2");
        title.textContent = `Home ${this.home.id}`;
        card.appendChild(title);

        const budget=document.createElement("p");
        budget.textContent=`Budget ${this.home.monthlyBudgetLimit}`;
        card.appendChild(budget);

        const email = document.createElement("p");
        email.textContent = this.home.contactEmail;
        card.appendChild(email);

        card.addEventListener("click",async ()=>{
            //1st html el whose class is modal-overlay
            const existingModal= document.querySelector(".modal-overlay");
            if (existingModal){
                existingModal.remove();
            }


            const modal=new ModalHome(this.home);
            const modalElement= await modal.render();
            document.body.appendChild(modalElement);
        });

        return card;
    }
}