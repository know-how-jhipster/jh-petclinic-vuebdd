<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="specialties">
        <h2 class="jh-entity-heading" data-cy="specialtiesDetailsHeading">
          <span v-text="$t('petclinicApp.specialties.detail.title')">Specialties</span> {{ specialties.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('petclinicApp.specialties.name')">Name</span>
          </dt>
          <dd>
            <span>{{ specialties.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('petclinicApp.specialties.vet')">Vet</span>
          </dt>
          <dd>
            <span v-for="(vet, i) in specialties.vets" :key="vet.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'VetsView', params: { vetsId: vet.id } }">{{ vet.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="specialties.id"
          :to="{ name: 'SpecialtiesEdit', params: { specialtiesId: specialties.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./specialties-details.component.ts"></script>
