import dayjs from 'dayjs/esm';

import { IActivity, NewActivity } from './activity.model';

export const sampleWithRequiredData: IActivity = {
  id: 68021,
  title: 'Marketing Principal',
  description: 'up Optimization',
  date: dayjs('2023-09-18T12:21'),
};

export const sampleWithPartialData: IActivity = {
  id: 69975,
  title: 'Interactions',
  description: 'programming',
  date: dayjs('2023-09-17T19:34'),
};

export const sampleWithFullData: IActivity = {
  id: 26841,
  title: 'content connect driver',
  description: 'encompassing',
  date: dayjs('2023-09-18T05:28'),
};

export const sampleWithNewData: NewActivity = {
  title: 'Money Fields frictionless',
  description: 'invoice bandwidth',
  date: dayjs('2023-09-17T19:49'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
