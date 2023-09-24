import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MeetComponent } from '../list/meet.component';
import { MeetDetailComponent } from '../detail/meet-detail.component';
import { MeetUpdateComponent } from '../update/meet-update.component';
import { MeetRoutingResolveService } from './meet-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const meetRoute: Routes = [
  {
    path: '',
    component: MeetComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MeetDetailComponent,
    resolve: {
      meet: MeetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MeetUpdateComponent,
    resolve: {
      meet: MeetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MeetUpdateComponent,
    resolve: {
      meet: MeetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(meetRoute)],
  exports: [RouterModule],
})
export class MeetRoutingModule {}
