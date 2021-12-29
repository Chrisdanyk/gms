jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IGarage, Garage } from '../garage.model';
import { GarageService } from '../service/garage.service';

import { GarageRoutingResolveService } from './garage-routing-resolve.service';

describe('Garage routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GarageRoutingResolveService;
  let service: GarageService;
  let resultGarage: IGarage | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(GarageRoutingResolveService);
    service = TestBed.inject(GarageService);
    resultGarage = undefined;
  });

  describe('resolve', () => {
    it('should return IGarage returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGarage = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGarage).toEqual({ id: 123 });
    });

    it('should return new IGarage if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGarage = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGarage).toEqual(new Garage());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Garage })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGarage = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGarage).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
