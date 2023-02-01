/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import VetsUpdateComponent from '@/entities/vets/vets-update.vue';
import VetsClass from '@/entities/vets/vets-update.component';
import VetsService from '@/entities/vets/vets.service';

import SpecialtiesService from '@/entities/specialties/specialties.service';
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
  describe('Vets Management Update Component', () => {
    let wrapper: Wrapper<VetsClass>;
    let comp: VetsClass;
    let vetsServiceStub: SinonStubbedInstance<VetsService>;

    beforeEach(() => {
      vetsServiceStub = sinon.createStubInstance<VetsService>(VetsService);

      wrapper = shallowMount<VetsClass>(VetsUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          vetsService: () => vetsServiceStub,
          alertService: () => new AlertService(),

          specialtiesService: () =>
            sinon.createStubInstance<SpecialtiesService>(SpecialtiesService, {
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
        comp.vets = entity;
        vetsServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(vetsServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.vets = entity;
        vetsServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(vetsServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundVets = { id: 123 };
        vetsServiceStub.find.resolves(foundVets);
        vetsServiceStub.retrieve.resolves([foundVets]);

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
