import { IGarage } from 'app/entities/garage/garage.model';
import { IOperation } from 'app/entities/operation/operation.model';

export interface ITache {
  id?: number;
  nom?: string;
  prixUnitaire?: number;
  description?: string | null;
  disponible?: boolean;
  nuid?: string;
  garage?: IGarage | null;
  operations?: IOperation[] | null;
}

export class Tache implements ITache {
  constructor(
    public id?: number,
    public nom?: string,
    public prixUnitaire?: number,
    public description?: string | null,
    public disponible?: boolean,
    public nuid?: string,
    public garage?: IGarage | null,
    public operations?: IOperation[] | null
  ) {
    this.disponible = this.disponible ?? false;
  }
}

export function getTacheIdentifier(tache: ITache): number | undefined {
  return tache.id;
}
