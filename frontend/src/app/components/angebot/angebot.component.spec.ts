import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AngebotComponent } from './angebot.component';

describe('AngebotComponent', () => {
  let component: AngebotComponent;
  let fixture: ComponentFixture<AngebotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AngebotComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AngebotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
