export class ConsumptionChart{
    constructor(consumptionData) {
        this.consumptionData=consumptionData;
    }

    render(){
        const section= document.createElement("section");
        section.className="consumption-chart";

        const title= document.createElement("h2");
        title.textContent = "Daily Consumption";

        const canvas = document.createElement("canvas");
        section.appendChild(title);
        section.appendChild(canvas);

        //make copy first so we don't mutate the array
        const sortedData= [...this.consumptionData].sort((a,b)=> new Date(a.date)- new Date(b.date));

        const dates= sortedData.map(day => day.date);
        const energyValues = sortedData.map(day => day.totalEnergyKWh)

        new Chart(canvas, {
            type: "line",

            data:{
                labels: dates,
                datasets:[
                    {
                        label: "Energy consumed (kWh)",
                        data: energyValues
                    }
                ]
            }
        })

        return section;
    }
}