import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ActivityTagComponent } from '../list/activity-tag.component';
import { ActivityTagDetailComponent } from '../detail/activity-tag-detail.component';
import { ActivityTagUpdateComponent } from '../update/activity-tag-update.component';
import { ActivityTagRoutingResolveService } from './activity-tag-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const activityTagRoute: Routes = [
  {
    path: '',
    component: ActivityTagComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ActivityTagDetailComponent,
    resolve: {
      activityTag: ActivityTagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ActivityTagUpdateComponent,
    resolve: {
      activityTag: ActivityTagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ActivityTagUpdateComponent,
    resolve: {
      activityTag: ActivityTagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(activityTagRoute)],
  exports: [RouterModule],
})
export class ActivityTagRoutingModule {}
