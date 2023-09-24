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
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
