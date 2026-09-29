import {Dashboard} from "./Dashboard.js";
class App{

    constructor() {
        this.init();

    }

    async init(){
        const homes= await this.loadHomes();
        homes.sort((a,b) => a.id - b.id);

        this.homesSnapshot = JSON.stringify(homes);

        const homeMetrics = await this.loadHomeMetrics(homes);

        const combinedHomes = homes.map((home,index) => ({
            ...home,
            ...homeMetrics[index]
        }));

        this.dashboard=new Dashboard("Dashboard",combinedHomes);

        const dashboardElement=this.dashboard.render();

        const appElement = document.getElementById("app");
        appElement.appendChild(dashboardElement);
        this.startHomePolling();
    }

    //async while fetch happens, don't wait
    async loadHomes(){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const response= await fetch(`/metric/getHomes`);

        if (!response.ok) {
            throw new Error(
                `Failed to load homes: ${response.status}`
            );
        }

        return await response.json();
    }

    async loadHomeMetrics(homes){
        //await: pause the function till this is done
        //fetch: gives a promise/ result will arrive later
        const requests=homes.map(async home => {
            const response= await fetch(`/metric/getHomeMetrics/${home.id}`);

            const text = await response.text();

            if (!text)
                return {};

            return JSON.parse(text);
        });

        return await Promise.all(requests);
    }

    startHomePolling(){
        setInterval(async ()=>{
            try {
                const homes = await this.loadHomes();
                homes.sort((a, b) => a.id - b.id);

                const newSnapshot =
                    JSON.stringify(homes);

                if (newSnapshot !== this.homesSnapshot) {
                    window.location.reload();
                }
            }catch (error){
                console.error(
                    "Failed to check for home updates:",
                    error
                );
            }
        },2000)
    }



}
new App();



//SPA user interface of the ecosystem
//provide a clean, responsive and intuitive interface for users to monitor ongoing
//electricity consumption profiles
//observe escalating utility costs
//track ai generated behavioral alert systems

//real time dashboard grid/ list of all registered residential structures
//Click on a home card -> modal popup

//dynamic quota breach
//visually differentiate WARNING/PENALTY homes
//in modal home - visually distinguish anomalous appliances

//interactive analytical charts (in modal homes)
//dyn render periodic charts for each home using a vis lib

//ui fluidity
//async loading
//error interception

/*
* App
*   Dashboard
*       HomeGrid
*           HomeCard.js
*   ModalHome
*       HomeStats
*       ApplianceList
*           ApplianceCard
*       Chart
* */



