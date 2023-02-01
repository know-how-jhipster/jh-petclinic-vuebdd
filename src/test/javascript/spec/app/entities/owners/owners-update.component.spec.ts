/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import OwnersUpdateComponent from '@/entities/owners/owners-update.vue';
import OwnersClass from '@/entities/owners/owners-update.component';
import OwnersService from '@/entities/owners/owners.service';

import PetsService from '@/entities/pets/pets.service';
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
  describe('Owners Management Update Component', () => {
    let wrapper: Wrapper<OwnersClass>;
    let comp: OwnersClass;
    let ownersServiceStub: SinonStubbedInstance<OwnersService>;

    beforeEach(() => {
      ownersServiceStub = sinon.createStubInstance<OwnersService>(OwnersService);

      wrapper = shallowMount<OwnersClass>(OwnersUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          ownersService: () => ownersServiceStub,
          alertService: () => new AlertService(),

          petsService: () =>
            sinon.createStubInstance<PetsService>(PetsService, {
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
        comp.owners = entity;
        ownersServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(ownersServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.owners = entity;
        ownersServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(ownersServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundOwners = { id: 123 };
        ownersServiceStub.find.resolves(foundOwners);
        ownersServiceStub.retrieve.resolves([foundOwners]);

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
