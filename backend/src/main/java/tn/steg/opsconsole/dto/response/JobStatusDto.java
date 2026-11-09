package tn.steg.opsconsole.dto.response;

import java.time.Instant;
import java.util.UUID;

public record JobStatusDto(
        UUID jobId,
        String targetName,     // hostname du serveur ou nom de la VM
        String status,         // pending | running | success | failed | rolled_back
        String lastLogLine,
        Instant startedAt,
        Instant finishedAt
) {}
