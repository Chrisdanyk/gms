jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMaintenance, Maintenance } from '../maintenance.model';
import { MaintenanceService } from '../service/maintenance.service';

import { MaintenanceRoutingResolveService } from './maintenance-routing-resolve.service';

describe('Maintenance routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: MaintenanceRoutingResolveService;
  let service: MaintenanceService;
  let resultMaintenance: IMaintenance | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(MaintenanceRoutingResolveService);
    service = TestBed.inject(MaintenanceService);
    resultMaintenance = undefined;
  });

  describe('resolve', () => {
    it('should return IMaintenance returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMaintenance = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMaintenance).toEqual({ id: 123 });
    });

    it('should return new IMaintenance if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMaintenance = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultMaintenance).toEqual(new Maintenance());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Maintenance })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMaintenance = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMaintenance).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
