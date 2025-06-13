import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailableTriprequestComponent } from './available-triprequest.component';

describe('AvailableTriprequestComponent', () => {
  let component: AvailableTriprequestComponent;
  let fixture: ComponentFixture<AvailableTriprequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailableTriprequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvailableTriprequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
