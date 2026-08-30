import {HomeGrid} from "./HomeGrid.js";

export class Dashboard{



    constructor(title,homes) {
        this.title=title;
        this.homeGrid= new HomeGrid(homes);
    }

    render(){
        //create <main class="dashboard"></main>
        const dashboard = document.createElement("main")
        dashboard.className="dashboard";


        //create <h1>VoltWise Dashboard</h1>
        const title = document.createElement("h1");
        title.textContent = this.title;
        //title is now inside <main></main>
        dashboard.appendChild(title);


        dashboard.appendChild(this.homeGrid.render());

        return dashboard;
    }
}

