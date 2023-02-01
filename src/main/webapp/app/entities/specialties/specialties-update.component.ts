import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import VetsService from '@/entities/vets/vets.service';
import { IVets } from '@/shared/model/vets.model';

import { ISpecialties, Specialties } from '@/shared/model/specialties.model';
import SpecialtiesService from './specialties.service';

const validations: any = {
  specialties: {
    name: {
      required,
      maxLength: maxLength(32),
    },
  },
};

@Component({
  validations,
})
export default class SpecialtiesUpdate extends Vue {
  @Inject('specialtiesService') private specialtiesService: () => SpecialtiesService;
  @Inject('alertService') private alertService: () => AlertService;

  public specialties: ISpecialties = new Specialties();

  @Inject('vetsService') private vetsService: () => VetsService;

  public vets: IVets[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.specialtiesId) {
        vm.retrieveSpecialties(to.params.specialtiesId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
    this.specialties.vets = [];
  }

  public save(): void {
    this.isSaving = true;
    if (this.specialties.id) {
      this.specialtiesService()
        .update(this.specialties)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.specialties.updated', { param: param.id });
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.specialtiesService()
        .create(this.specialties)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.specialties.created', { param: param.id });
          (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveSpecialties(specialtiesId): void {
    this.specialtiesService()
      .find(specialtiesId)
      .then(res => {
        this.specialties = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.vetsService()
      .retrieve()
      .then(res => {
        this.vets = res.data;
      });
  }

  public getSelected(selectedVals, option): any {
    if (selectedVals) {
      return selectedVals.find(value => option.id === value.id) ?? option;
    }
    return option;
  }
}
