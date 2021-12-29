import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GarageComponent } from './list/garage.component';
import { GarageDetailComponent } from './detail/garage-detail.component';
import { GarageUpdateComponent } from './update/garage-update.component';
import { GarageDeleteDialogComponent } from './delete/garage-delete-dialog.component';
import { GarageRoutingModule } from './route/garage-routing.module';

@NgModule({
  imports: [SharedModule, GarageRoutingModule],
  declarations: [GarageComponent, GarageDetailComponent, GarageUpdateComponent, GarageDeleteDialogComponent],
  entryComponents: [GarageDeleteDialogComponent],
})
export class GarageModule {}
