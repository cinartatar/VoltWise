import {ModalHome} from "./ModalHome.js";
import {showToast} from "./Toast.js";

export class HomeCard{

    static activeModal=null;

    constructor(home) {
        this.home = home;
        this.cardElement = null;
        this.pollingId = null;
    }
    //render into html code
    render(){

        this.cardElement= document.createElement("article");
        this.cardElement.className = "home-card";

        this.updateState(this.home);

        const title= document.createElement("h2");
        title.textContent = `Home ${this.home.id}`;
        this.cardElement.appendChild(title);

        const budget=document.createElement("p");
        budget.textContent=`Budget ${this.home.monthlyBudgetLimit}`;
        this.cardElement.appendChild(budget);

        const email = document.createElement("p");
        email.textContent = this.home.contactEmail;
        this.cardElement.appendChild(email);

        this.cardElement.addEventListener("click",async ()=>{
            //first html element whose class is modal-overlay
            const existingModal =
                document.querySelector(".modal-overlay");


            if (HomeCard.activeModal!==null){

                const sameHome = HomeCard.activeModal.home.id === this.home.id;
                HomeCard.activeModal.stopPolling();
                if (existingModal){
                    existingModal.remove();
                }
                HomeCard.activeModal =null;
                if (sameHome) {
                    return;
                }
            }

            const loading = document.createElement("div");
            loading.className = "loading";
            loading.textContent = "Loading home...";
            document.body.appendChild(loading);



            try{
                const modal=new ModalHome(this.home);
                const modalElement= await modal.render();
                HomeCard.activeModal = modal;
                document.body.appendChild(modalElement);
            }catch (error){
                console.error(error);
                showToast("Could not load this home's data. Please try again");
            }
            finally {
                loading.remove();
            }
        });

        this.startPolling();
        return this.cardElement;
    }
    async loadMetrics(){
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

    startPolling(){
        this.pollingId = setInterval(async () => {
           try {
               const metrics = await this.loadMetrics();
               this.updateState(metrics);
           }catch (error){
               console.error("Home card polling failed", error);
           }
        },2000);
    }

    stopPolling(){
        if (this.pollingId !== null){
            clearInterval(this.pollingId);
            this.pollingId=null;
        }
    }

    updateState(metrics){
        this.cardElement.classList.remove("normal","warning","penalty","unknown");

        if (!metrics || !metrics.budgetState){
            this.cardElement.classList.add("unknown");
        }
        else if (metrics.budgetState === "WARNING") {
            this.cardElement.classList.add("warning")
        }
        else if (metrics.budgetState === "PENALTY") {
            this.cardElement.classList.add("penalty");
        }
        else {
            this.cardElement.classList.add("normal");
        }
    }
}