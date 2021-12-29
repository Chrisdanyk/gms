import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEngin, Engin } from '../engin.model';
import { EnginService } from '../service/engin.service';

@Injectable({ providedIn: 'root' })
export class EnginRoutingResolveService implements Resolve<IEngin> {
  constructor(protected service: EnginService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEngin> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((engin: HttpResponse<Engin>) => {
          if (engin.body) {
            return of(engin.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Engin());
  }
}
