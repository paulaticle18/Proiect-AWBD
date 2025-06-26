package ro.ticle.paula.spring.project.model.response;

import java.util.UUID;

public record StudentProfileResponse(
        UUID id,
        String address,
        String phoneNumber,
        UUID studentId
) {}
