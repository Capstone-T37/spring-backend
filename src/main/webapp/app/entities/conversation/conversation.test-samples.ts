import { IConversation, NewConversation } from './conversation.model';

export const sampleWithRequiredData: IConversation = {
  id: 71239,
};

export const sampleWithPartialData: IConversation = {
  id: 49083,
};

export const sampleWithFullData: IConversation = {
  id: 26524,
};

export const sampleWithNewData: NewConversation = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
