import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilecollectionComponent } from './profilecollection.component';

describe('ProfilecollectionComponent', () => {
  let component: ProfilecollectionComponent;
  let fixture: ComponentFixture<ProfilecollectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfilecollectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfilecollectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
