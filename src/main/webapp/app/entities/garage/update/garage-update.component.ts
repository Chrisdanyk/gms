import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IGarage, Garage } from '../garage.model';
import { GarageService } from '../service/garage.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-garage-update',
  templateUrl: './garage-update.component.html',
})
export class GarageUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    addresse: [],
    email: [],
    telephone: [],
    rccm: [],
    url: [],
    nuid: [null, [Validators.required]],
    utilisateurs: [],
  });

  constructor(
    protected garageService: GarageService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ garage }) => {
      this.updateForm(garage);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const garage = this.createFromForm();
    if (garage.id !== undefined) {
      this.subscribeToSaveResponse(this.garageService.update(garage));
    } else {
      this.subscribeToSaveResponse(this.garageService.create(garage));
    }
  }

  trackUserById(index: number, item: IUser): number {
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGarage>>): void {
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

  protected updateForm(garage: IGarage): void {
    this.editForm.patchValue({
      id: garage.id,
      nom: garage.nom,
      addresse: garage.addresse,
      email: garage.email,
      telephone: garage.telephone,
      rccm: garage.rccm,
      url: garage.url,
      nuid: garage.nuid,
      utilisateurs: garage.utilisateurs,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, ...(garage.utilisateurs ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, ...(this.editForm.get('utilisateurs')!.value ?? [])))
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IGarage {
    return {
      ...new Garage(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      addresse: this.editForm.get(['addresse'])!.value,
      email: this.editForm.get(['email'])!.value,
      telephone: this.editForm.get(['telephone'])!.value,
      rccm: this.editForm.get(['rccm'])!.value,
      url: this.editForm.get(['url'])!.value,
      nuid: this.editForm.get(['nuid'])!.value,
      utilisateurs: this.editForm.get(['utilisateurs'])!.value,
    };
  }
}
