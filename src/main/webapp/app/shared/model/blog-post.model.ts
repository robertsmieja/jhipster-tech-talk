import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';

export interface IBlogPost {
  id?: number;
  text?: string;
  entryTimestamp?: Moment;
  user?: IUser;
}

export const defaultValue: Readonly<IBlogPost> = {};
