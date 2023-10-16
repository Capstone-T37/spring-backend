import { IMeet, NewMeet } from './meet.model';

export const sampleWithRequiredData: IMeet = {
  id: 56322,
  description: 'Seamless Fantastic up',
};

export const sampleWithPartialData: IMeet = {
  id: 49869,
  description: 'SMS',
};

export const sampleWithFullData: IMeet = {
  id: 33261,
  description: 'online',
  isEnabled: false,
};

export const sampleWithNewData: NewMeet = {
  description: 'Functionality Human leading-edge',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
