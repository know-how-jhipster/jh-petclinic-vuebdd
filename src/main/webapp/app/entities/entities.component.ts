import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import VetsService from './vets/vets.service';
import SpecialtiesService from './specialties/specialties.service';
import TypesService from './types/types.service';
import OwnersService from './owners/owners.service';
import PetsService from './pets/pets.service';
import VisitsService from './visits/visits.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('vetsService') private vetsService = () => new VetsService();
  @Provide('specialtiesService') private specialtiesService = () => new SpecialtiesService();
  @Provide('typesService') private typesService = () => new TypesService();
  @Provide('ownersService') private ownersService = () => new OwnersService();
  @Provide('petsService') private petsService = () => new PetsService();
  @Provide('visitsService') private visitsService = () => new VisitsService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
