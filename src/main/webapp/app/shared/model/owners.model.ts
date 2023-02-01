import { IPets } from '@/shared/model/pets.model';

export interface IOwners {
  id?: number;
  firstname?: string;
  lastname?: string;
  address?: string;
  city?: string | null;
  telephone?: string;
  pets?: IPets[] | null;
}

export class Owners implements IOwners {
  constructor(
    public id?: number,
    public firstname?: string,
    public lastname?: string,
    public address?: string,
    public city?: string | null,
    public telephone?: string,
    public pets?: IPets[] | null
  ) {}
}
