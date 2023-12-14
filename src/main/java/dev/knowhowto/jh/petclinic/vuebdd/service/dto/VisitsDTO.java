package dev.knowhowto.jh.petclinic.vuebdd.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Visits} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VisitsDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant visitdate;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String description;

    private PetsDTO pet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getVisitdate() {
        return visitdate;
    }

    public void setVisitdate(Instant visitdate) {
        this.visitdate = visitdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PetsDTO getPet() {
        return pet;
    }

    public void setPet(PetsDTO pet) {
        this.pet = pet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitsDTO)) {
            return false;
        }

        VisitsDTO visitsDTO = (VisitsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, visitsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitsDTO{" +
            "id=" + getId() +
            ", visitdate='" + getVisitdate() + "'" +
            ", description='" + getDescription() + "'" +
            ", pet=" + getPet() +
            "}";
    }
}
