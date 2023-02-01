import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import PetsService from '@/entities/pets/pets.service';
import { IPets } from '@/shared/model/pets.model';

import { IVisits, Visits } from '@/shared/model/visits.model';
import VisitsService from './visits.service';

const validations: any = {
  visits: {
    visitdate: {
      required,
    },
    description: {
      required,
      maxLength: maxLength(255),
    },
  },
};

@Component({
  validations,
})
export default class VisitsUpdate extends Vue {
  @Inject('visitsService') private visitsService: () => VisitsService;
  @Inject('alertService') private alertService: () => AlertService;

  public visits: IVisits = new Visits();

  @Inject('petsService') private petsService: () => PetsService;

  public pets: IPets[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.visitsId) {
        vm.retrieveVisits(to.params.visitsId);
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
    if (this.visits.id) {
      this.visitsService()
        .update(this.visits)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.visits.updated', { param: param.id });
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
      this.visitsService()
        .create(this.visits)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('petclinicApp.visits.created', { param: param.id });
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

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.visits[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.visits[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.visits[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.visits[field] = null;
    }
  }

  public retrieveVisits(visitsId): void {
    this.visitsService()
      .find(visitsId)
      .then(res => {
        res.visitdate = new Date(res.visitdate);
        this.visits = res;
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
