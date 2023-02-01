<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="petclinicApp.pets.home.createOrEditLabel"
          data-cy="PetsCreateUpdateHeading"
          v-text="$t('petclinicApp.pets.home.createOrEditLabel')"
        >
          Create or edit a Pets
        </h2>
        <div>
          <div class="form-group" v-if="pets.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="pets.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.pets.name')" for="pets-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="pets-name"
              data-cy="name"
              :class="{ valid: !$v.pets.name.$invalid, invalid: $v.pets.name.$invalid }"
              v-model="$v.pets.name.$model"
              required
            />
            <div v-if="$v.pets.name.$anyDirty && $v.pets.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.pets.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.pets.name.maxLength" v-text="$t('entity.validation.maxlength', { max: 32 })">
                This field cannot be longer than 32 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.pets.birthdate')" for="pets-birthdate">Birthdate</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="pets-birthdate"
                  v-model="$v.pets.birthdate.$model"
                  name="birthdate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="pets-birthdate"
                data-cy="birthdate"
                type="text"
                class="form-control"
                name="birthdate"
                :class="{ valid: !$v.pets.birthdate.$invalid, invalid: $v.pets.birthdate.$invalid }"
                v-model="$v.pets.birthdate.$model"
                required
              />
            </b-input-group>
            <div v-if="$v.pets.birthdate.$anyDirty && $v.pets.birthdate.$invalid">
              <small class="form-text text-danger" v-if="!$v.pets.birthdate.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.pets.type')" for="pets-type">Type</label>
            <select class="form-control" id="pets-type" data-cy="type" name="type" v-model="pets.type">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="pets.type && typesOption.id === pets.type.id ? pets.type : typesOption"
                v-for="typesOption in types"
                :key="typesOption.id"
              >
                {{ typesOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.pets.owner')" for="pets-owner">Owner</label>
            <select class="form-control" id="pets-owner" data-cy="owner" name="owner" v-model="pets.owner">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="pets.owner && ownersOption.id === pets.owner.id ? pets.owner : ownersOption"
                v-for="ownersOption in owners"
                :key="ownersOption.id"
              >
                {{ ownersOption.id }}
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
            :disabled="$v.pets.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./pets-update.component.ts"></script>
