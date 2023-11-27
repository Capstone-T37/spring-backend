import { ITag } from 'app/entities/tag/tag.model';
import { IActivity } from 'app/entities/activity/activity.model';

export interface IActivityTag {
  id: number;
  tag?: Pick<ITag, 'id'> | null;
  activity?: Pick<IActivity, 'id'> | null;
}

export type NewActivityTag = Omit<IActivityTag, 'id'> & { id: null };
