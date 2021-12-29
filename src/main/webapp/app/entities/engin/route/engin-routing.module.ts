import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnginComponent } from '../list/engin.component';
import { EnginDetailComponent } from '../detail/engin-detail.component';
import { EnginUpdateComponent } from '../update/engin-update.component';
import { EnginRoutingResolveService } from './engin-routing-resolve.service';

const enginRoute: Routes = [
  {
    path: '',
    component: EnginComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnginDetailComponent,
    resolve: {
      engin: EnginRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnginUpdateComponent,
    resolve: {
      engin: EnginRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnginUpdateComponent,
    resolve: {
      engin: EnginRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(enginRoute)],
  exports: [RouterModule],
})
export class EnginRoutingModule {}
