package dev.knowhowto.jh.petclinic.vuebdd.domain;

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
 * A Specialties.
 */
@Table("specialties")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "specialties")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Specialties implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    @Column("name")
    private String name;

    @Transient
    @JsonIgnoreProperties(value = { "specialties" }, allowSetters = true)
    private Set<Vets> vets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Specialties id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Specialties name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Vets> getVets() {
        return this.vets;
    }

    public void setVets(Set<Vets> vets) {
        this.vets = vets;
    }

    public Specialties vets(Set<Vets> vets) {
        this.setVets(vets);
        return this;
    }

    public Specialties addVet(Vets vets) {
        this.vets.add(vets);
        vets.getSpecialties().add(this);
        return this;
    }

    public Specialties removeVet(Vets vets) {
        this.vets.remove(vets);
        vets.getSpecialties().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Specialties)) {
            return false;
        }
        return id != null && id.equals(((Specialties) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Specialties{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
