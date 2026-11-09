package tn.steg.opsconsole.dto.response;

import java.time.Instant;
import java.util.UUID;

public record VmDto(
        UUID id,
        String name,
        int cpu,
        int ramGb,
        int diskGb,
        String template,
        String status,
        String regionName,
        Instant createdAt
) {}
