<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="owners">
        <h2 class="jh-entity-heading" data-cy="ownersDetailsHeading">
          <span v-text="$t('petclinicApp.owners.detail.title')">Owners</span> {{ owners.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('petclinicApp.owners.firstname')">Firstname</span>
          </dt>
          <dd>
            <span>{{ owners.firstname }}</span>
          </dd>
          <dt>
            <span v-text="$t('petclinicApp.owners.lastname')">Lastname</span>
          </dt>
          <dd>
            <span>{{ owners.lastname }}</span>
          </dd>
          <dt>
            <span v-text="$t('petclinicApp.owners.address')">Address</span>
          </dt>
          <dd>
            <span>{{ owners.address }}</span>
          </dd>
          <dt>
            <span v-text="$t('petclinicApp.owners.city')">City</span>
          </dt>
          <dd>
            <span>{{ owners.city }}</span>
          </dd>
          <dt>
            <span v-text="$t('petclinicApp.owners.telephone')">Telephone</span>
          </dt>
          <dd>
            <span>{{ owners.telephone }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="owners.id" :to="{ name: 'OwnersEdit', params: { ownersId: owners.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./owners-details.component.ts"></script>
