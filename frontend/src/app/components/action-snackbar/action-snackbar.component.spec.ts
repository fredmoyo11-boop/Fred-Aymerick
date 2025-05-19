import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionSnackbarComponent } from './action-snackbar.component';

describe('LoginSnackbarComponent', () => {
  let component: ActionSnackbarComponent;
  let fixture: ComponentFixture<ActionSnackbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActionSnackbarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActionSnackbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
