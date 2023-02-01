<template>
  <div>
    <h2 id="page-heading" data-cy="PetsHeading">
      <span v-text="$t('petclinicApp.pets.home.title')" id="pets-heading">Pets</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('petclinicApp.pets.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'PetsCreate' }" custom v-slot="{ navigate }">
          <button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-pets">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('petclinicApp.pets.home.createLabel')"> Create a new Pets </span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              v-bind:placeholder="$t('petclinicApp.pets.home.search')"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && pets && pets.length === 0">
      <span v-text="$t('petclinicApp.pets.home.notFound')">No pets found</span>
    </div>
    <div class="table-responsive" v-if="pets && pets.length > 0">
      <table class="table table-striped" aria-describedby="pets">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="$t('petclinicApp.pets.name')">Name</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('birthdate')">
              <span v-text="$t('petclinicApp.pets.birthdate')">Birthdate</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'birthdate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('type.id')">
              <span v-text="$t('petclinicApp.pets.type')">Type</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'type.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('owner.id')">
              <span v-text="$t('petclinicApp.pets.owner')">Owner</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'owner.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pets in pets" :key="pets.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PetsView', params: { petsId: pets.id } }">{{ pets.id }}</router-link>
            </td>
            <td>{{ pets.name }}</td>
            <td>{{ pets.birthdate }}</td>
            <td>
              <div v-if="pets.type">
                <router-link :to="{ name: 'TypesView', params: { typesId: pets.type.id } }">{{ pets.type.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="pets.owner">
                <router-link :to="{ name: 'OwnersView', params: { ownersId: pets.owner.id } }">{{ pets.owner.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'PetsView', params: { petsId: pets.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'PetsEdit', params: { petsId: pets.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(pets)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
        <infinite-loading
          ref="infiniteLoading"
          v-if="totalItems > itemsPerPage"
          :identifier="infiniteId"
          slot="append"
          @infinite="loadMore"
          force-use-infinite-wrapper=".el-table__body-wrapper"
          :distance="20"
        >
        </infinite-loading>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="petclinicApp.pets.delete.question" data-cy="petsDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-pets-heading" v-text="$t('petclinicApp.pets.delete.question', { id: removeId })">
          Are you sure you want to delete this Pets?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-pets"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removePets()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./pets.component.ts"></script>
