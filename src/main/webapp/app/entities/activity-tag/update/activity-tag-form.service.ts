import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IActivityTag, NewActivityTag } from '../activity-tag.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IActivityTag for edit and NewActivityTagFormGroupInput for create.
 */
type ActivityTagFormGroupInput = IActivityTag | PartialWithRequiredKeyOf<NewActivityTag>;

type ActivityTagFormDefaults = Pick<NewActivityTag, 'id'>;

type ActivityTagFormGroupContent = {
  id: FormControl<IActivityTag['id'] | NewActivityTag['id']>;
  tag: FormControl<IActivityTag['tag']>;
  activity: FormControl<IActivityTag['activity']>;
  user: FormControl<IActivityTag['user']>;
};

export type ActivityTagFormGroup = FormGroup<ActivityTagFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ActivityTagFormService {
  createActivityTagFormGroup(activityTag: ActivityTagFormGroupInput = { id: null }): ActivityTagFormGroup {
    const activityTagRawValue = {
      ...this.getFormDefaults(),
      ...activityTag,
    };
    return new FormGroup<ActivityTagFormGroupContent>({
      id: new FormControl(
        { value: activityTagRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      tag: new FormControl(activityTagRawValue.tag, {
        validators: [Validators.required],
      }),
      activity: new FormControl(activityTagRawValue.activity, {
        validators: [Validators.required],
      }),
      user: new FormControl(activityTagRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getActivityTag(form: ActivityTagFormGroup): IActivityTag | NewActivityTag {
    return form.getRawValue() as IActivityTag | NewActivityTag;
  }

  resetForm(form: ActivityTagFormGroup, activityTag: ActivityTagFormGroupInput): void {
    const activityTagRawValue = { ...this.getFormDefaults(), ...activityTag };
    form.reset(
      {
        ...activityTagRawValue,
        id: { value: activityTagRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ActivityTagFormDefaults {
    return {
      id: null,
    };
  }
}
