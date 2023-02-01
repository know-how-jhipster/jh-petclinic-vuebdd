/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import VetsDetailComponent from '@/entities/vets/vets-details.vue';
import VetsClass from '@/entities/vets/vets-details.component';
import VetsService from '@/entities/vets/vets.service';
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
  describe('Vets Management Detail Component', () => {
    let wrapper: Wrapper<VetsClass>;
    let comp: VetsClass;
    let vetsServiceStub: SinonStubbedInstance<VetsService>;

    beforeEach(() => {
      vetsServiceStub = sinon.createStubInstance<VetsService>(VetsService);

      wrapper = shallowMount<VetsClass>(VetsDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { vetsService: () => vetsServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundVets = { id: 123 };
        vetsServiceStub.find.resolves(foundVets);

        // WHEN
        comp.retrieveVets(123);
        await comp.$nextTick();

        // THEN
        expect(comp.vets).toBe(foundVets);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundVets = { id: 123 };
        vetsServiceStub.find.resolves(foundVets);

        // WHEN
        comp.beforeRouteEnter({ params: { vetsId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.vets).toBe(foundVets);
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
