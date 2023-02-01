/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import OwnersDetailComponent from '@/entities/owners/owners-details.vue';
import OwnersClass from '@/entities/owners/owners-details.component';
import OwnersService from '@/entities/owners/owners.service';
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
  describe('Owners Management Detail Component', () => {
    let wrapper: Wrapper<OwnersClass>;
    let comp: OwnersClass;
    let ownersServiceStub: SinonStubbedInstance<OwnersService>;

    beforeEach(() => {
      ownersServiceStub = sinon.createStubInstance<OwnersService>(OwnersService);

      wrapper = shallowMount<OwnersClass>(OwnersDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { ownersService: () => ownersServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundOwners = { id: 123 };
        ownersServiceStub.find.resolves(foundOwners);

        // WHEN
        comp.retrieveOwners(123);
        await comp.$nextTick();

        // THEN
        expect(comp.owners).toBe(foundOwners);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundOwners = { id: 123 };
        ownersServiceStub.find.resolves(foundOwners);

        // WHEN
        comp.beforeRouteEnter({ params: { ownersId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.owners).toBe(foundOwners);
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
