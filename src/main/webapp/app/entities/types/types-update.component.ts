import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import { ITypes, Types } from '@/shared/model/types.model';
import TypesService from './types.service';

const validations: any = {
  types: {
    name: {
      required,
      maxLength: maxLength(80),
    },
  },
};

@Component({
  validations,
})
export default class TypesUpdate extends Vue {
  @Inject('typesService') private typesService: () => TypesService;
  @Inject('alertService') private alertService: () => AlertService;

  public types: ITypes = new Types();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.typesId) {
        vm.retrieveTypes(to.params.typesId);
      }
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
    if (this.types.id) {
      this.typesService()
        .update(this.types)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.types.updated', { param: param.id });
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
      this.typesService()
        .create(this.types)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.types.created', { param: param.id });
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

  public retrieveTypes(typesId): void {
    this.typesService()
      .find(typesId)
      .then(res => {
        this.types = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
