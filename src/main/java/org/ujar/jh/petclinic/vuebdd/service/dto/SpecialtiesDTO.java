package org.ujar.jh.petclinic.vuebdd.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.ujar.jh.petclinic.vuebdd.domain.Specialties} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpecialtiesDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    private String name;

    private Set<VetsDTO> vets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<VetsDTO> getVets() {
        return vets;
    }

    public void setVets(Set<VetsDTO> vets) {
        this.vets = vets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpecialtiesDTO)) {
            return false;
        }

        SpecialtiesDTO specialtiesDTO = (SpecialtiesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, specialtiesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpecialtiesDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", vets=" + getVets() +
            "}";
    }
}
