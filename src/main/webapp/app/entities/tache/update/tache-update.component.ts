import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITache, Tache } from '../tache.model';
import { TacheService } from '../service/tache.service';
import { IGarage } from 'app/entities/garage/garage.model';
import { GarageService } from 'app/entities/garage/service/garage.service';

@Component({
  selector: 'jhi-tache-update',
  templateUrl: './tache-update.component.html',
})
export class TacheUpdateComponent implements OnInit {
  isSaving = false;

  garagesSharedCollection: IGarage[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    prixUnitaire: [null, [Validators.required]],
    description: [],
    disponible: [null, [Validators.required]],
    nuid: [null, [Validators.required]],
    garage: [],
  });

  constructor(
    protected tacheService: TacheService,
    protected garageService: GarageService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tache }) => {
      this.updateForm(tache);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tache = this.createFromForm();
    if (tache.id !== undefined) {
      this.subscribeToSaveResponse(this.tacheService.update(tache));
    } else {
      this.subscribeToSaveResponse(this.tacheService.create(tache));
    }
  }

  trackGarageById(index: number, item: IGarage): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITache>>): void {
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

  protected updateForm(tache: ITache): void {
    this.editForm.patchValue({
      id: tache.id,
      nom: tache.nom,
      prixUnitaire: tache.prixUnitaire,
      description: tache.description,
      disponible: tache.disponible,
      nuid: tache.nuid,
      garage: tache.garage,
    });

    this.garagesSharedCollection = this.garageService.addGarageToCollectionIfMissing(this.garagesSharedCollection, tache.garage);
  }

  protected loadRelationshipsOptions(): void {
    this.garageService
      .query()
      .pipe(map((res: HttpResponse<IGarage[]>) => res.body ?? []))
      .pipe(map((garages: IGarage[]) => this.garageService.addGarageToCollectionIfMissing(garages, this.editForm.get('garage')!.value)))
      .subscribe((garages: IGarage[]) => (this.garagesSharedCollection = garages));
  }

  protected createFromForm(): ITache {
    return {
      ...new Tache(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      prixUnitaire: this.editForm.get(['prixUnitaire'])!.value,
      description: this.editForm.get(['description'])!.value,
      disponible: this.editForm.get(['disponible'])!.value,
      nuid: this.editForm.get(['nuid'])!.value,
      garage: this.editForm.get(['garage'])!.value,
    };
  }
}
