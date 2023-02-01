<template>
  <div>
    <h2 id="page-heading" data-cy="SpecialtiesHeading">
      <span v-text="$t('petclinicApp.specialties.home.title')" id="specialties-heading">Specialties</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('petclinicApp.specialties.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'SpecialtiesCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-specialties"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('petclinicApp.specialties.home.createLabel')"> Create a new Specialties </span>
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
              v-bind:placeholder="$t('petclinicApp.specialties.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && specialties && specialties.length === 0">
      <span v-text="$t('petclinicApp.specialties.home.notFound')">No specialties found</span>
    </div>
    <div class="table-responsive" v-if="specialties && specialties.length > 0">
      <table class="table table-striped" aria-describedby="specialties">
        <thead>
          <tr>
            <th scope="row"><span v-text="$t('global.field.id')">ID</span></th>
            <th scope="row"><span v-text="$t('petclinicApp.specialties.name')">Name</span></th>
            <th scope="row"><span v-text="$t('petclinicApp.specialties.vet')">Vet</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="specialties in specialties" :key="specialties.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'SpecialtiesView', params: { specialtiesId: specialties.id } }">{{ specialties.id }}</router-link>
            </td>
            <td>{{ specialties.name }}</td>
            <td>
              <span v-for="(vet, i) in specialties.vets" :key="vet.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link class="form-control-static" :to="{ name: 'VetsView', params: { vetsId: vet.id } }">{{ vet.id }}</router-link>
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'SpecialtiesView', params: { specialtiesId: specialties.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'SpecialtiesEdit', params: { specialtiesId: specialties.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(specialties)"
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
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="petclinicApp.specialties.delete.question" data-cy="specialtiesDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-specialties-heading" v-text="$t('petclinicApp.specialties.delete.question', { id: removeId })">
          Are you sure you want to delete this Specialties?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-specialties"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeSpecialties()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./specialties.component.ts"></script>
