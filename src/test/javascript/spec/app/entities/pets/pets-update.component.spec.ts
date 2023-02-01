/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import PetsUpdateComponent from '@/entities/pets/pets-update.vue';
import PetsClass from '@/entities/pets/pets-update.component';
import PetsService from '@/entities/pets/pets.service';

import VisitsService from '@/entities/visits/visits.service';

import TypesService from '@/entities/types/types.service';

import OwnersService from '@/entities/owners/owners.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Pets Management Update Component', () => {
    let wrapper: Wrapper<PetsClass>;
    let comp: PetsClass;
    let petsServiceStub: SinonStubbedInstance<PetsService>;

    beforeEach(() => {
      petsServiceStub = sinon.createStubInstance<PetsService>(PetsService);

      wrapper = shallowMount<PetsClass>(PetsUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          petsService: () => petsServiceStub,
          alertService: () => new AlertService(),

          visitsService: () =>
            sinon.createStubInstance<VisitsService>(VisitsService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          typesService: () =>
            sinon.createStubInstance<TypesService>(TypesService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          ownersService: () =>
            sinon.createStubInstance<OwnersService>(OwnersService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.pets = entity;
        petsServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(petsServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.pets = entity;
        petsServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(petsServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundPets = { id: 123 };
        petsServiceStub.find.resolves(foundPets);
        petsServiceStub.retrieve.resolves([foundPets]);

        // WHEN
        comp.beforeRouteEnter({ params: { petsId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.pets).toBe(foundPets);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
