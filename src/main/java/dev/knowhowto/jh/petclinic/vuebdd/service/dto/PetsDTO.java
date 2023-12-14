package dev.knowhowto.jh.petclinic.vuebdd.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Pets} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PetsDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    private String name;

    @NotNull(message = "must not be null")
    private LocalDate birthdate;

    private TypesDTO type;

    private OwnersDTO owner;

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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public TypesDTO getType() {
        return type;
    }

    public void setType(TypesDTO type) {
        this.type = type;
    }

    public OwnersDTO getOwner() {
        return owner;
    }

    public void setOwner(OwnersDTO owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PetsDTO)) {
            return false;
        }

        PetsDTO petsDTO = (PetsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, petsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PetsDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", birthdate='" + getBirthdate() + "'" +
            ", type=" + getType() +
            ", owner=" + getOwner() +
            "}";
    }
}
