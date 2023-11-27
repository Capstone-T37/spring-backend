import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IActivityTag, NewActivityTag } from '../activity-tag.model';

export type PartialUpdateActivityTag = Partial<IActivityTag> & Pick<IActivityTag, 'id'>;

export type EntityResponseType = HttpResponse<IActivityTag>;
export type EntityArrayResponseType = HttpResponse<IActivityTag[]>;

@Injectable({ providedIn: 'root' })
export class ActivityTagService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/activity-tags');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(activityTag: NewActivityTag): Observable<EntityResponseType> {
    return this.http.post<IActivityTag>(this.resourceUrl, activityTag, { observe: 'response' });
  }

  update(activityTag: IActivityTag): Observable<EntityResponseType> {
    return this.http.put<IActivityTag>(`${this.resourceUrl}/${this.getActivityTagIdentifier(activityTag)}`, activityTag, {
      observe: 'response',
    });
  }

  partialUpdate(activityTag: PartialUpdateActivityTag): Observable<EntityResponseType> {
    return this.http.patch<IActivityTag>(`${this.resourceUrl}/${this.getActivityTagIdentifier(activityTag)}`, activityTag, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IActivityTag>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IActivityTag[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getActivityTagIdentifier(activityTag: Pick<IActivityTag, 'id'>): number {
    return activityTag.id;
  }

  compareActivityTag(o1: Pick<IActivityTag, 'id'> | null, o2: Pick<IActivityTag, 'id'> | null): boolean {
    return o1 && o2 ? this.getActivityTagIdentifier(o1) === this.getActivityTagIdentifier(o2) : o1 === o2;
  }

  addActivityTagToCollectionIfMissing<Type extends Pick<IActivityTag, 'id'>>(
    activityTagCollection: Type[],
    ...activityTagsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const activityTags: Type[] = activityTagsToCheck.filter(isPresent);
    if (activityTags.length > 0) {
      const activityTagCollectionIdentifiers = activityTagCollection.map(
        activityTagItem => this.getActivityTagIdentifier(activityTagItem)!
      );
      const activityTagsToAdd = activityTags.filter(activityTagItem => {
        const activityTagIdentifier = this.getActivityTagIdentifier(activityTagItem);
        if (activityTagCollectionIdentifiers.includes(activityTagIdentifier)) {
          return false;
        }
        activityTagCollectionIdentifiers.push(activityTagIdentifier);
        return true;
      });
      return [...activityTagsToAdd, ...activityTagCollection];
    }
    return activityTagCollection;
  }
}
