import { Component, Vue, Inject } from 'vue-property-decorator';

import { IVets } from '@/shared/model/vets.model';
import VetsService from './vets.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class VetsDetails extends Vue {
  @Inject('vetsService') private vetsService: () => VetsService;
  @Inject('alertService') private alertService: () => AlertService;

  public vets: IVets = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.vetsId) {
        vm.retrieveVets(to.params.vetsId);
      }
    });
  }

  public retrieveVets(vetsId) {
    this.vetsService()
      .find(vetsId)
      .then(res => {
        this.vets = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
