package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;@NotBlank(message = "The username is required.")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String affiliation;

    private String email;

    private String message;

    @Column(name="submit_date")
    private Date submitDate;

}
