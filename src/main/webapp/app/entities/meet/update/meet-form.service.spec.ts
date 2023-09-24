import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../meet.test-samples';

import { MeetFormService } from './meet-form.service';

describe('Meet Form Service', () => {
  let service: MeetFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MeetFormService);
  });

  describe('Service methods', () => {
    describe('createMeetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMeetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IMeet should create a new form with FormGroup', () => {
        const formGroup = service.createMeetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getMeet', () => {
      it('should return NewMeet for default Meet initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createMeetFormGroup(sampleWithNewData);

        const meet = service.getMeet(formGroup) as any;

        expect(meet).toMatchObject(sampleWithNewData);
      });

      it('should return NewMeet for empty Meet initial value', () => {
        const formGroup = service.createMeetFormGroup();

        const meet = service.getMeet(formGroup) as any;

        expect(meet).toMatchObject({});
      });

      it('should return IMeet', () => {
        const formGroup = service.createMeetFormGroup(sampleWithRequiredData);

        const meet = service.getMeet(formGroup) as any;

        expect(meet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMeet should not enable id FormControl', () => {
        const formGroup = service.createMeetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMeet should disable id FormControl', () => {
        const formGroup = service.createMeetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
