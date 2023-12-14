package dev.knowhowto.jh.petclinic.vuebdd.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Vets} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VetsDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    private String firstname;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    private String lastname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VetsDTO)) {
            return false;
        }

        VetsDTO vetsDTO = (VetsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vetsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VetsDTO{" +
            "id=" + getId() +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            "}";
    }
}
