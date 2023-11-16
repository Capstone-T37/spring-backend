import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRequest, NewRequest } from '../request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRequest for edit and NewRequestFormGroupInput for create.
 */
type RequestFormGroupInput = IRequest | PartialWithRequiredKeyOf<NewRequest>;

type RequestFormDefaults = Pick<NewRequest, 'id'>;

type RequestFormGroupContent = {
  id: FormControl<IRequest['id'] | NewRequest['id']>;
  user: FormControl<IRequest['user']>;
  meet: FormControl<IRequest['meet']>;
};

export type RequestFormGroup = FormGroup<RequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RequestFormService {
  createRequestFormGroup(request: RequestFormGroupInput = { id: null }): RequestFormGroup {
    const requestRawValue = {
      ...this.getFormDefaults(),
      ...request,
    };
    return new FormGroup<RequestFormGroupContent>({
      id: new FormControl(
        { value: requestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      user: new FormControl(requestRawValue.user, {
        validators: [Validators.required],
      }),
      meet: new FormControl(requestRawValue.meet, {
        validators: [Validators.required],
      }),
    });
  }

  getRequest(form: RequestFormGroup): IRequest | NewRequest {
    return form.getRawValue() as IRequest | NewRequest;
  }

  resetForm(form: RequestFormGroup, request: RequestFormGroupInput): void {
    const requestRawValue = { ...this.getFormDefaults(), ...request };
    form.reset(
      {
        ...requestRawValue,
        id: { value: requestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RequestFormDefaults {
    return {
      id: null,
    };
  }
}
