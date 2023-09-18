import dayjs from 'dayjs/esm';

import { IActivity, NewActivity } from './activity.model';

export const sampleWithRequiredData: IActivity = {
  id: 68021,
  title: 'Marketing Principal',
  description: 'up Optimization',
  address: 'Franc',
  date: dayjs('2023-09-17T17:01'),
  maximum: 28409,
};

export const sampleWithPartialData: IActivity = {
  id: 13370,
  title: 'Future reciprocal platforms',
  description: 'Clothing backing',
  address: 'navigating Money',
  date: dayjs('2023-09-18T03:33'),
  maximum: 21902,
};

export const sampleWithFullData: IActivity = {
  id: 87964,
  title: 'ability invoice',
  description: 'Rustic Cloned copy',
  address: 'Handcrafted algorithm',
  date: dayjs('2023-09-17T22:18'),
  maximum: 55531,
};

export const sampleWithNewData: NewActivity = {
  title: 'Liberia Market Soft',
  description: 'Rustic Senior',
  address: 'Berkshire Point',
  date: dayjs('2023-09-18T03:23'),
  maximum: 98771,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
