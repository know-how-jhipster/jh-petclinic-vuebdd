import { Component, Vue, Inject } from 'vue-property-decorator';

import { IVisits } from '@/shared/model/visits.model';
import VisitsService from './visits.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class VisitsDetails extends Vue {
  @Inject('visitsService') private visitsService: () => VisitsService;
  @Inject('alertService') private alertService: () => AlertService;

  public visits: IVisits = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.visitsId) {
        vm.retrieveVisits(to.params.visitsId);
      }
    });
  }

  public retrieveVisits(visitsId) {
    this.visitsService()
      .find(visitsId)
      .then(res => {
        this.visits = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
