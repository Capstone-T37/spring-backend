import { IActivityTag, NewActivityTag } from './activity-tag.model';

export const sampleWithRequiredData: IActivityTag = {
  id: 9170,
};

export const sampleWithPartialData: IActivityTag = {
  id: 15107,
};

export const sampleWithFullData: IActivityTag = {
  id: 74025,
};

export const sampleWithNewData: NewActivityTag = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
