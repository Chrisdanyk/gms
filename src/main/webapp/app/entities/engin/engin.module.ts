import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnginComponent } from './list/engin.component';
import { EnginDetailComponent } from './detail/engin-detail.component';
import { EnginUpdateComponent } from './update/engin-update.component';
import { EnginDeleteDialogComponent } from './delete/engin-delete-dialog.component';
import { EnginRoutingModule } from './route/engin-routing.module';

@NgModule({
  imports: [SharedModule, EnginRoutingModule],
  declarations: [EnginComponent, EnginDetailComponent, EnginUpdateComponent, EnginDeleteDialogComponent],
  entryComponents: [EnginDeleteDialogComponent],
})
export class EnginModule {}
