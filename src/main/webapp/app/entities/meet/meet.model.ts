import { IUser } from 'app/entities/user/user.model';

export interface IMeet {
  id: number;
  description?: string | null;
  isEnabled?: boolean | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewMeet = Omit<IMeet, 'id'> & { id: null };
