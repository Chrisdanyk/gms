jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MaintenanceService } from '../service/maintenance.service';
import { IMaintenance, Maintenance } from '../maintenance.model';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';
import { IEngin } from 'app/entities/engin/engin.model';
import { EnginService } from 'app/entities/engin/service/engin.service';
import { IOperation } from 'app/entities/operation/operation.model';
import { OperationService } from 'app/entities/operation/service/operation.service';

import { MaintenanceUpdateComponent } from './maintenance-update.component';

describe('Maintenance Management Update Component', () => {
  let comp: MaintenanceUpdateComponent;
  let fixture: ComponentFixture<MaintenanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let maintenanceService: MaintenanceService;
  let notificationService: NotificationService;
  let enginService: EnginService;
  let operationService: OperationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MaintenanceUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(MaintenanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MaintenanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    maintenanceService = TestBed.inject(MaintenanceService);
    notificationService = TestBed.inject(NotificationService);
    enginService = TestBed.inject(EnginService);
    operationService = TestBed.inject(OperationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call notification query and add missing value', () => {
      const maintenance: IMaintenance = { id: 456 };
      const notification: INotification = { id: 73146 };
      maintenance.notification = notification;

      const notificationCollection: INotification[] = [{ id: 43103 }];
      jest.spyOn(notificationService, 'query').mockReturnValue(of(new HttpResponse({ body: notificationCollection })));
      const expectedCollection: INotification[] = [notification, ...notificationCollection];
      jest.spyOn(notificationService, 'addNotificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      expect(notificationService.query).toHaveBeenCalled();
      expect(notificationService.addNotificationToCollectionIfMissing).toHaveBeenCalledWith(notificationCollection, notification);
      expect(comp.notificationsCollection).toEqual(expectedCollection);
    });

    it('Should call Engin query and add missing value', () => {
      const maintenance: IMaintenance = { id: 456 };
      const engin: IEngin = { id: 50219 };
      maintenance.engin = engin;

      const enginCollection: IEngin[] = [{ id: 57455 }];
      jest.spyOn(enginService, 'query').mockReturnValue(of(new HttpResponse({ body: enginCollection })));
      const additionalEngins = [engin];
      const expectedCollection: IEngin[] = [...additionalEngins, ...enginCollection];
      jest.spyOn(enginService, 'addEnginToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      expect(enginService.query).toHaveBeenCalled();
      expect(enginService.addEnginToCollectionIfMissing).toHaveBeenCalledWith(enginCollection, ...additionalEngins);
      expect(comp.enginsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Operation query and add missing value', () => {
      const maintenance: IMaintenance = { id: 456 };
      const operations: IOperation[] = [{ id: 34861 }];
      maintenance.operations = operations;

      const operationCollection: IOperation[] = [{ id: 83030 }];
      jest.spyOn(operationService, 'query').mockReturnValue(of(new HttpResponse({ body: operationCollection })));
      const additionalOperations = [...operations];
      const expectedCollection: IOperation[] = [...additionalOperations, ...operationCollection];
      jest.spyOn(operationService, 'addOperationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      expect(operationService.query).toHaveBeenCalled();
      expect(operationService.addOperationToCollectionIfMissing).toHaveBeenCalledWith(operationCollection, ...additionalOperations);
      expect(comp.operationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const maintenance: IMaintenance = { id: 456 };
      const notification: INotification = { id: 40924 };
      maintenance.notification = notification;
      const engin: IEngin = { id: 19348 };
      maintenance.engin = engin;
      const operations: IOperation = { id: 11678 };
      maintenance.operations = [operations];

      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(maintenance));
      expect(comp.notificationsCollection).toContain(notification);
      expect(comp.enginsSharedCollection).toContain(engin);
      expect(comp.operationsSharedCollection).toContain(operations);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Maintenance>>();
      const maintenance = { id: 123 };
      jest.spyOn(maintenanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: maintenance }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(maintenanceService.update).toHaveBeenCalledWith(maintenance);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Maintenance>>();
      const maintenance = new Maintenance();
      jest.spyOn(maintenanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: maintenance }));
      saveSubject.complete();

      // THEN
      expect(maintenanceService.create).toHaveBeenCalledWith(maintenance);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Maintenance>>();
      const maintenance = { id: 123 };
      jest.spyOn(maintenanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ maintenance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(maintenanceService.update).toHaveBeenCalledWith(maintenance);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackNotificationById', () => {
      it('Should return tracked Notification primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackNotificationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackEnginById', () => {
      it('Should return tracked Engin primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEnginById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackOperationById', () => {
      it('Should return tracked Operation primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackOperationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedOperation', () => {
      it('Should return option if no Operation is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedOperation(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Operation for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedOperation(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Operation is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedOperation(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
