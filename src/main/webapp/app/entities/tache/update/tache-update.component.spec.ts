jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TacheService } from '../service/tache.service';
import { ITache, Tache } from '../tache.model';
import { IGarage } from 'app/entities/garage/garage.model';
import { GarageService } from 'app/entities/garage/service/garage.service';

import { TacheUpdateComponent } from './tache-update.component';

describe('Tache Management Update Component', () => {
  let comp: TacheUpdateComponent;
  let fixture: ComponentFixture<TacheUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tacheService: TacheService;
  let garageService: GarageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TacheUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(TacheUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TacheUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tacheService = TestBed.inject(TacheService);
    garageService = TestBed.inject(GarageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Garage query and add missing value', () => {
      const tache: ITache = { id: 456 };
      const garage: IGarage = { id: 33820 };
      tache.garage = garage;

      const garageCollection: IGarage[] = [{ id: 58897 }];
      jest.spyOn(garageService, 'query').mockReturnValue(of(new HttpResponse({ body: garageCollection })));
      const additionalGarages = [garage];
      const expectedCollection: IGarage[] = [...additionalGarages, ...garageCollection];
      jest.spyOn(garageService, 'addGarageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(garageService.query).toHaveBeenCalled();
      expect(garageService.addGarageToCollectionIfMissing).toHaveBeenCalledWith(garageCollection, ...additionalGarages);
      expect(comp.garagesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tache: ITache = { id: 456 };
      const garage: IGarage = { id: 67132 };
      tache.garage = garage;

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tache));
      expect(comp.garagesSharedCollection).toContain(garage);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = { id: 123 };
      jest.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tache }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tacheService.update).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = new Tache();
      jest.spyOn(tacheService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tache }));
      saveSubject.complete();

      // THEN
      expect(tacheService.create).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = { id: 123 };
      jest.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tacheService.update).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackGarageById', () => {
      it('Should return tracked Garage primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGarageById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
