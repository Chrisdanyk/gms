import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGarage } from '../garage.model';
import { GarageService } from '../service/garage.service';

@Component({
  templateUrl: './garage-delete-dialog.component.html',
})
export class GarageDeleteDialogComponent {
  garage?: IGarage;

  constructor(protected garageService: GarageService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.garageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
