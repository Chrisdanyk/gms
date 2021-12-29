import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IMaintenance, Maintenance } from '../maintenance.model';

import { MaintenanceService } from './maintenance.service';

describe('Maintenance Service', () => {
  let service: MaintenanceService;
  let httpMock: HttpTestingController;
  let elemDefault: IMaintenance;
  let expectedResult: IMaintenance | IMaintenance[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MaintenanceService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      dateDebut: currentDate,
      dateFin: currentDate,
      rapportGlobal: 'AAAAAAA',
      prixTotal: 0,
      discountTotal: 0,
      nuid: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Maintenance', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.create(new Maintenance()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Maintenance', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
          rapportGlobal: 'BBBBBB',
          prixTotal: 1,
          discountTotal: 1,
          nuid: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Maintenance', () => {
      const patchObject = Object.assign(
        {
          dateFin: currentDate.format(DATE_FORMAT),
          rapportGlobal: 'BBBBBB',
          discountTotal: 1,
          nuid: 'BBBBBB',
        },
        new Maintenance()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Maintenance', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          dateDebut: currentDate.format(DATE_FORMAT),
          dateFin: currentDate.format(DATE_FORMAT),
          rapportGlobal: 'BBBBBB',
          prixTotal: 1,
          discountTotal: 1,
          nuid: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Maintenance', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMaintenanceToCollectionIfMissing', () => {
      it('should add a Maintenance to an empty array', () => {
        const maintenance: IMaintenance = { id: 123 };
        expectedResult = service.addMaintenanceToCollectionIfMissing([], maintenance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(maintenance);
      });

      it('should not add a Maintenance to an array that contains it', () => {
        const maintenance: IMaintenance = { id: 123 };
        const maintenanceCollection: IMaintenance[] = [
          {
            ...maintenance,
          },
          { id: 456 },
        ];
        expectedResult = service.addMaintenanceToCollectionIfMissing(maintenanceCollection, maintenance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Maintenance to an array that doesn't contain it", () => {
        const maintenance: IMaintenance = { id: 123 };
        const maintenanceCollection: IMaintenance[] = [{ id: 456 }];
        expectedResult = service.addMaintenanceToCollectionIfMissing(maintenanceCollection, maintenance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(maintenance);
      });

      it('should add only unique Maintenance to an array', () => {
        const maintenanceArray: IMaintenance[] = [{ id: 123 }, { id: 456 }, { id: 51177 }];
        const maintenanceCollection: IMaintenance[] = [{ id: 123 }];
        expectedResult = service.addMaintenanceToCollectionIfMissing(maintenanceCollection, ...maintenanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const maintenance: IMaintenance = { id: 123 };
        const maintenance2: IMaintenance = { id: 456 };
        expectedResult = service.addMaintenanceToCollectionIfMissing([], maintenance, maintenance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(maintenance);
        expect(expectedResult).toContain(maintenance2);
      });

      it('should accept null and undefined values', () => {
        const maintenance: IMaintenance = { id: 123 };
        expectedResult = service.addMaintenanceToCollectionIfMissing([], null, maintenance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(maintenance);
      });

      it('should return initial array if no Maintenance is added', () => {
        const maintenanceCollection: IMaintenance[] = [{ id: 123 }];
        expectedResult = service.addMaintenanceToCollectionIfMissing(maintenanceCollection, undefined, null);
        expect(expectedResult).toEqual(maintenanceCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
