import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEngin, Engin } from '../engin.model';
import { EnginService } from '../service/engin.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { Type } from 'app/entities/enumerations/type.model';

@Component({
  selector: 'jhi-engin-update',
  templateUrl: './engin-update.component.html',
})
export class EnginUpdateComponent implements OnInit {
  isSaving = false;
  typeValues = Object.keys(Type);

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    modele: [],
    plaque: [null, [Validators.required]],
    dateFabrication: [],
    type: [null, [Validators.required]],
    proprietaire: [],
  });

  constructor(
    protected enginService: EnginService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ engin }) => {
      this.updateForm(engin);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const engin = this.createFromForm();
    if (engin.id !== undefined) {
      this.subscribeToSaveResponse(this.enginService.update(engin));
    } else {
      this.subscribeToSaveResponse(this.enginService.create(engin));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEngin>>): void {
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

  protected updateForm(engin: IEngin): void {
    this.editForm.patchValue({
      id: engin.id,
      modele: engin.modele,
      plaque: engin.plaque,
      dateFabrication: engin.dateFabrication,
      type: engin.type,
      proprietaire: engin.proprietaire,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, engin.proprietaire);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('proprietaire')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IEngin {
    return {
      ...new Engin(),
      id: this.editForm.get(['id'])!.value,
      modele: this.editForm.get(['modele'])!.value,
      plaque: this.editForm.get(['plaque'])!.value,
      dateFabrication: this.editForm.get(['dateFabrication'])!.value,
      type: this.editForm.get(['type'])!.value,
      proprietaire: this.editForm.get(['proprietaire'])!.value,
    };
  }
}
