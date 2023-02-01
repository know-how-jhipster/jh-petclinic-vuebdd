/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import PetsDetailComponent from '@/entities/pets/pets-details.vue';
import PetsClass from '@/entities/pets/pets-details.component';
import PetsService from '@/entities/pets/pets.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Pets Management Detail Component', () => {
    let wrapper: Wrapper<PetsClass>;
    let comp: PetsClass;
    let petsServiceStub: SinonStubbedInstance<PetsService>;

    beforeEach(() => {
      petsServiceStub = sinon.createStubInstance<PetsService>(PetsService);

      wrapper = shallowMount<PetsClass>(PetsDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { petsService: () => petsServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundPets = { id: 123 };
        petsServiceStub.find.resolves(foundPets);

        // WHEN
        comp.retrievePets(123);
        await comp.$nextTick();

        // THEN
        expect(comp.pets).toBe(foundPets);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundPets = { id: 123 };
        petsServiceStub.find.resolves(foundPets);

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
