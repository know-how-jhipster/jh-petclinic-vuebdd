<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="petclinicApp.visits.home.createOrEditLabel"
          data-cy="VisitsCreateUpdateHeading"
          v-text="$t('petclinicApp.visits.home.createOrEditLabel')"
        >
          Create or edit a Visits
        </h2>
        <div>
          <div class="form-group" v-if="visits.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="visits.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.visits.visitdate')" for="visits-visitdate">Visitdate</label>
            <div class="d-flex">
              <input
                id="visits-visitdate"
                data-cy="visitdate"
                type="datetime-local"
                class="form-control"
                name="visitdate"
                :class="{ valid: !$v.visits.visitdate.$invalid, invalid: $v.visits.visitdate.$invalid }"
                required
                :value="convertDateTimeFromServer($v.visits.visitdate.$model)"
                @change="updateInstantField('visitdate', $event)"
              />
            </div>
            <div v-if="$v.visits.visitdate.$anyDirty && $v.visits.visitdate.$invalid">
              <small class="form-text text-danger" v-if="!$v.visits.visitdate.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.visits.visitdate.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.visits.description')" for="visits-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="visits-description"
              data-cy="description"
              :class="{ valid: !$v.visits.description.$invalid, invalid: $v.visits.description.$invalid }"
              v-model="$v.visits.description.$model"
              required
            />
            <div v-if="$v.visits.description.$anyDirty && $v.visits.description.$invalid">
              <small class="form-text text-danger" v-if="!$v.visits.description.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.visits.description.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 255 })"
              >
                This field cannot be longer than 255 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('petclinicApp.visits.pet')" for="visits-pet">Pet</label>
            <select class="form-control" id="visits-pet" data-cy="pet" name="pet" v-model="visits.pet">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="visits.pet && petsOption.id === visits.pet.id ? visits.pet : petsOption"
                v-for="petsOption in pets"
                :key="petsOption.id"
              >
                {{ petsOption.id }}
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
            :disabled="$v.visits.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./visits-update.component.ts"></script>
