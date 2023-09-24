import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMeet } from '../meet.model';
import { MeetService } from '../service/meet.service';

@Injectable({ providedIn: 'root' })
export class MeetRoutingResolveService implements Resolve<IMeet | null> {
  constructor(protected service: MeetService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMeet | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((meet: HttpResponse<IMeet>) => {
          if (meet.body) {
            return of(meet.body);
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
