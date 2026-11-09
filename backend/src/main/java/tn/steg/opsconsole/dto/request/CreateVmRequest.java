package tn.steg.opsconsole.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateVmRequest(
        @NotBlank String name,
        @NotBlank String environment,      // prod | test | preprod
        @Min(1) @Max(32) int cpu,
        @Min(1) @Max(256) int ramGb,
        @Min(10) @Max(2000) int diskGb,
        @NotBlank String diskType,         // ssd | hdd
        @NotBlank String template,
        @NotBlank String network,          // ex: VLAN-SUD-PROD
        boolean backupEnabled,
        String description,
        @NotNull UUID regionId
) {}