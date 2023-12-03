import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../activity-tag.test-samples';

import { ActivityTagFormService } from './activity-tag-form.service';

describe('ActivityTag Form Service', () => {
  let service: ActivityTagFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActivityTagFormService);
  });

  describe('Service methods', () => {
    describe('createActivityTagFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createActivityTagFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tag: expect.any(Object),
            activity: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IActivityTag should create a new form with FormGroup', () => {
        const formGroup = service.createActivityTagFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tag: expect.any(Object),
            activity: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getActivityTag', () => {
      it('should return NewActivityTag for default ActivityTag initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createActivityTagFormGroup(sampleWithNewData);

        const activityTag = service.getActivityTag(formGroup) as any;

        expect(activityTag).toMatchObject(sampleWithNewData);
      });

      it('should return NewActivityTag for empty ActivityTag initial value', () => {
        const formGroup = service.createActivityTagFormGroup();

        const activityTag = service.getActivityTag(formGroup) as any;

        expect(activityTag).toMatchObject({});
      });

      it('should return IActivityTag', () => {
        const formGroup = service.createActivityTagFormGroup(sampleWithRequiredData);

        const activityTag = service.getActivityTag(formGroup) as any;

        expect(activityTag).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IActivityTag should not enable id FormControl', () => {
        const formGroup = service.createActivityTagFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewActivityTag should disable id FormControl', () => {
        const formGroup = service.createActivityTagFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
