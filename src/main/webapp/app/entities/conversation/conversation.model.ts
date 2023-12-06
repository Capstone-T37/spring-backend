import { IUser } from 'app/entities/user/user.model';

export interface IConversation {
  id: number;
  users?: Pick<IUser, 'id' | 'login'>[] | null;
}

export type NewConversation = Omit<IConversation, 'id'> & { id: null };
