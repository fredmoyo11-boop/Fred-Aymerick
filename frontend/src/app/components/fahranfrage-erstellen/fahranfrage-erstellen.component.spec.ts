import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FahranfrageErstellenComponent } from './fahranfrage-erstellen.component';

describe('FahranfrageErstellenComponent', () => {
  let component: FahranfrageErstellenComponent;
  let fixture: ComponentFixture<FahranfrageErstellenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FahranfrageErstellenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FahranfrageErstellenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
