/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import VisitsDetailComponent from '@/entities/visits/visits-details.vue';
import VisitsClass from '@/entities/visits/visits-details.component';
import VisitsService from '@/entities/visits/visits.service';
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
  describe('Visits Management Detail Component', () => {
    let wrapper: Wrapper<VisitsClass>;
    let comp: VisitsClass;
    let visitsServiceStub: SinonStubbedInstance<VisitsService>;

    beforeEach(() => {
      visitsServiceStub = sinon.createStubInstance<VisitsService>(VisitsService);

      wrapper = shallowMount<VisitsClass>(VisitsDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { visitsService: () => visitsServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundVisits = { id: 123 };
        visitsServiceStub.find.resolves(foundVisits);

        // WHEN
        comp.retrieveVisits(123);
        await comp.$nextTick();

        // THEN
        expect(comp.visits).toBe(foundVisits);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundVisits = { id: 123 };
        visitsServiceStub.find.resolves(foundVisits);

        // WHEN
        comp.beforeRouteEnter({ params: { visitsId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.visits).toBe(foundVisits);
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
