import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGarage, getGarageIdentifier } from '../garage.model';

export type EntityResponseType = HttpResponse<IGarage>;
export type EntityArrayResponseType = HttpResponse<IGarage[]>;

@Injectable({ providedIn: 'root' })
export class GarageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/garages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(garage: IGarage): Observable<EntityResponseType> {
    return this.http.post<IGarage>(this.resourceUrl, garage, { observe: 'response' });
  }

  update(garage: IGarage): Observable<EntityResponseType> {
    return this.http.put<IGarage>(`${this.resourceUrl}/${getGarageIdentifier(garage) as number}`, garage, { observe: 'response' });
  }

  partialUpdate(garage: IGarage): Observable<EntityResponseType> {
    return this.http.patch<IGarage>(`${this.resourceUrl}/${getGarageIdentifier(garage) as number}`, garage, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGarage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGarage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGarageToCollectionIfMissing(garageCollection: IGarage[], ...garagesToCheck: (IGarage | null | undefined)[]): IGarage[] {
    const garages: IGarage[] = garagesToCheck.filter(isPresent);
    if (garages.length > 0) {
      const garageCollectionIdentifiers = garageCollection.map(garageItem => getGarageIdentifier(garageItem)!);
      const garagesToAdd = garages.filter(garageItem => {
        const garageIdentifier = getGarageIdentifier(garageItem);
        if (garageIdentifier == null || garageCollectionIdentifiers.includes(garageIdentifier)) {
          return false;
        }
        garageCollectionIdentifiers.push(garageIdentifier);
        return true;
      });
      return [...garagesToAdd, ...garageCollection];
    }
    return garageCollection;
  }
}
