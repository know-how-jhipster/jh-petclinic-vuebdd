/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import SpecialtiesUpdateComponent from '@/entities/specialties/specialties-update.vue';
import SpecialtiesClass from '@/entities/specialties/specialties-update.component';
import SpecialtiesService from '@/entities/specialties/specialties.service';

import VetsService from '@/entities/vets/vets.service';
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
  describe('Specialties Management Update Component', () => {
    let wrapper: Wrapper<SpecialtiesClass>;
    let comp: SpecialtiesClass;
    let specialtiesServiceStub: SinonStubbedInstance<SpecialtiesService>;

    beforeEach(() => {
      specialtiesServiceStub = sinon.createStubInstance<SpecialtiesService>(SpecialtiesService);

      wrapper = shallowMount<SpecialtiesClass>(SpecialtiesUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          specialtiesService: () => specialtiesServiceStub,
          alertService: () => new AlertService(),

          vetsService: () =>
            sinon.createStubInstance<VetsService>(VetsService, {
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
        comp.specialties = entity;
        specialtiesServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(specialtiesServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.specialties = entity;
        specialtiesServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(specialtiesServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSpecialties = { id: 123 };
        specialtiesServiceStub.find.resolves(foundSpecialties);
        specialtiesServiceStub.retrieve.resolves([foundSpecialties]);

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
