import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMaintenance, Maintenance } from '../maintenance.model';
import { MaintenanceService } from '../service/maintenance.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';
import { IEngin } from 'app/entities/engin/engin.model';
import { EnginService } from 'app/entities/engin/service/engin.service';
import { IOperation } from 'app/entities/operation/operation.model';
import { OperationService } from 'app/entities/operation/service/operation.service';

@Component({
  selector: 'jhi-maintenance-update',
  templateUrl: './maintenance-update.component.html',
})
export class MaintenanceUpdateComponent implements OnInit {
  isSaving = false;

  notificationsCollection: INotification[] = [];
  enginsSharedCollection: IEngin[] = [];
  operationsSharedCollection: IOperation[] = [];

  editForm = this.fb.group({
    id: [],
    dateDebut: [null, [Validators.required]],
    dateFin: [null, [Validators.required]],
    rapportGlobal: [],
    prixTotal: [null, [Validators.required]],
    discountTotal: [],
    nuid: [null, [Validators.required]],
    notification: [],
    engin: [],
    operations: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected maintenanceService: MaintenanceService,
    protected notificationService: NotificationService,
    protected enginService: EnginService,
    protected operationService: OperationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ maintenance }) => {
      this.updateForm(maintenance);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('gmsApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const maintenance = this.createFromForm();
    if (maintenance.id !== undefined) {
      this.subscribeToSaveResponse(this.maintenanceService.update(maintenance));
    } else {
      this.subscribeToSaveResponse(this.maintenanceService.create(maintenance));
    }
  }

  trackNotificationById(index: number, item: INotification): number {
    return item.id!;
  }

  trackEnginById(index: number, item: IEngin): number {
    return item.id!;
  }

  trackOperationById(index: number, item: IOperation): number {
    return item.id!;
  }

  getSelectedOperation(option: IOperation, selectedVals?: IOperation[]): IOperation {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMaintenance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(maintenance: IMaintenance): void {
    this.editForm.patchValue({
      id: maintenance.id,
      dateDebut: maintenance.dateDebut,
      dateFin: maintenance.dateFin,
      rapportGlobal: maintenance.rapportGlobal,
      prixTotal: maintenance.prixTotal,
      discountTotal: maintenance.discountTotal,
      nuid: maintenance.nuid,
      notification: maintenance.notification,
      engin: maintenance.engin,
      operations: maintenance.operations,
    });

    this.notificationsCollection = this.notificationService.addNotificationToCollectionIfMissing(
      this.notificationsCollection,
      maintenance.notification
    );
    this.enginsSharedCollection = this.enginService.addEnginToCollectionIfMissing(this.enginsSharedCollection, maintenance.engin);
    this.operationsSharedCollection = this.operationService.addOperationToCollectionIfMissing(
      this.operationsSharedCollection,
      ...(maintenance.operations ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.notificationService
      .query({ filter: 'maintenance-is-null' })
      .pipe(map((res: HttpResponse<INotification[]>) => res.body ?? []))
      .pipe(
        map((notifications: INotification[]) =>
          this.notificationService.addNotificationToCollectionIfMissing(notifications, this.editForm.get('notification')!.value)
        )
      )
      .subscribe((notifications: INotification[]) => (this.notificationsCollection = notifications));

    this.enginService
      .query()
      .pipe(map((res: HttpResponse<IEngin[]>) => res.body ?? []))
      .pipe(map((engins: IEngin[]) => this.enginService.addEnginToCollectionIfMissing(engins, this.editForm.get('engin')!.value)))
      .subscribe((engins: IEngin[]) => (this.enginsSharedCollection = engins));

    this.operationService
      .query()
      .pipe(map((res: HttpResponse<IOperation[]>) => res.body ?? []))
      .pipe(
        map((operations: IOperation[]) =>
          this.operationService.addOperationToCollectionIfMissing(operations, ...(this.editForm.get('operations')!.value ?? []))
        )
      )
      .subscribe((operations: IOperation[]) => (this.operationsSharedCollection = operations));
  }

  protected createFromForm(): IMaintenance {
    return {
      ...new Maintenance(),
      id: this.editForm.get(['id'])!.value,
      dateDebut: this.editForm.get(['dateDebut'])!.value,
      dateFin: this.editForm.get(['dateFin'])!.value,
      rapportGlobal: this.editForm.get(['rapportGlobal'])!.value,
      prixTotal: this.editForm.get(['prixTotal'])!.value,
      discountTotal: this.editForm.get(['discountTotal'])!.value,
      nuid: this.editForm.get(['nuid'])!.value,
      notification: this.editForm.get(['notification'])!.value,
      engin: this.editForm.get(['engin'])!.value,
      operations: this.editForm.get(['operations'])!.value,
    };
  }
}
