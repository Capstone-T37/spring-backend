import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'activity',
        data: { pageTitle: 'meetupbackendApp.activity.home.title' },
        loadChildren: () => import('./activity/activity.module').then(m => m.ActivityModule),
      },
      {
        path: 'meet',
        data: { pageTitle: 'meetupbackendApp.meet.home.title' },
        loadChildren: () => import('./meet/meet.module').then(m => m.MeetModule),
      },
      {
        path: 'request',
        data: { pageTitle: 'meetupbackendApp.request.home.title' },
        loadChildren: () => import('./request/request.module').then(m => m.RequestModule),
      },
      {
        path: 'participant',
        data: { pageTitle: 'meetupbackendApp.participant.home.title' },
        loadChildren: () => import('./participant/participant.module').then(m => m.ParticipantModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'meetupbackendApp.tag.home.title' },
        loadChildren: () => import('./tag/tag.module').then(m => m.TagModule),
      },
      {
        path: 'activity-tag',
        data: { pageTitle: 'meetupbackendApp.activityTag.home.title' },
        loadChildren: () => import('./activity-tag/activity-tag.module').then(m => m.ActivityTagModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
