package tn.steg.opsconsole.dto.response;

import java.util.UUID;

public record UpdateResultDto(
        UUID jobId,
        String hostname,
        String status,      // success | failed
        String logs
) {}