jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IEngin, Engin } from '../engin.model';
import { EnginService } from '../service/engin.service';

import { EnginRoutingResolveService } from './engin-routing-resolve.service';

describe('Engin routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: EnginRoutingResolveService;
  let service: EnginService;
  let resultEngin: IEngin | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(EnginRoutingResolveService);
    service = TestBed.inject(EnginService);
    resultEngin = undefined;
  });

  describe('resolve', () => {
    it('should return IEngin returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEngin = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultEngin).toEqual({ id: 123 });
    });

    it('should return new IEngin if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEngin = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultEngin).toEqual(new Engin());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Engin })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEngin = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultEngin).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
