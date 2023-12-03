import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ActivityTagFormService, ActivityTagFormGroup } from './activity-tag-form.service';
import { IActivityTag } from '../activity-tag.model';
import { ActivityTagService } from '../service/activity-tag.service';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-activity-tag-update',
  templateUrl: './activity-tag-update.component.html',
})
export class ActivityTagUpdateComponent implements OnInit {
  isSaving = false;
  activityTag: IActivityTag | null = null;

  tagsSharedCollection: ITag[] = [];
  activitiesSharedCollection: IActivity[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ActivityTagFormGroup = this.activityTagFormService.createActivityTagFormGroup();

  constructor(
    protected activityTagService: ActivityTagService,
    protected activityTagFormService: ActivityTagFormService,
    protected tagService: TagService,
    protected activityService: ActivityService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTag = (o1: ITag | null, o2: ITag | null): boolean => this.tagService.compareTag(o1, o2);

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activityTag }) => {
      this.activityTag = activityTag;
      if (activityTag) {
        this.updateForm(activityTag);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const activityTag = this.activityTagFormService.getActivityTag(this.editForm);
    if (activityTag.id !== null) {
      this.subscribeToSaveResponse(this.activityTagService.update(activityTag));
    } else {
      this.subscribeToSaveResponse(this.activityTagService.create(activityTag));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivityTag>>): void {
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

  protected updateForm(activityTag: IActivityTag): void {
    this.activityTag = activityTag;
    this.activityTagFormService.resetForm(this.editForm, activityTag);

    this.tagsSharedCollection = this.tagService.addTagToCollectionIfMissing<ITag>(this.tagsSharedCollection, activityTag.tag);
    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      activityTag.activity
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, activityTag.user);
  }

  protected loadRelationshipsOptions(): void {
    this.tagService
      .query()
      .pipe(map((res: HttpResponse<ITag[]>) => res.body ?? []))
      .pipe(map((tags: ITag[]) => this.tagService.addTagToCollectionIfMissing<ITag>(tags, this.activityTag?.tag)))
      .subscribe((tags: ITag[]) => (this.tagsSharedCollection = tags));

    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) =>
          this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, this.activityTag?.activity)
        )
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.activityTag?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
