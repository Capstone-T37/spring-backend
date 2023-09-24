import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMeet, NewMeet } from '../meet.model';

export type PartialUpdateMeet = Partial<IMeet> & Pick<IMeet, 'id'>;

export type EntityResponseType = HttpResponse<IMeet>;
export type EntityArrayResponseType = HttpResponse<IMeet[]>;

@Injectable({ providedIn: 'root' })
export class MeetService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/meets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(meet: NewMeet): Observable<EntityResponseType> {
    return this.http.post<IMeet>(this.resourceUrl, meet, { observe: 'response' });
  }

  update(meet: IMeet): Observable<EntityResponseType> {
    return this.http.put<IMeet>(`${this.resourceUrl}/${this.getMeetIdentifier(meet)}`, meet, { observe: 'response' });
  }

  partialUpdate(meet: PartialUpdateMeet): Observable<EntityResponseType> {
    return this.http.patch<IMeet>(`${this.resourceUrl}/${this.getMeetIdentifier(meet)}`, meet, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMeet>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMeet[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMeetIdentifier(meet: Pick<IMeet, 'id'>): number {
    return meet.id;
  }

  compareMeet(o1: Pick<IMeet, 'id'> | null, o2: Pick<IMeet, 'id'> | null): boolean {
    return o1 && o2 ? this.getMeetIdentifier(o1) === this.getMeetIdentifier(o2) : o1 === o2;
  }

  addMeetToCollectionIfMissing<Type extends Pick<IMeet, 'id'>>(
    meetCollection: Type[],
    ...meetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const meets: Type[] = meetsToCheck.filter(isPresent);
    if (meets.length > 0) {
      const meetCollectionIdentifiers = meetCollection.map(meetItem => this.getMeetIdentifier(meetItem)!);
      const meetsToAdd = meets.filter(meetItem => {
        const meetIdentifier = this.getMeetIdentifier(meetItem);
        if (meetCollectionIdentifiers.includes(meetIdentifier)) {
          return false;
        }
        meetCollectionIdentifiers.push(meetIdentifier);
        return true;
      });
      return [...meetsToAdd, ...meetCollection];
    }
    return meetCollection;
  }
}
