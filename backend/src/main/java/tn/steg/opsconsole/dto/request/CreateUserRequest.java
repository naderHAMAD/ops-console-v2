package tn.steg.opsconsole.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotBlank String fullName,
        @NotBlank String temporaryPassword,
        @NotNull UUID roleId,
        UUID regionId // peut être null pour un rôle national
) {}
