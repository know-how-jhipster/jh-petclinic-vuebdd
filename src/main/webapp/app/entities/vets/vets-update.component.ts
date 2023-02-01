import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import SpecialtiesService from '@/entities/specialties/specialties.service';
import { ISpecialties } from '@/shared/model/specialties.model';

import { IVets, Vets } from '@/shared/model/vets.model';
import VetsService from './vets.service';

const validations: any = {
  vets: {
    firstname: {
      required,
      maxLength: maxLength(32),
    },
    lastname: {
      required,
      maxLength: maxLength(32),
    },
  },
};

@Component({
  validations,
})
export default class VetsUpdate extends Vue {
  @Inject('vetsService') private vetsService: () => VetsService;
  @Inject('alertService') private alertService: () => AlertService;

  public vets: IVets = new Vets();

  @Inject('specialtiesService') private specialtiesService: () => SpecialtiesService;

  public specialties: ISpecialties[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.vetsId) {
        vm.retrieveVets(to.params.vetsId);
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
  }

  public save(): void {
    this.isSaving = true;
    if (this.vets.id) {
      this.vetsService()
        .update(this.vets)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.vets.updated', { param: param.id });
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
      this.vetsService()
        .create(this.vets)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.vets.created', { param: param.id });
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

  public retrieveVets(vetsId): void {
    this.vetsService()
      .find(vetsId)
      .then(res => {
        this.vets = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.specialtiesService()
      .retrieve()
      .then(res => {
        this.specialties = res.data;
      });
  }
}
