package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.dulab.adapcompounddb.validation.Email;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter(value=AccessLevel.PUBLIC)
@Setter(value=AccessLevel.PUBLIC)
@RequiredArgsConstructor
public class FeedbackDTO implements Serializable {

    private Integer id;

    @NotBlank(message = "Please provide your Name.")
    private String name;

    private String affiliation;

    @NotBlank(message = "Please provide your Email for contact.")
    @Email
    private String email;

    @NotBlank(message = "Your Feedback Message is required.")
    private String message;

    private String submitDate;

    private Boolean read;
}