import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteRideDialogComponent } from './delete-ride-dialog.component';

describe('DeleteRideDialogComponent', () => {
  let component: DeleteRideDialogComponent;
  let fixture: ComponentFixture<DeleteRideDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteRideDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteRideDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
