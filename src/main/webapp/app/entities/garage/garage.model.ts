import { IUser } from 'app/entities/user/user.model';

export interface IGarage {
  id?: number;
  nom?: string;
  addresse?: string | null;
  email?: string | null;
  telephone?: string | null;
  rccm?: string | null;
  url?: string | null;
  nuid?: string;
  utilisateurs?: IUser[] | null;
}

export class Garage implements IGarage {
  constructor(
    public id?: number,
    public nom?: string,
    public addresse?: string | null,
    public email?: string | null,
    public telephone?: string | null,
    public rccm?: string | null,
    public url?: string | null,
    public nuid?: string,
    public utilisateurs?: IUser[] | null
  ) {}
}

export function getGarageIdentifier(garage: IGarage): number | undefined {
  return garage.id;
}
