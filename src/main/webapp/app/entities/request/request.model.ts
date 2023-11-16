import { IUser } from 'app/entities/user/user.model';
import { IMeet } from 'app/entities/meet/meet.model';

export interface IRequest {
  id: number;
  user?: Pick<IUser, 'id' | 'login'> | null;
  meet?: Pick<IMeet, 'id'> | null;
}

export type NewRequest = Omit<IRequest, 'id'> & { id: null };
