import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { ISpecialties } from '@/shared/model/specialties.model';

import SpecialtiesService from './specialties.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Specialties extends Vue {
  @Inject('specialtiesService') private specialtiesService: () => SpecialtiesService;
  @Inject('alertService') private alertService: () => AlertService;

  public currentSearch = '';
  private removeId: number = null;

  public specialties: ISpecialties[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllSpecialtiess();
  }

  public search(query): void {
    if (!query) {
      return this.clear();
    }
    this.currentSearch = query;
    this.retrieveAllSpecialtiess();
  }

  public clear(): void {
    this.currentSearch = '';
    this.retrieveAllSpecialtiess();
  }

  public retrieveAllSpecialtiess(): void {
    this.isFetching = true;
    if (this.currentSearch) {
      this.specialtiesService()
        .search(this.currentSearch)
        .then(
          res => {
            this.specialties = res;
            this.isFetching = false;
          },
          err => {
            this.isFetching = false;
            this.alertService().showHttpError(this, err.response);
          }
        );
      return;
    }
    this.specialtiesService()
      .retrieve()
      .then(
        res => {
          this.specialties = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: ISpecialties): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeSpecialties(): void {
    this.specialtiesService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('petclinicApp.specialties.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllSpecialtiess();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
