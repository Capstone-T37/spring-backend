import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMeet, NewMeet } from '../meet.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMeet for edit and NewMeetFormGroupInput for create.
 */
type MeetFormGroupInput = IMeet | PartialWithRequiredKeyOf<NewMeet>;

type MeetFormDefaults = Pick<NewMeet, 'id'>;

type MeetFormGroupContent = {
  id: FormControl<IMeet['id'] | NewMeet['id']>;
  description: FormControl<IMeet['description']>;
  user: FormControl<IMeet['user']>;
};

export type MeetFormGroup = FormGroup<MeetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MeetFormService {
  createMeetFormGroup(meet: MeetFormGroupInput = { id: null }): MeetFormGroup {
    const meetRawValue = {
      ...this.getFormDefaults(),
      ...meet,
    };
    return new FormGroup<MeetFormGroupContent>({
      id: new FormControl(
        { value: meetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      description: new FormControl(meetRawValue.description, {
        validators: [Validators.required],
      }),
      user: new FormControl(meetRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getMeet(form: MeetFormGroup): IMeet | NewMeet {
    return form.getRawValue() as IMeet | NewMeet;
  }

  resetForm(form: MeetFormGroup, meet: MeetFormGroupInput): void {
    const meetRawValue = { ...this.getFormDefaults(), ...meet };
    form.reset(
      {
        ...meetRawValue,
        id: { value: meetRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MeetFormDefaults {
    return {
      id: null,
    };
  }
}
