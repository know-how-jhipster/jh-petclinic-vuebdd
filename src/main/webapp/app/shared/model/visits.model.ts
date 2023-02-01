import { IPets } from '@/shared/model/pets.model';

export interface IVisits {
  id?: number;
  visitdate?: Date;
  description?: string;
  pet?: IPets | null;
}

export class Visits implements IVisits {
  constructor(public id?: number, public visitdate?: Date, public description?: string, public pet?: IPets | null) {}
}
