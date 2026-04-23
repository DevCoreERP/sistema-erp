import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NominasHome } from './nominas-home';

describe('NominasHome', () => {
  let component: NominasHome;
  let fixture: ComponentFixture<NominasHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NominasHome],
    }).compileComponents();

    fixture = TestBed.createComponent(NominasHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
