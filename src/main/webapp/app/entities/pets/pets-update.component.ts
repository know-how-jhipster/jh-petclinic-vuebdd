import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import VisitsService from '@/entities/visits/visits.service';
import { IVisits } from '@/shared/model/visits.model';

import TypesService from '@/entities/types/types.service';
import { ITypes } from '@/shared/model/types.model';

import OwnersService from '@/entities/owners/owners.service';
import { IOwners } from '@/shared/model/owners.model';

import { IPets, Pets } from '@/shared/model/pets.model';
import PetsService from './pets.service';

const validations: any = {
  pets: {
    name: {
      required,
      maxLength: maxLength(32),
    },
    birthdate: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class PetsUpdate extends Vue {
  @Inject('petsService') private petsService: () => PetsService;
  @Inject('alertService') private alertService: () => AlertService;

  public pets: IPets = new Pets();

  @Inject('visitsService') private visitsService: () => VisitsService;

  public visits: IVisits[] = [];

  @Inject('typesService') private typesService: () => TypesService;

  public types: ITypes[] = [];

  @Inject('ownersService') private ownersService: () => OwnersService;

  public owners: IOwners[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.petsId) {
        vm.retrievePets(to.params.petsId);
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
    if (this.pets.id) {
      this.petsService()
        .update(this.pets)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.pets.updated', { param: param.id });
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
      this.petsService()
        .create(this.pets)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.pets.created', { param: param.id });
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

  public retrievePets(petsId): void {
    this.petsService()
      .find(petsId)
      .then(res => {
        this.pets = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.visitsService()
      .retrieve()
      .then(res => {
        this.visits = res.data;
      });
    this.typesService()
      .retrieve()
      .then(res => {
        this.types = res.data;
      });
    this.ownersService()
      .retrieve()
      .then(res => {
        this.owners = res.data;
      });
  }
}
