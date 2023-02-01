import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

// prettier-ignore
const Vets = () => import('@/entities/vets/vets.vue');
// prettier-ignore
const VetsUpdate = () => import('@/entities/vets/vets-update.vue');
// prettier-ignore
const VetsDetails = () => import('@/entities/vets/vets-details.vue');
// prettier-ignore
const Specialties = () => import('@/entities/specialties/specialties.vue');
// prettier-ignore
const SpecialtiesUpdate = () => import('@/entities/specialties/specialties-update.vue');
// prettier-ignore
const SpecialtiesDetails = () => import('@/entities/specialties/specialties-details.vue');
// prettier-ignore
const Types = () => import('@/entities/types/types.vue');
// prettier-ignore
const TypesUpdate = () => import('@/entities/types/types-update.vue');
// prettier-ignore
const TypesDetails = () => import('@/entities/types/types-details.vue');
// prettier-ignore
const Owners = () => import('@/entities/owners/owners.vue');
// prettier-ignore
const OwnersUpdate = () => import('@/entities/owners/owners-update.vue');
// prettier-ignore
const OwnersDetails = () => import('@/entities/owners/owners-details.vue');
// prettier-ignore
const Pets = () => import('@/entities/pets/pets.vue');
// prettier-ignore
const PetsUpdate = () => import('@/entities/pets/pets-update.vue');
// prettier-ignore
const PetsDetails = () => import('@/entities/pets/pets-details.vue');
// prettier-ignore
const Visits = () => import('@/entities/visits/visits.vue');
// prettier-ignore
const VisitsUpdate = () => import('@/entities/visits/visits-update.vue');
// prettier-ignore
const VisitsDetails = () => import('@/entities/visits/visits-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'vets',
      name: 'Vets',
      component: Vets,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'vets/new',
      name: 'VetsCreate',
      component: VetsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'vets/:vetsId/edit',
      name: 'VetsEdit',
      component: VetsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'vets/:vetsId/view',
      name: 'VetsView',
      component: VetsDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'specialties',
      name: 'Specialties',
      component: Specialties,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'specialties/new',
      name: 'SpecialtiesCreate',
      component: SpecialtiesUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'specialties/:specialtiesId/edit',
      name: 'SpecialtiesEdit',
      component: SpecialtiesUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'specialties/:specialtiesId/view',
      name: 'SpecialtiesView',
      component: SpecialtiesDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'types',
      name: 'Types',
      component: Types,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'types/new',
      name: 'TypesCreate',
      component: TypesUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'types/:typesId/edit',
      name: 'TypesEdit',
      component: TypesUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'types/:typesId/view',
      name: 'TypesView',
      component: TypesDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'owners',
      name: 'Owners',
      component: Owners,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'owners/new',
      name: 'OwnersCreate',
      component: OwnersUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'owners/:ownersId/edit',
      name: 'OwnersEdit',
      component: OwnersUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'owners/:ownersId/view',
      name: 'OwnersView',
      component: OwnersDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'pets',
      name: 'Pets',
      component: Pets,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'pets/new',
      name: 'PetsCreate',
      component: PetsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'pets/:petsId/edit',
      name: 'PetsEdit',
      component: PetsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'pets/:petsId/view',
      name: 'PetsView',
      component: PetsDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'visits',
      name: 'Visits',
      component: Visits,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'visits/new',
      name: 'VisitsCreate',
      component: VisitsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'visits/:visitsId/edit',
      name: 'VisitsEdit',
      component: VisitsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'visits/:visitsId/view',
      name: 'VisitsView',
      component: VisitsDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
