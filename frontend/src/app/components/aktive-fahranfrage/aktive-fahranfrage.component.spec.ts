import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AktiveFahranfrageComponent } from './aktive-fahranfrage.component';

describe('AktiveFahranfrageComponent', () => {
  let component: AktiveFahranfrageComponent;
  let fixture: ComponentFixture<AktiveFahranfrageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AktiveFahranfrageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AktiveFahranfrageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
