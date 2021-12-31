import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITache, Tache } from '../tache.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { TacheService } from '../service/tache.service';
import { TacheDeleteDialogComponent } from '../delete/tache-delete-dialog.component';

@Component({
  selector: 'jhi-tache',
  templateUrl: './tache.component.html',
})
export class TacheComponent implements OnInit {
  filteredAndOrderedTaches?: ITache[];
  taches?: ITache[];
  filter = '';
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  // ascending!: boolean;
  ngbPaginationPage = 1;
  orderProp: keyof Tache = 'nom';
  ascending = true;

  constructor(
    protected tacheService: TacheService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.tacheService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<ITache[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          this.onError();
        }
      );
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  trackId(index: number, item: ITache): number {
    return item.id!;
  }

  delete(tache: ITache): void {
    const modalRef = this.modalService.open(TacheDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tache = tache;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  filterAndSort(): void {
    this.filteredAndOrderedTaches = this.taches!.filter(t => !this.filter || t.nom!.toLowerCase().includes(this.filter.toLowerCase()));
    if (typeof this.filteredAndOrderedTaches !== 'undefined') {
      if (this.filteredAndOrderedTaches.length > 0) {
        this.taches = this.filteredAndOrderedTaches;
      }
      if (this.filteredAndOrderedTaches.length === 0) {
        this.taches = [];
      }
    } else {
      this.loadPage();
    }
    if (!this.filter) {
      this.loadPage();
    }
  }

  public search(key: string): void {
    const results: ITache[] = [];
    for (const tache of this.taches!) {
      if (
        tache.nom?.toLowerCase().indexOf(key.toLowerCase()) !== -1 ||
        tache.nuid?.toLowerCase().indexOf(key.toLowerCase()) !== -1 ||
        tache.description?.toLowerCase().indexOf(key.toLowerCase()) !== -1 ||
        tache.garage?.nom?.toLowerCase().indexOf(key.toLowerCase()) !== -1
      ) {
        results.push(tache);
      }
    }
    console.error('results >>>', results.length);
    console.error('taches >>>', this.taches);

    this.taches = results;
    if (results.length === 0 || !key) {
      this.loadPage();
    }
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: ITache[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/tache'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.taches = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
