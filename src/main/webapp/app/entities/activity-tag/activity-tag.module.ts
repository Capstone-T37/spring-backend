import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ActivityTagComponent } from './list/activity-tag.component';
import { ActivityTagDetailComponent } from './detail/activity-tag-detail.component';
import { ActivityTagUpdateComponent } from './update/activity-tag-update.component';
import { ActivityTagDeleteDialogComponent } from './delete/activity-tag-delete-dialog.component';
import { ActivityTagRoutingModule } from './route/activity-tag-routing.module';

@NgModule({
  imports: [SharedModule, ActivityTagRoutingModule],
  declarations: [ActivityTagComponent, ActivityTagDetailComponent, ActivityTagUpdateComponent, ActivityTagDeleteDialogComponent],
})
export class ActivityTagModule {}
