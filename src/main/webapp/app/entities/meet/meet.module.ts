import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MeetComponent } from './list/meet.component';
import { MeetDetailComponent } from './detail/meet-detail.component';
import { MeetUpdateComponent } from './update/meet-update.component';
import { MeetDeleteDialogComponent } from './delete/meet-delete-dialog.component';
import { MeetRoutingModule } from './route/meet-routing.module';

@NgModule({
  imports: [SharedModule, MeetRoutingModule],
  declarations: [MeetComponent, MeetDetailComponent, MeetUpdateComponent, MeetDeleteDialogComponent],
})
export class MeetModule {}
