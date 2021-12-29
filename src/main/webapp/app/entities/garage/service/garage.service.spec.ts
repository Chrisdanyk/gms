import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGarage, Garage } from '../garage.model';

import { GarageService } from './garage.service';

describe('Garage Service', () => {
  let service: GarageService;
  let httpMock: HttpTestingController;
  let elemDefault: IGarage;
  let expectedResult: IGarage | IGarage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GarageService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      addresse: 'AAAAAAA',
      email: 'AAAAAAA',
      telephone: 'AAAAAAA',
      rccm: 'AAAAAAA',
      url: 'AAAAAAA',
      nuid: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Garage', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Garage()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Garage', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          addresse: 'BBBBBB',
          email: 'BBBBBB',
          telephone: 'BBBBBB',
          rccm: 'BBBBBB',
          url: 'BBBBBB',
          nuid: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Garage', () => {
      const patchObject = Object.assign(
        {
          telephone: 'BBBBBB',
          rccm: 'BBBBBB',
          url: 'BBBBBB',
          nuid: 'BBBBBB',
        },
        new Garage()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Garage', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          addresse: 'BBBBBB',
          email: 'BBBBBB',
          telephone: 'BBBBBB',
          rccm: 'BBBBBB',
          url: 'BBBBBB',
          nuid: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Garage', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGarageToCollectionIfMissing', () => {
      it('should add a Garage to an empty array', () => {
        const garage: IGarage = { id: 123 };
        expectedResult = service.addGarageToCollectionIfMissing([], garage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(garage);
      });

      it('should not add a Garage to an array that contains it', () => {
        const garage: IGarage = { id: 123 };
        const garageCollection: IGarage[] = [
          {
            ...garage,
          },
          { id: 456 },
        ];
        expectedResult = service.addGarageToCollectionIfMissing(garageCollection, garage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Garage to an array that doesn't contain it", () => {
        const garage: IGarage = { id: 123 };
        const garageCollection: IGarage[] = [{ id: 456 }];
        expectedResult = service.addGarageToCollectionIfMissing(garageCollection, garage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(garage);
      });

      it('should add only unique Garage to an array', () => {
        const garageArray: IGarage[] = [{ id: 123 }, { id: 456 }, { id: 39511 }];
        const garageCollection: IGarage[] = [{ id: 123 }];
        expectedResult = service.addGarageToCollectionIfMissing(garageCollection, ...garageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const garage: IGarage = { id: 123 };
        const garage2: IGarage = { id: 456 };
        expectedResult = service.addGarageToCollectionIfMissing([], garage, garage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(garage);
        expect(expectedResult).toContain(garage2);
      });

      it('should accept null and undefined values', () => {
        const garage: IGarage = { id: 123 };
        expectedResult = service.addGarageToCollectionIfMissing([], null, garage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(garage);
      });

      it('should return initial array if no Garage is added', () => {
        const garageCollection: IGarage[] = [{ id: 123 }];
        expectedResult = service.addGarageToCollectionIfMissing(garageCollection, undefined, null);
        expect(expectedResult).toEqual(garageCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
