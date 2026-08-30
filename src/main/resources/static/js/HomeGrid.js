import {HomeCard} from "./HomeCard.js";

export class HomeGrid{

    constructor(homes) {
        this.homes=homes;
    }

    render(){
        const grid= document.createElement("section");
        grid.className="home-grid";

        this.homes.forEach(home => {
            const homeCard= new HomeCard(home);
            grid.appendChild(homeCard.render());
        });

        return grid;
    }
}