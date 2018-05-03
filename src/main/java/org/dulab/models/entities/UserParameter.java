package org.dulab.models.entities;

import org.dulab.models.UserParameterType;
import org.dulab.validation.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class UserParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private Long userPrincipalId;

    @NotBlank(message = "Parameter identifier is required.")
    private String identifier;

    @NotBlank(message = "Parameter value is required.")
    private String value;

    @NotNull(message = "Parameter type is required.")
    private UserParameterType type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserPrincipalId() {
        return userPrincipalId;
    }

    public void setUserPrincipalId(Long userPrincipalId) {
        this.userPrincipalId = userPrincipalId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Enumerated(EnumType.STRING)
    public UserParameterType getType() {
        return type;
    }

    public void setType(UserParameterType type) {
        this.type = type;
    }

    @Transient
    public Object getObject() {
        return type == null ? value : type.fromString(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof UserParameter)) return false;
        return id == ((UserParameter) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return identifier + " = " + value;
    }
}
