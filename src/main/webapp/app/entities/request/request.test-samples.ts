import { IRequest, NewRequest } from './request.model';

export const sampleWithRequiredData: IRequest = {
  id: 88640,
};

export const sampleWithPartialData: IRequest = {
  id: 23857,
};

export const sampleWithFullData: IRequest = {
  id: 95978,
};

export const sampleWithNewData: NewRequest = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
