/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import TypesDetailComponent from '@/entities/types/types-details.vue';
import TypesClass from '@/entities/types/types-details.component';
import TypesService from '@/entities/types/types.service';
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
  describe('Types Management Detail Component', () => {
    let wrapper: Wrapper<TypesClass>;
    let comp: TypesClass;
    let typesServiceStub: SinonStubbedInstance<TypesService>;

    beforeEach(() => {
      typesServiceStub = sinon.createStubInstance<TypesService>(TypesService);

      wrapper = shallowMount<TypesClass>(TypesDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { typesService: () => typesServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundTypes = { id: 123 };
        typesServiceStub.find.resolves(foundTypes);

        // WHEN
        comp.retrieveTypes(123);
        await comp.$nextTick();

        // THEN
        expect(comp.types).toBe(foundTypes);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundTypes = { id: 123 };
        typesServiceStub.find.resolves(foundTypes);

        // WHEN
        comp.beforeRouteEnter({ params: { typesId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.types).toBe(foundTypes);
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
