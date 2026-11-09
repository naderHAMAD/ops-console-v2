package tn.steg.opsconsole.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record TriggerRollbackRequest(
        @NotEmpty List<UUID> serverIds
) {}
