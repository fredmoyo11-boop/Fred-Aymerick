import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveRideDialogComponent } from './active-ride-dialog.component';

describe('ActiveRideDialogComponent', () => {
  let component: ActiveRideDialogComponent;
  let fixture: ComponentFixture<ActiveRideDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveRideDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveRideDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
