import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { Type } from 'app/entities/enumerations/type.model';
import { IEngin, Engin } from '../engin.model';

import { EnginService } from './engin.service';

describe('Engin Service', () => {
  let service: EnginService;
  let httpMock: HttpTestingController;
  let elemDefault: IEngin;
  let expectedResult: IEngin | IEngin[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnginService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      modele: 'AAAAAAA',
      plaque: 'AAAAAAA',
      dateFabrication: currentDate,
      type: Type.VOITURE,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateFabrication: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Engin', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateFabrication: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateFabrication: currentDate,
        },
        returnedFromService
      );

      service.create(new Engin()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Engin', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          modele: 'BBBBBB',
          plaque: 'BBBBBB',
          dateFabrication: currentDate.format(DATE_FORMAT),
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateFabrication: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Engin', () => {
      const patchObject = Object.assign(
        {
          modele: 'BBBBBB',
          dateFabrication: currentDate.format(DATE_FORMAT),
        },
        new Engin()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateFabrication: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Engin', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          modele: 'BBBBBB',
          plaque: 'BBBBBB',
          dateFabrication: currentDate.format(DATE_FORMAT),
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateFabrication: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Engin', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEnginToCollectionIfMissing', () => {
      it('should add a Engin to an empty array', () => {
        const engin: IEngin = { id: 123 };
        expectedResult = service.addEnginToCollectionIfMissing([], engin);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(engin);
      });

      it('should not add a Engin to an array that contains it', () => {
        const engin: IEngin = { id: 123 };
        const enginCollection: IEngin[] = [
          {
            ...engin,
          },
          { id: 456 },
        ];
        expectedResult = service.addEnginToCollectionIfMissing(enginCollection, engin);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Engin to an array that doesn't contain it", () => {
        const engin: IEngin = { id: 123 };
        const enginCollection: IEngin[] = [{ id: 456 }];
        expectedResult = service.addEnginToCollectionIfMissing(enginCollection, engin);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(engin);
      });

      it('should add only unique Engin to an array', () => {
        const enginArray: IEngin[] = [{ id: 123 }, { id: 456 }, { id: 81926 }];
        const enginCollection: IEngin[] = [{ id: 123 }];
        expectedResult = service.addEnginToCollectionIfMissing(enginCollection, ...enginArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const engin: IEngin = { id: 123 };
        const engin2: IEngin = { id: 456 };
        expectedResult = service.addEnginToCollectionIfMissing([], engin, engin2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(engin);
        expect(expectedResult).toContain(engin2);
      });

      it('should accept null and undefined values', () => {
        const engin: IEngin = { id: 123 };
        expectedResult = service.addEnginToCollectionIfMissing([], null, engin, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(engin);
      });

      it('should return initial array if no Engin is added', () => {
        const enginCollection: IEngin[] = [{ id: 123 }];
        expectedResult = service.addEnginToCollectionIfMissing(enginCollection, undefined, null);
        expect(expectedResult).toEqual(enginCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
