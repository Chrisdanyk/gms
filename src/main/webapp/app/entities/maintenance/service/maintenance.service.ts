import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMaintenance, getMaintenanceIdentifier } from '../maintenance.model';

export type EntityResponseType = HttpResponse<IMaintenance>;
export type EntityArrayResponseType = HttpResponse<IMaintenance[]>;

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/maintenances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(maintenance: IMaintenance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(maintenance);
    return this.http
      .post<IMaintenance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(maintenance: IMaintenance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(maintenance);
    return this.http
      .put<IMaintenance>(`${this.resourceUrl}/${getMaintenanceIdentifier(maintenance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(maintenance: IMaintenance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(maintenance);
    return this.http
      .patch<IMaintenance>(`${this.resourceUrl}/${getMaintenanceIdentifier(maintenance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IMaintenance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IMaintenance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMaintenanceToCollectionIfMissing(
    maintenanceCollection: IMaintenance[],
    ...maintenancesToCheck: (IMaintenance | null | undefined)[]
  ): IMaintenance[] {
    const maintenances: IMaintenance[] = maintenancesToCheck.filter(isPresent);
    if (maintenances.length > 0) {
      const maintenanceCollectionIdentifiers = maintenanceCollection.map(maintenanceItem => getMaintenanceIdentifier(maintenanceItem)!);
      const maintenancesToAdd = maintenances.filter(maintenanceItem => {
        const maintenanceIdentifier = getMaintenanceIdentifier(maintenanceItem);
        if (maintenanceIdentifier == null || maintenanceCollectionIdentifiers.includes(maintenanceIdentifier)) {
          return false;
        }
        maintenanceCollectionIdentifiers.push(maintenanceIdentifier);
        return true;
      });
      return [...maintenancesToAdd, ...maintenanceCollection];
    }
    return maintenanceCollection;
  }

  protected convertDateFromClient(maintenance: IMaintenance): IMaintenance {
    return Object.assign({}, maintenance, {
      dateDebut: maintenance.dateDebut?.isValid() ? maintenance.dateDebut.format(DATE_FORMAT) : undefined,
      dateFin: maintenance.dateFin?.isValid() ? maintenance.dateFin.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateDebut = res.body.dateDebut ? dayjs(res.body.dateDebut) : undefined;
      res.body.dateFin = res.body.dateFin ? dayjs(res.body.dateFin) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((maintenance: IMaintenance) => {
        maintenance.dateDebut = maintenance.dateDebut ? dayjs(maintenance.dateDebut) : undefined;
        maintenance.dateFin = maintenance.dateFin ? dayjs(maintenance.dateFin) : undefined;
      });
    }
    return res;
  }
}
