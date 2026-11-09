package tn.steg.opsconsole.dto.response;

import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String fullName,
        boolean isActive,
        boolean is2faEnabled,
        String roleName,
        String regionName
) {}
