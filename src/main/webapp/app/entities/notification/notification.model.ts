import * as dayjs from 'dayjs';
import { IMaintenance } from 'app/entities/maintenance/maintenance.model';
import { Statut } from 'app/entities/enumerations/statut.model';

export interface INotification {
  id?: number;
  date?: dayjs.Dayjs;
  statut?: Statut;
  dateProchaineMaintenance?: dayjs.Dayjs | null;
  nuid?: string;
  maintenance?: IMaintenance | null;
}

export class Notification implements INotification {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs,
    public statut?: Statut,
    public dateProchaineMaintenance?: dayjs.Dayjs | null,
    public nuid?: string,
    public maintenance?: IMaintenance | null
  ) {}
}

export function getNotificationIdentifier(notification: INotification): number | undefined {
  return notification.id;
}
