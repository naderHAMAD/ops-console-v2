package tn.steg.opsconsole.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ServerDto(
        UUID id,
        String hostname,
        String ipAddress,
        String jbossVersion,
        String status,
        Instant lastUpdate,
        String regionName
) {}
