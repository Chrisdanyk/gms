import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEngin } from '../engin.model';

@Component({
  selector: 'jhi-engin-detail',
  templateUrl: './engin-detail.component.html',
})
export class EnginDetailComponent implements OnInit {
  engin: IEngin | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ engin }) => {
      this.engin = engin;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
