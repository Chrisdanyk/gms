import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MaintenanceComponent } from '../list/maintenance.component';
import { MaintenanceDetailComponent } from '../detail/maintenance-detail.component';
import { MaintenanceUpdateComponent } from '../update/maintenance-update.component';
import { MaintenanceRoutingResolveService } from './maintenance-routing-resolve.service';

const maintenanceRoute: Routes = [
  {
    path: '',
    component: MaintenanceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MaintenanceDetailComponent,
    resolve: {
      maintenance: MaintenanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MaintenanceUpdateComponent,
    resolve: {
      maintenance: MaintenanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MaintenanceUpdateComponent,
    resolve: {
      maintenance: MaintenanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(maintenanceRoute)],
  exports: [RouterModule],
})
export class MaintenanceRoutingModule {}
