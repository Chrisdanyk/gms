import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITache, getTacheIdentifier } from '../tache.model';

export type EntityResponseType = HttpResponse<ITache>;
export type EntityArrayResponseType = HttpResponse<ITache[]>;

@Injectable({ providedIn: 'root' })
export class TacheService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/taches');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tache: ITache): Observable<EntityResponseType> {
    return this.http.post<ITache>(this.resourceUrl, tache, { observe: 'response' });
  }

  update(tache: ITache): Observable<EntityResponseType> {
    return this.http.put<ITache>(`${this.resourceUrl}/${getTacheIdentifier(tache) as number}`, tache, { observe: 'response' });
  }

  partialUpdate(tache: ITache): Observable<EntityResponseType> {
    return this.http.patch<ITache>(`${this.resourceUrl}/${getTacheIdentifier(tache) as number}`, tache, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITache>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITache[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTacheToCollectionIfMissing(tacheCollection: ITache[], ...tachesToCheck: (ITache | null | undefined)[]): ITache[] {
    const taches: ITache[] = tachesToCheck.filter(isPresent);
    if (taches.length > 0) {
      const tacheCollectionIdentifiers = tacheCollection.map(tacheItem => getTacheIdentifier(tacheItem)!);
      const tachesToAdd = taches.filter(tacheItem => {
        const tacheIdentifier = getTacheIdentifier(tacheItem);
        if (tacheIdentifier == null || tacheCollectionIdentifiers.includes(tacheIdentifier)) {
          return false;
        }
        tacheCollectionIdentifiers.push(tacheIdentifier);
        return true;
      });
      return [...tachesToAdd, ...tacheCollection];
    }
    return tacheCollection;
  }
}
