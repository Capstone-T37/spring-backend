import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IActivity {
  id: number;
  title?: string | null;
  description?: string | null;
  address?: string | null;
  date?: dayjs.Dayjs | null;
  maximum?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewActivity = Omit<IActivity, 'id'> & { id: null };
