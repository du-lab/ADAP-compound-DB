package org.dulab.adapcompounddb.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SearchParameters implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column
    private long userPrimaryId;

    @Column
    private String name;

    @Column
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getUserPrimaryId() {
        return userPrimaryId;
    }

    public void setUserPrimaryId(long userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
