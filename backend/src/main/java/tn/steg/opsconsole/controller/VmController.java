package tn.steg.opsconsole.controller;
import tn.steg.opsconsole.dto.request.DeployWithPlaybookRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.dto.request.CreateVmRequest;
import tn.steg.opsconsole.dto.response.DeployVmResponse;
import tn.steg.opsconsole.dto.response.VmDto;
import tn.steg.opsconsole.service.VmService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vm")
@RequiredArgsConstructor
public class VmController {

    private final VmService vmService;

    @GetMapping
    public List<VmDto> listAll() {
        return vmService.listAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(
            @Valid @RequestBody CreateVmRequest request,
            Authentication authentication
    ) {
        DeployVmResponse response = vmService.createVm(request, authentication);
        return ResponseEntity.accepted().body(Map.of("jobId", response.jobId()));
    }
    
    
    private final tn.steg.opsconsole.service.AnsibleRunnerService ansibleRunnerService;

    @PostMapping("/deploy-with-playbook")
    public ResponseEntity<Map<String, String>> deployWithPlaybook(
            @Valid @RequestBody DeployWithPlaybookRequest request
    ) {
        try {
            String output = ansibleRunnerService.runUploadedPlaybook(
                    request.playbookContent(), request.vmSpecJson()
            );
            return ResponseEntity.ok(Map.of("output", output));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("output", "Erreur: " + e.getMessage()));
        }
    }
    
}
