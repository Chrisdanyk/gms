import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEngin, getEnginIdentifier } from '../engin.model';

export type EntityResponseType = HttpResponse<IEngin>;
export type EntityArrayResponseType = HttpResponse<IEngin[]>;

@Injectable({ providedIn: 'root' })
export class EnginService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/engins');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(engin: IEngin): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(engin);
    return this.http
      .post<IEngin>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(engin: IEngin): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(engin);
    return this.http
      .put<IEngin>(`${this.resourceUrl}/${getEnginIdentifier(engin) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(engin: IEngin): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(engin);
    return this.http
      .patch<IEngin>(`${this.resourceUrl}/${getEnginIdentifier(engin) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEngin>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEngin[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEnginToCollectionIfMissing(enginCollection: IEngin[], ...enginsToCheck: (IEngin | null | undefined)[]): IEngin[] {
    const engins: IEngin[] = enginsToCheck.filter(isPresent);
    if (engins.length > 0) {
      const enginCollectionIdentifiers = enginCollection.map(enginItem => getEnginIdentifier(enginItem)!);
      const enginsToAdd = engins.filter(enginItem => {
        const enginIdentifier = getEnginIdentifier(enginItem);
        if (enginIdentifier == null || enginCollectionIdentifiers.includes(enginIdentifier)) {
          return false;
        }
        enginCollectionIdentifiers.push(enginIdentifier);
        return true;
      });
      return [...enginsToAdd, ...enginCollection];
    }
    return enginCollection;
  }

  protected convertDateFromClient(engin: IEngin): IEngin {
    return Object.assign({}, engin, {
      dateFabrication: engin.dateFabrication?.isValid() ? engin.dateFabrication.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateFabrication = res.body.dateFabrication ? dayjs(res.body.dateFabrication) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((engin: IEngin) => {
        engin.dateFabrication = engin.dateFabrication ? dayjs(engin.dateFabrication) : undefined;
      });
    }
    return res;
  }
}
