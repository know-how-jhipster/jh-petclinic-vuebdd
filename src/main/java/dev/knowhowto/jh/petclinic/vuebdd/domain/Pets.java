package dev.knowhowto.jh.petclinic.vuebdd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Pets.
 */
@Table("pets")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "pets")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pets implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("birthdate")
    private LocalDate birthdate;

    @Transient
    @JsonIgnoreProperties(value = { "pet" }, allowSetters = true)
    private Set<Visits> visits = new HashSet<>();

    @Transient
    private Types type;

    @Transient
    @JsonIgnoreProperties(value = { "pets" }, allowSetters = true)
    private Owners owner;

    @Column("type_id")
    private Long typeId;

    @Column("owner_id")
    private Long ownerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pets id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Pets name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public Pets birthdate(LocalDate birthdate) {
        this.setBirthdate(birthdate);
        return this;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Set<Visits> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<Visits> visits) {
        if (this.visits != null) {
            this.visits.forEach(i -> i.setPet(null));
        }
        if (visits != null) {
            visits.forEach(i -> i.setPet(this));
        }
        this.visits = visits;
    }

    public Pets visits(Set<Visits> visits) {
        this.setVisits(visits);
        return this;
    }

    public Pets addVisits(Visits visits) {
        this.visits.add(visits);
        visits.setPet(this);
        return this;
    }

    public Pets removeVisits(Visits visits) {
        this.visits.remove(visits);
        visits.setPet(null);
        return this;
    }

    public Types getType() {
        return this.type;
    }

    public void setType(Types types) {
        this.type = types;
        this.typeId = types != null ? types.getId() : null;
    }

    public Pets type(Types types) {
        this.setType(types);
        return this;
    }

    public Owners getOwner() {
        return this.owner;
    }

    public void setOwner(Owners owners) {
        this.owner = owners;
        this.ownerId = owners != null ? owners.getId() : null;
    }

    public Pets owner(Owners owners) {
        this.setOwner(owners);
        return this;
    }

    public Long getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Long types) {
        this.typeId = types;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long owners) {
        this.ownerId = owners;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pets)) {
            return false;
        }
        return id != null && id.equals(((Pets) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pets{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", birthdate='" + getBirthdate() + "'" +
            "}";
    }
}
