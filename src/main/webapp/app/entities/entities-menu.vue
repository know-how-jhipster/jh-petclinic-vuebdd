<template>
  <div>
    <b-dropdown-item to="/vets">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.vets')">Vets</span>
    </b-dropdown-item>
    <b-dropdown-item to="/specialties">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.specialties')">Specialties</span>
    </b-dropdown-item>
    <b-dropdown-item to="/types">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.types')">Types</span>
    </b-dropdown-item>
    <b-dropdown-item to="/owners">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.owners')">Owners</span>
    </b-dropdown-item>
    <b-dropdown-item to="/pets">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.pets')">Pets</span>
    </b-dropdown-item>
    <b-dropdown-item to="/visits">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.visits')">Visits</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
