/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import SpecialtiesComponent from '@/entities/specialties/specialties.vue';
import SpecialtiesClass from '@/entities/specialties/specialties.component';
import SpecialtiesService from '@/entities/specialties/specialties.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(ToastPlugin);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('Specialties Management Component', () => {
    let wrapper: Wrapper<SpecialtiesClass>;
    let comp: SpecialtiesClass;
    let specialtiesServiceStub: SinonStubbedInstance<SpecialtiesService>;

    beforeEach(() => {
      specialtiesServiceStub = sinon.createStubInstance<SpecialtiesService>(SpecialtiesService);
      specialtiesServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<SpecialtiesClass>(SpecialtiesComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          specialtiesService: () => specialtiesServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      specialtiesServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllSpecialtiess();
      await comp.$nextTick();

      // THEN
      expect(specialtiesServiceStub.retrieve.called).toBeTruthy();
      expect(comp.specialties[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      specialtiesServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      expect(specialtiesServiceStub.retrieve.callCount).toEqual(1);

      comp.removeSpecialties();
      await comp.$nextTick();

      // THEN
      expect(specialtiesServiceStub.delete.called).toBeTruthy();
      expect(specialtiesServiceStub.retrieve.callCount).toEqual(2);
    });
  });
});
