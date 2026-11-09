package tn.steg.opsconsole.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record TriggerUpdateRequest(
        @NotEmpty List<UUID> serverIds,
        @NotBlank String targetVersion
) {}
