import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import PetsService from '@/entities/pets/pets.service';
import { IPets } from '@/shared/model/pets.model';

import { IOwners, Owners } from '@/shared/model/owners.model';
import OwnersService from './owners.service';

const validations: any = {
  owners: {
    firstname: {
      required,
      maxLength: maxLength(32),
    },
    lastname: {
      required,
      maxLength: maxLength(32),
    },
    address: {
      required,
      maxLength: maxLength(255),
    },
    city: {
      maxLength: maxLength(32),
    },
    telephone: {
      required,
      maxLength: maxLength(20),
    },
  },
};

@Component({
  validations,
})
export default class OwnersUpdate extends Vue {
  @Inject('ownersService') private ownersService: () => OwnersService;
  @Inject('alertService') private alertService: () => AlertService;

  public owners: IOwners = new Owners();

  @Inject('petsService') private petsService: () => PetsService;

  public pets: IPets[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.ownersId) {
        vm.retrieveOwners(to.params.ownersId);
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
    if (this.owners.id) {
      this.ownersService()
        .update(this.owners)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.owners.updated', { param: param.id });
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
      this.ownersService()
        .create(this.owners)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.owners.created', { param: param.id });
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

  public retrieveOwners(ownersId): void {
    this.ownersService()
      .find(ownersId)
      .then(res => {
        this.owners = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.petsService()
      .retrieve()
      .then(res => {
        this.pets = res.data;
      });
  }
}
