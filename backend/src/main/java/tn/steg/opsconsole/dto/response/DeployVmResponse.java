package tn.steg.opsconsole.dto.response;

import java.util.UUID;

public record DeployVmResponse(
        UUID jobId,
        String infoFile,
        String message
) {
}