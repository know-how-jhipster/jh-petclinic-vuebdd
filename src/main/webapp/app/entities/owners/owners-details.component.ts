import { Component, Vue, Inject } from 'vue-property-decorator';

import { IOwners } from '@/shared/model/owners.model';
import OwnersService from './owners.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class OwnersDetails extends Vue {
  @Inject('ownersService') private ownersService: () => OwnersService;
  @Inject('alertService') private alertService: () => AlertService;

  public owners: IOwners = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.ownersId) {
        vm.retrieveOwners(to.params.ownersId);
      }
    });
  }

  public retrieveOwners(ownersId) {
    this.ownersService()
      .find(ownersId)
      .then(res => {
        this.owners = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
