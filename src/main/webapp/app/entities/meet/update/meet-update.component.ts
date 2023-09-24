import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MeetFormService, MeetFormGroup } from './meet-form.service';
import { IMeet } from '../meet.model';
import { MeetService } from '../service/meet.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-meet-update',
  templateUrl: './meet-update.component.html',
})
export class MeetUpdateComponent implements OnInit {
  isSaving = false;
  meet: IMeet | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: MeetFormGroup = this.meetFormService.createMeetFormGroup();

  constructor(
    protected meetService: MeetService,
    protected meetFormService: MeetFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ meet }) => {
      this.meet = meet;
      if (meet) {
        this.updateForm(meet);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const meet = this.meetFormService.getMeet(this.editForm);
    if (meet.id !== null) {
      this.subscribeToSaveResponse(this.meetService.update(meet));
    } else {
      this.subscribeToSaveResponse(this.meetService.create(meet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMeet>>): void {
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

  protected updateForm(meet: IMeet): void {
    this.meet = meet;
    this.meetFormService.resetForm(this.editForm, meet);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, meet.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.meet?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
