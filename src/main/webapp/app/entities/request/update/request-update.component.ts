import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RequestFormService, RequestFormGroup } from './request-form.service';
import { IRequest } from '../request.model';
import { RequestService } from '../service/request.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IMeet } from 'app/entities/meet/meet.model';
import { MeetService } from 'app/entities/meet/service/meet.service';

@Component({
  selector: 'jhi-request-update',
  templateUrl: './request-update.component.html',
})
export class RequestUpdateComponent implements OnInit {
  isSaving = false;
  request: IRequest | null = null;

  usersSharedCollection: IUser[] = [];
  meetsSharedCollection: IMeet[] = [];

  editForm: RequestFormGroup = this.requestFormService.createRequestFormGroup();

  constructor(
    protected requestService: RequestService,
    protected requestFormService: RequestFormService,
    protected userService: UserService,
    protected meetService: MeetService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareMeet = (o1: IMeet | null, o2: IMeet | null): boolean => this.meetService.compareMeet(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ request }) => {
      this.request = request;
      if (request) {
        this.updateForm(request);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const request = this.requestFormService.getRequest(this.editForm);
    if (request.id !== null) {
      this.subscribeToSaveResponse(this.requestService.update(request));
    } else {
      this.subscribeToSaveResponse(this.requestService.create(request));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRequest>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(request: IRequest): void {
    this.request = request;
    this.requestFormService.resetForm(this.editForm, request);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, request.user);
    this.meetsSharedCollection = this.meetService.addMeetToCollectionIfMissing<IMeet>(this.meetsSharedCollection, request.meet);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.request?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.meetService
      .query()
      .pipe(map((res: HttpResponse<IMeet[]>) => res.body ?? []))
      .pipe(map((meets: IMeet[]) => this.meetService.addMeetToCollectionIfMissing<IMeet>(meets, this.request?.meet)))
      .subscribe((meets: IMeet[]) => (this.meetsSharedCollection = meets));
  }
}
