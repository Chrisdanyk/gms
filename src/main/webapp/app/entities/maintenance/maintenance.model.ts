import * as dayjs from 'dayjs';
import { INotification } from 'app/entities/notification/notification.model';
import { IEngin } from 'app/entities/engin/engin.model';
import { IOperation } from 'app/entities/operation/operation.model';

export interface IMaintenance {
  id?: number;
  dateDebut?: dayjs.Dayjs;
  dateFin?: dayjs.Dayjs;
  rapportGlobal?: string | null;
  prixTotal?: number;
  discountTotal?: number | null;
  nuid?: string;
  notification?: INotification | null;
  engin?: IEngin | null;
  operations?: IOperation[] | null;
}

export class Maintenance implements IMaintenance {
  constructor(
    public id?: number,
    public dateDebut?: dayjs.Dayjs,
    public dateFin?: dayjs.Dayjs,
    public rapportGlobal?: string | null,
    public prixTotal?: number,
    public discountTotal?: number | null,
    public nuid?: string,
    public notification?: INotification | null,
    public engin?: IEngin | null,
    public operations?: IOperation[] | null
  ) {}
}

export function getMaintenanceIdentifier(maintenance: IMaintenance): number | undefined {
  return maintenance.id;
}
