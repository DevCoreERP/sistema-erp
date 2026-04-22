import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AsistenciaHome } from './asistencia-home';

describe('AsistenciaHome', () => {
  let component: AsistenciaHome;
  let fixture: ComponentFixture<AsistenciaHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AsistenciaHome],
    }).compileComponents();

    fixture = TestBed.createComponent(AsistenciaHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
