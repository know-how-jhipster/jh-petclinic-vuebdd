package org.ujar.jh.petclinic.vuebdd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Visits.
 */
@Table("visits")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "visits")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Visits implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("visitdate")
    private Instant visitdate;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "visits", "type", "owner" }, allowSetters = true)
    private Pets pet;

    @Column("pet_id")
    private Long petId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Visits id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getVisitdate() {
        return this.visitdate;
    }

    public Visits visitdate(Instant visitdate) {
        this.setVisitdate(visitdate);
        return this;
    }

    public void setVisitdate(Instant visitdate) {
        this.visitdate = visitdate;
    }

    public String getDescription() {
        return this.description;
    }

    public Visits description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Pets getPet() {
        return this.pet;
    }

    public void setPet(Pets pets) {
        this.pet = pets;
        this.petId = pets != null ? pets.getId() : null;
    }

    public Visits pet(Pets pets) {
        this.setPet(pets);
        return this;
    }

    public Long getPetId() {
        return this.petId;
    }

    public void setPetId(Long pets) {
        this.petId = pets;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visits)) {
            return false;
        }
        return id != null && id.equals(((Visits) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visits{" +
            "id=" + getId() +
            ", visitdate='" + getVisitdate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
