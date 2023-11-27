import { ITag, NewTag } from './tag.model';

export const sampleWithRequiredData: ITag = {
  id: 42372,
  title: 'human-resource',
};

export const sampleWithPartialData: ITag = {
  id: 72152,
  title: 'Officer Sleek Extended',
};

export const sampleWithFullData: ITag = {
  id: 82371,
  title: 'Mongolia',
};

export const sampleWithNewData: NewTag = {
  title: 'heuristic next-generation',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
