import {Component, inject, OnInit} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { NgIf } from '@angular/common';
import { StatisticsService } from '../../../api/sep_drive';

import {
  Chart,
  ChartType,
  ChartConfiguration,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

@Component({
  selector: 'app-stats',
  imports: [
    FormsModule,
    BaseChartDirective,
    NgIf
  ],
  templateUrl: 'stats.component.html',
  styleUrl: `stats.component.css`
})
export class StatsComponent implements OnInit {

  constructor() {
    Chart.register(
      BarController,
      BarElement,
      CategoryScale,
      LinearScale,
      Title,
      Tooltip,
      Legend
    );
  }

  showWholeAlone = true;

  selectedType = 'DISTANCE';
  selectedYear = new Date().getFullYear();
  selectedMonth = new Date().getMonth() + 1;

  chartData: number[] = [];
  chartLabels: string[] = [];
  chartTitle = '';

  chartType: ChartType = "bar"
  chartOptions = { responsive: true };


  validInput:boolean =  true;

 REVENUE = "REVENUE";

 DISTANCE = "DISTANCE";

TIME = "TIME";

RATING = "RATING";


  statisticsService = inject(StatisticsService);

  ngOnInit() {
    this.loadChart();
  }

  switchShow() {
    this.showWholeAlone = !this.showWholeAlone;
    this.loadChart();

  }

  loadChart() {
    this.yearValidator(this.selectedYear)
    if(!this.validInput){
      return;
    }
    this.monthValidator(this.selectedMonth)
    if(!this.validInput){
      return;
    }


    if (this.showWholeAlone) {
      this.statisticsService.getStatisticsForYear(this.selectedType, this.selectedYear).subscribe(data => {
        this.chartData = data;


        for (let i = 0; i < this.chartData.length; i++) {
          if (this.chartData.at(i) === undefined || this.chartData.at(i) === null) {
            this.chartData[i] = 0;
          }
        }

        this.chartLabels = ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'];
        this.chartTitle = `Jahresstatistik für ` + this.typeConverter(this.selectedType);
      });

    } else {
      this.statisticsService.getStatisticsForMonth(this.selectedType, this.selectedYear, this.selectedMonth).subscribe(data => {
        this.chartData = data;

        this.chartLabels = [];
        for (let i = 1; i <= this.chartData.length; i++) {
          this.chartLabels.push(i.toString());
          if (this.chartData.at(i - 1) === undefined || this.chartData.at(i - 1) === null) {
            this.chartData[i - 1] = 0;
          }
        }

        this.chartTitle = `Monatsstatistik für ` + this.typeConverter(this.selectedType);

      });
    }



  }
  yearValidator(year: number):boolean{
    if (!year){
      this.validInput = false;
      return false;
    }
    if (isNaN(year)){ //diffrence Number, number
      this.validInput = false;
      return false;
    }
    if(year < 2000 ){
      this.validInput = false;
      return false;
    }
    if( year > 20000 ){
      this.validInput = false;
      return false;
    }

    this.validInput = true;
    return true;
  }


  monthValidator(month: number):boolean{
    if (!month){
      this.validInput = false;
      return false;
    }
    if (isNaN(month)){ //diffrence Number, number
      this.validInput = false;
      return false;
    }
    if(month > 12 || month < 1){
      this.validInput = false;
      return false;
    }
    this.validInput = true;
    return true;

  }
  typeConverter(type:String):String{
    if (type === this.DISTANCE){
      return "Entfernung";
    }
    if (type === this.REVENUE){
      return "Einnahmen";
    }
    if (type === this.RATING){
      return "Bewertung";
    }

      return "Fahrdauer";


  }
}

