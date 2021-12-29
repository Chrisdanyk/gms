import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMaintenance } from '../maintenance.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-maintenance-detail',
  templateUrl: './maintenance-detail.component.html',
})
export class MaintenanceDetailComponent implements OnInit {
  maintenance: IMaintenance | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ maintenance }) => {
      this.maintenance = maintenance;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
