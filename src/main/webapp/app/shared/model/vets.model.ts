import { ISpecialties } from '@/shared/model/specialties.model';

export interface IVets {
  id?: number;
  firstname?: string;
  lastname?: string;
  specialties?: ISpecialties[] | null;
}

export class Vets implements IVets {
  constructor(public id?: number, public firstname?: string, public lastname?: string, public specialties?: ISpecialties[] | null) {}
}
