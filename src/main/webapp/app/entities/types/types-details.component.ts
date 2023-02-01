import { Component, Vue, Inject } from 'vue-property-decorator';

import { ITypes } from '@/shared/model/types.model';
import TypesService from './types.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class TypesDetails extends Vue {
  @Inject('typesService') private typesService: () => TypesService;
  @Inject('alertService') private alertService: () => AlertService;

  public types: ITypes = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.typesId) {
        vm.retrieveTypes(to.params.typesId);
      }
    });
  }

  public retrieveTypes(typesId) {
    this.typesService()
      .find(typesId)
      .then(res => {
        this.types = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
