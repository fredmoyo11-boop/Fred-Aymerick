import {Component, inject, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {BaseChartDirective} from 'ng2-charts';
import {NgIf} from '@angular/common';
import {StatisticsService} from '../../../api/sep_drive';

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

  showWholeYear = true;

  selectedType = 'DISTANCE';
  selectedYear = new Date().getFullYear();
  selectedMonth = new Date().getMonth() + 1;

  chartData: number[] = [];
  chartLabels: string[] = [];
  chartTitle = '';

  chartType: ChartType = "bar"
  chartOptions = {responsive: true};

  validInput: boolean = true;

  REVENUE = "REVENUE";

  DISTANCE = "DISTANCE";

  TIME = "TIME";

  RATING = "RATING";


  statisticsService = inject(StatisticsService);

  ngOnInit() {
    this.loadChart();
  }

  switchShow() {
    this.showWholeYear = !this.showWholeYear;
    this.loadChart();

  }

  loadChart() {
    this.yearInputValidator(this.selectedYear)
    if (!this.validInput) {
      return;
    }
    this.monthInputValidator(this.selectedMonth)
    if (!this.validInput) {
      return;
    }


    if (this.showWholeYear) {
      this.statisticsService.getStatisticsForYear(this.selectedType, this.selectedYear).subscribe(data => {
        this.chartData = data;


        for (let i = 0; i < this.chartData.length; i++) {
          if (this.chartData.at(i) === undefined || this.chartData.at(i) === null) {
            this.chartData[i] = 0;
          }
        }

        if (this.selectedType == this.DISTANCE){
          for (let i= 0; i < this.chartData.length; i++){
            this.chartData[i] = this.chartData[i] /1000;
          }
        }
        if (this.selectedType == this.TIME){
          for (let i= 0; i < this.chartData.length; i++){
            this.chartData[i] = this.chartData[i] /60;
          }
        }


        this.chartLabels = ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'];
        this.chartTitle = `Jahresstatistik für die ` + this.typeTranslator(this.selectedType);
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
        if (this.selectedType == this.DISTANCE){
          for (let i= 0; i < this.chartData.length; i++){
            this.chartData[i] = this.chartData[i] /1000;
          }
        }
        if (this.selectedType == this.TIME){
          for (let i= 0; i < this.chartData.length; i++){
            this.chartData[i] = this.chartData[i] /60;
          }
        }

        this.chartTitle = `Monatsstatistik für die ` + this.typeTranslator(this.selectedType);

      });
    }



  }

  yearInputValidator(year: number): boolean {
    if (!year) {
      this.validInput = false;
      return false;
    }
    if (isNaN(year)) {
      this.validInput = false;
      return false;
    }
    if (year < 2000) {
      this.validInput = false;
      return false;
    }
    if (year > 20000) {
      this.validInput = false;
      return false;
    }

    this.validInput = true;
    return true;
  }


  monthInputValidator(month: number): boolean {
    if (!month) {
      this.validInput = false;
      return false;
    }
    if (isNaN(month)) {
      this.validInput = false;
      return false;
    }
    if (month > 12 || month < 1) {
      this.validInput = false;
      return false;
    }
    this.validInput = true;
    return true;

  }

  typeTranslator(type: String): String {

    if (type === this.DISTANCE) {
      return "Entfernung";
    }
    if (type === this.REVENUE) {
      return "Einnahmen";
    }
    if (type === this.RATING) {
      return "Bewertungen";
    }

    return "Fahrdauer";


  }
}

