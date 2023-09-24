import { IMeet, NewMeet } from './meet.model';

export const sampleWithRequiredData: IMeet = {
  id: 56322,
  description: 'Seamless Fantastic up',
};

export const sampleWithPartialData: IMeet = {
  id: 41395,
  description: 'Sports virtual',
};

export const sampleWithFullData: IMeet = {
  id: 80054,
  description: 'turquoise Functionality',
};

export const sampleWithNewData: NewMeet = {
  description: 'JBOD Gorgeous Borders',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
