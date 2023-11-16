package dev.knowhowto.jh.petclinic.vuebdd.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Owners} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OwnersDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    private String firstname;

    @NotNull(message = "must not be null")
    @Size(max = 32)
    private String lastname;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String address;

    @Size(max = 32)
    private String city;

    @NotNull(message = "must not be null")
    @Size(max = 20)
    private String telephone;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OwnersDTO)) {
            return false;
        }

        OwnersDTO ownersDTO = (OwnersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ownersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OwnersDTO{" +
            "id=" + getId() +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", telephone='" + getTelephone() + "'" +
            "}";
    }
}
