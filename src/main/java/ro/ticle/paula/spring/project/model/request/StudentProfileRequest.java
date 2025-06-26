package ro.ticle.paula.spring.project.model.request;

import jakarta.validation.constraints.NotEmpty;

public record StudentProfileRequest(
        @NotEmpty String address,
        @NotEmpty String phoneNumber
) {}
