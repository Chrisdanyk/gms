import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOperation, Operation } from '../operation.model';
import { OperationService } from '../service/operation.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ITache } from 'app/entities/tache/tache.model';
import { TacheService } from 'app/entities/tache/service/tache.service';

@Component({
  selector: 'jhi-operation-update',
  templateUrl: './operation-update.component.html',
})
export class OperationUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  tachesSharedCollection: ITache[] = [];

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    prix: [null, [Validators.required]],
    discount: [],
    nuid: [null, [Validators.required]],
    mecaniciens: [],
    taches: [],
  });

  constructor(
    protected operationService: OperationService,
    protected userService: UserService,
    protected tacheService: TacheService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ operation }) => {
      this.updateForm(operation);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const operation = this.createFromForm();
    if (operation.id !== undefined) {
      this.subscribeToSaveResponse(this.operationService.update(operation));
    } else {
      this.subscribeToSaveResponse(this.operationService.create(operation));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackTacheById(index: number, item: ITache): number {
    return item.id!;
  }

  getSelectedUser(option: IUser, selectedVals?: IUser[]): IUser {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedTache(option: ITache, selectedVals?: ITache[]): ITache {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOperation>>): void {
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

  protected updateForm(operation: IOperation): void {
    this.editForm.patchValue({
      id: operation.id,
      date: operation.date,
      prix: operation.prix,
      discount: operation.discount,
      nuid: operation.nuid,
      mecaniciens: operation.mecaniciens,
      taches: operation.taches,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(
      this.usersSharedCollection,
      ...(operation.mecaniciens ?? [])
    );
    this.tachesSharedCollection = this.tacheService.addTacheToCollectionIfMissing(this.tachesSharedCollection, ...(operation.taches ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, ...(this.editForm.get('mecaniciens')!.value ?? [])))
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.tacheService
      .query()
      .pipe(map((res: HttpResponse<ITache[]>) => res.body ?? []))
      .pipe(
        map((taches: ITache[]) => this.tacheService.addTacheToCollectionIfMissing(taches, ...(this.editForm.get('taches')!.value ?? [])))
      )
      .subscribe((taches: ITache[]) => (this.tachesSharedCollection = taches));
  }

  protected createFromForm(): IOperation {
    return {
      ...new Operation(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value,
      prix: this.editForm.get(['prix'])!.value,
      discount: this.editForm.get(['discount'])!.value,
      nuid: this.editForm.get(['nuid'])!.value,
      mecaniciens: this.editForm.get(['mecaniciens'])!.value,
      taches: this.editForm.get(['taches'])!.value,
    };
  }
}
