import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnprofileComponent } from './ownprofile.component';

describe('OwnprofileComponent', () => {
  let component: OwnprofileComponent;
  let fixture: ComponentFixture<OwnprofileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OwnprofileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OwnprofileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
