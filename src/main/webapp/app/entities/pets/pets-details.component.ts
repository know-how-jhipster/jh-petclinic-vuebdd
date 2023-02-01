import { Component, Vue, Inject } from 'vue-property-decorator';

import { IPets } from '@/shared/model/pets.model';
import PetsService from './pets.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class PetsDetails extends Vue {
  @Inject('petsService') private petsService: () => PetsService;
  @Inject('alertService') private alertService: () => AlertService;

  public pets: IPets = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.petsId) {
        vm.retrievePets(to.params.petsId);
      }
    });
  }

  public retrievePets(petsId) {
    this.petsService()
      .find(petsId)
      .then(res => {
        this.pets = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
