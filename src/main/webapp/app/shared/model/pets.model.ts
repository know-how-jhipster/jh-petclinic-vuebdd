import { IVisits } from '@/shared/model/visits.model';
import { ITypes } from '@/shared/model/types.model';
import { IOwners } from '@/shared/model/owners.model';

export interface IPets {
  id?: number;
  name?: string;
  birthdate?: Date;
  visits?: IVisits[] | null;
  type?: ITypes | null;
  owner?: IOwners | null;
}

export class Pets implements IPets {
  constructor(
    public id?: number,
    public name?: string,
    public birthdate?: Date,
    public visits?: IVisits[] | null,
    public type?: ITypes | null,
    public owner?: IOwners | null
  ) {}
}
