import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GarageDetailComponent } from './garage-detail.component';

describe('Garage Management Detail Component', () => {
  let comp: GarageDetailComponent;
  let fixture: ComponentFixture<GarageDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GarageDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ garage: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GarageDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GarageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load garage on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.garage).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
