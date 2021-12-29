import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnginDetailComponent } from './engin-detail.component';

describe('Engin Management Detail Component', () => {
  let comp: EnginDetailComponent;
  let fixture: ComponentFixture<EnginDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnginDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ engin: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EnginDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EnginDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load engin on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.engin).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
