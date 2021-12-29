import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GarageComponent } from '../list/garage.component';
import { GarageDetailComponent } from '../detail/garage-detail.component';
import { GarageUpdateComponent } from '../update/garage-update.component';
import { GarageRoutingResolveService } from './garage-routing-resolve.service';

const garageRoute: Routes = [
  {
    path: '',
    component: GarageComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GarageDetailComponent,
    resolve: {
      garage: GarageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GarageUpdateComponent,
    resolve: {
      garage: GarageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GarageUpdateComponent,
    resolve: {
      garage: GarageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(garageRoute)],
  exports: [RouterModule],
})
export class GarageRoutingModule {}
