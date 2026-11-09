package tn.steg.opsconsole.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AuditLogDto(
        UUID id,
        String userFullName,
        String action,
        String targetType,
        String details,
        Instant timestamp
) {}
