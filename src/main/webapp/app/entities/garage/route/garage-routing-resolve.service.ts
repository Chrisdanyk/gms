import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGarage, Garage } from '../garage.model';
import { GarageService } from '../service/garage.service';

@Injectable({ providedIn: 'root' })
export class GarageRoutingResolveService implements Resolve<IGarage> {
  constructor(protected service: GarageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGarage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((garage: HttpResponse<Garage>) => {
          if (garage.body) {
            return of(garage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Garage());
  }
}
