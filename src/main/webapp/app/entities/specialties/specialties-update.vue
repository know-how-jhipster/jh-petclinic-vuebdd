<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="petclinicApp.specialties.home.createOrEditLabel"
          data-cy="SpecialtiesCreateUpdateHeading"
          v-text="$t('petclinicApp.specialties.home.createOrEditLabel')"
        >
          Create or edit a Specialties
        </h2>
        <div>
          <div class="form-group" v-if="specialties.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="specialties.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.specialties.name')" for="specialties-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="specialties-name"
              data-cy="name"
              :class="{ valid: !$v.specialties.name.$invalid, invalid: $v.specialties.name.$invalid }"
              v-model="$v.specialties.name.$model"
              required
            />
            <div v-if="$v.specialties.name.$anyDirty && $v.specialties.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.specialties.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.specialties.name.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 32 })"
              >
                This field cannot be longer than 32 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label v-text="$t('petclinicApp.specialties.vet')" for="specialties-vet">Vet</label>
            <select
              class="form-control"
              id="specialties-vets"
              data-cy="vet"
              multiple
              name="vet"
              v-if="specialties.vets !== undefined"
              v-model="specialties.vets"
            >
              <option v-bind:value="getSelected(specialties.vets, vetsOption)" v-for="vetsOption in vets" :key="vetsOption.id">
                {{ vetsOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.specialties.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./specialties-update.component.ts"></script>
