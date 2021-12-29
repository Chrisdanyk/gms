import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';
import { Type } from 'app/entities/enumerations/type.model';

export interface IEngin {
  id?: number;
  modele?: string | null;
  plaque?: string;
  dateFabrication?: dayjs.Dayjs | null;
  type?: Type;
  proprietaire?: IUser | null;
}

export class Engin implements IEngin {
  constructor(
    public id?: number,
    public modele?: string | null,
    public plaque?: string,
    public dateFabrication?: dayjs.Dayjs | null,
    public type?: Type,
    public proprietaire?: IUser | null
  ) {}
}

export function getEnginIdentifier(engin: IEngin): number | undefined {
  return engin.id;
}
