import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IActivity, NewActivity } from '../activity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IActivity for edit and NewActivityFormGroupInput for create.
 */
type ActivityFormGroupInput = IActivity | PartialWithRequiredKeyOf<NewActivity>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IActivity | NewActivity> = Omit<T, 'date'> & {
  date?: string | null;
};

type ActivityFormRawValue = FormValueOf<IActivity>;

type NewActivityFormRawValue = FormValueOf<NewActivity>;

type ActivityFormDefaults = Pick<NewActivity, 'id' | 'date'>;

type ActivityFormGroupContent = {
  id: FormControl<ActivityFormRawValue['id'] | NewActivity['id']>;
  title: FormControl<ActivityFormRawValue['title']>;
  description: FormControl<ActivityFormRawValue['description']>;
  date: FormControl<ActivityFormRawValue['date']>;
  user: FormControl<ActivityFormRawValue['user']>;
};

export type ActivityFormGroup = FormGroup<ActivityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ActivityFormService {
  createActivityFormGroup(activity: ActivityFormGroupInput = { id: null }): ActivityFormGroup {
    const activityRawValue = this.convertActivityToActivityRawValue({
      ...this.getFormDefaults(),
      ...activity,
    });
    return new FormGroup<ActivityFormGroupContent>({
      id: new FormControl(
        { value: activityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(activityRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(activityRawValue.description, {
        validators: [Validators.required],
      }),
      date: new FormControl(activityRawValue.date, {
        validators: [Validators.required],
      }),
      user: new FormControl(activityRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getActivity(form: ActivityFormGroup): IActivity | NewActivity {
    return this.convertActivityRawValueToActivity(form.getRawValue() as ActivityFormRawValue | NewActivityFormRawValue);
  }

  resetForm(form: ActivityFormGroup, activity: ActivityFormGroupInput): void {
    const activityRawValue = this.convertActivityToActivityRawValue({ ...this.getFormDefaults(), ...activity });
    form.reset(
      {
        ...activityRawValue,
        id: { value: activityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ActivityFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertActivityRawValueToActivity(rawActivity: ActivityFormRawValue | NewActivityFormRawValue): IActivity | NewActivity {
    return {
      ...rawActivity,
      date: dayjs(rawActivity.date, DATE_TIME_FORMAT),
    };
  }

  private convertActivityToActivityRawValue(
    activity: IActivity | (Partial<NewActivity> & ActivityFormDefaults)
  ): ActivityFormRawValue | PartialWithRequiredKeyOf<NewActivityFormRawValue> {
    return {
      ...activity,
      date: activity.date ? activity.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
