import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEngin } from '../engin.model';
import { EnginService } from '../service/engin.service';

@Component({
  templateUrl: './engin-delete-dialog.component.html',
})
export class EnginDeleteDialogComponent {
  engin?: IEngin;

  constructor(protected enginService: EnginService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.enginService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
