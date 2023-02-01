import { IVets } from '@/shared/model/vets.model';

export interface ISpecialties {
  id?: number;
  name?: string;
  vets?: IVets[] | null;
}

export class Specialties implements ISpecialties {
  constructor(public id?: number, public name?: string, public vets?: IVets[] | null) {}
}
