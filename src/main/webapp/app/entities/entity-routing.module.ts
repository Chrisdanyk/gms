import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'engin',
        data: { pageTitle: 'gmsApp.engin.home.title' },
        loadChildren: () => import('./engin/engin.module').then(m => m.EnginModule),
      },
      {
        path: 'maintenance',
        data: { pageTitle: 'gmsApp.maintenance.home.title' },
        loadChildren: () => import('./maintenance/maintenance.module').then(m => m.MaintenanceModule),
      },
      {
        path: 'notification',
        data: { pageTitle: 'gmsApp.notification.home.title' },
        loadChildren: () => import('./notification/notification.module').then(m => m.NotificationModule),
      },
      {
        path: 'operation',
        data: { pageTitle: 'gmsApp.operation.home.title' },
        loadChildren: () => import('./operation/operation.module').then(m => m.OperationModule),
      },
      {
        path: 'tache',
        data: { pageTitle: 'gmsApp.tache.home.title' },
        loadChildren: () => import('./tache/tache.module').then(m => m.TacheModule),
      },
      {
        path: 'garage',
        data: { pageTitle: 'gmsApp.garage.home.title' },
        loadChildren: () => import('./garage/garage.module').then(m => m.GarageModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
