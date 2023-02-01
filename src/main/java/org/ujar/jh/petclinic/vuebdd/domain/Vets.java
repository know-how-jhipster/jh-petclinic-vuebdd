package org.ujar.jh.petclinic.vuebdd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Vets.
 */
@Table("vets")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "vets")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Vets implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    @Column("firstname")
    private String firstname;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    @Column("lastname")
    private String lastname;

    @Transient
    @JsonIgnoreProperties(value = { "vets" }, allowSetters = true)
    private Set<Specialties> specialties = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Vets id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public Vets firstname(String firstname) {
        this.setFirstname(firstname);
        return this;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public Vets lastname(String lastname) {
        this.setLastname(lastname);
        return this;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Set<Specialties> getSpecialties() {
        return this.specialties;
    }

    public void setSpecialties(Set<Specialties> specialties) {
        if (this.specialties != null) {
            this.specialties.forEach(i -> i.removeVet(this));
        }
        if (specialties != null) {
            specialties.forEach(i -> i.addVet(this));
        }
        this.specialties = specialties;
    }

    public Vets specialties(Set<Specialties> specialties) {
        this.setSpecialties(specialties);
        return this;
    }

    public Vets addSpecialty(Specialties specialties) {
        this.specialties.add(specialties);
        specialties.getVets().add(this);
        return this;
    }

    public Vets removeSpecialty(Specialties specialties) {
        this.specialties.remove(specialties);
        specialties.getVets().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vets)) {
            return false;
        }
        return id != null && id.equals(((Vets) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vets{" +
            "id=" + getId() +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            "}";
    }
}
