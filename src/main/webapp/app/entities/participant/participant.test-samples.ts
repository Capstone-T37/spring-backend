import { IParticipant, NewParticipant } from './participant.model';

export const sampleWithRequiredData: IParticipant = {
  id: 35428,
};

export const sampleWithPartialData: IParticipant = {
  id: 55210,
};

export const sampleWithFullData: IParticipant = {
  id: 6288,
};

export const sampleWithNewData: NewParticipant = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
