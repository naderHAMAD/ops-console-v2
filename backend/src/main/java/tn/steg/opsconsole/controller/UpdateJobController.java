package tn.steg.opsconsole.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.dto.request.TriggerUpdateRequest;
import tn.steg.opsconsole.dto.request.TriggerRollbackRequest;
import tn.steg.opsconsole.dto.response.UpdateResultDto;
import tn.steg.opsconsole.service.UpdateJobService;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/update")
@RequiredArgsConstructor
public class UpdateJobController {

    private final UpdateJobService updateJobService;

    @PostMapping
    public ResponseEntity<List<UpdateResultDto>> trigger(
            @Valid @RequestBody TriggerUpdateRequest request,
            Authentication authentication
    ) {
        List<UpdateResultDto> results = updateJobService.triggerUpdate(request, authentication);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/rollback")
    public ResponseEntity<List<UpdateResultDto>> triggerRollback(
            @Valid @RequestBody TriggerRollbackRequest request,
            Authentication authentication
    ) {
        List<UpdateResultDto> results = updateJobService.triggerRollback(request, authentication);
        return ResponseEntity.ok(results);
    }
}