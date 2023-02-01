<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="petclinicApp.vets.home.createOrEditLabel"
          data-cy="VetsCreateUpdateHeading"
          v-text="$t('petclinicApp.vets.home.createOrEditLabel')"
        >
          Create or edit a Vets
        </h2>
        <div>
          <div class="form-group" v-if="vets.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="vets.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.vets.firstname')" for="vets-firstname">Firstname</label>
            <input
              type="text"
              class="form-control"
              name="firstname"
              id="vets-firstname"
              data-cy="firstname"
              :class="{ valid: !$v.vets.firstname.$invalid, invalid: $v.vets.firstname.$invalid }"
              v-model="$v.vets.firstname.$model"
              required
            />
            <div v-if="$v.vets.firstname.$anyDirty && $v.vets.firstname.$invalid">
              <small class="form-text text-danger" v-if="!$v.vets.firstname.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.vets.firstname.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 32 })"
              >
                This field cannot be longer than 32 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.vets.lastname')" for="vets-lastname">Lastname</label>
            <input
              type="text"
              class="form-control"
              name="lastname"
              id="vets-lastname"
              data-cy="lastname"
              :class="{ valid: !$v.vets.lastname.$invalid, invalid: $v.vets.lastname.$invalid }"
              v-model="$v.vets.lastname.$model"
              required
            />
            <div v-if="$v.vets.lastname.$anyDirty && $v.vets.lastname.$invalid">
              <small class="form-text text-danger" v-if="!$v.vets.lastname.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.vets.lastname.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 32 })"
              >
                This field cannot be longer than 32 characters.
              </small>
            </div>
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
            :disabled="$v.vets.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./vets-update.component.ts"></script>
