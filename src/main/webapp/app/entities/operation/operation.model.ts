import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';
import { ITache } from 'app/entities/tache/tache.model';
import { IMaintenance } from 'app/entities/maintenance/maintenance.model';

export interface IOperation {
  id?: number;
  date?: dayjs.Dayjs;
  prix?: number;
  discount?: number | null;
  nuid?: string;
  mecaniciens?: IUser[] | null;
  taches?: ITache[] | null;
  maintenances?: IMaintenance[] | null;
}

export class Operation implements IOperation {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs,
    public prix?: number,
    public discount?: number | null,
    public nuid?: string,
    public mecaniciens?: IUser[] | null,
    public taches?: ITache[] | null,
    public maintenances?: IMaintenance[] | null
  ) {}
}

export function getOperationIdentifier(operation: IOperation): number | undefined {
  return operation.id;
}
