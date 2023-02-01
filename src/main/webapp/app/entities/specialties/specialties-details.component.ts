import { Component, Vue, Inject } from 'vue-property-decorator';

import { ISpecialties } from '@/shared/model/specialties.model';
import SpecialtiesService from './specialties.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class SpecialtiesDetails extends Vue {
  @Inject('specialtiesService') private specialtiesService: () => SpecialtiesService;
  @Inject('alertService') private alertService: () => AlertService;

  public specialties: ISpecialties = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.specialtiesId) {
        vm.retrieveSpecialties(to.params.specialtiesId);
      }
    });
  }

  public retrieveSpecialties(specialtiesId) {
    this.specialtiesService()
      .find(specialtiesId)
      .then(res => {
        this.specialties = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
