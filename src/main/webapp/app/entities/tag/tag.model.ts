export interface ITag {
  id: number;
  title?: string | null;
}

export type NewTag = Omit<ITag, 'id'> & { id: null };
