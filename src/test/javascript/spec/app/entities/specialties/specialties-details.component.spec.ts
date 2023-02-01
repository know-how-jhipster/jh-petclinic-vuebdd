/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import SpecialtiesDetailComponent from '@/entities/specialties/specialties-details.vue';
import SpecialtiesClass from '@/entities/specialties/specialties-details.component';
import SpecialtiesService from '@/entities/specialties/specialties.service';
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
  describe('Specialties Management Detail Component', () => {
    let wrapper: Wrapper<SpecialtiesClass>;
    let comp: SpecialtiesClass;
    let specialtiesServiceStub: SinonStubbedInstance<SpecialtiesService>;

    beforeEach(() => {
      specialtiesServiceStub = sinon.createStubInstance<SpecialtiesService>(SpecialtiesService);

      wrapper = shallowMount<SpecialtiesClass>(SpecialtiesDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { specialtiesService: () => specialtiesServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundSpecialties = { id: 123 };
        specialtiesServiceStub.find.resolves(foundSpecialties);

        // WHEN
        comp.retrieveSpecialties(123);
        await comp.$nextTick();

        // THEN
        expect(comp.specialties).toBe(foundSpecialties);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSpecialties = { id: 123 };
        specialtiesServiceStub.find.resolves(foundSpecialties);

        // WHEN
        comp.beforeRouteEnter({ params: { specialtiesId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.specialties).toBe(foundSpecialties);
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
