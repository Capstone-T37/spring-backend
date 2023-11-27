import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IActivityTag } from '../activity-tag.model';
import { ActivityTagService } from '../service/activity-tag.service';

@Injectable({ providedIn: 'root' })
export class ActivityTagRoutingResolveService implements Resolve<IActivityTag | null> {
  constructor(protected service: ActivityTagService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IActivityTag | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((activityTag: HttpResponse<IActivityTag>) => {
          if (activityTag.body) {
            return of(activityTag.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
