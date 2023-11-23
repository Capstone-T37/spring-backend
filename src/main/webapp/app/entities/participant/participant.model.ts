import { IUser } from 'app/entities/user/user.model';
import { IActivity } from 'app/entities/activity/activity.model';

export interface IParticipant {
  id: number;
  user?: Pick<IUser, 'id' | 'login'> | null;
  activity?: Pick<IActivity, 'id'> | null;
}

export type NewParticipant = Omit<IParticipant, 'id'> & { id: null };
