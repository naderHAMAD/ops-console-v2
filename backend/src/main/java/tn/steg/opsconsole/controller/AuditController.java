package tn.steg.opsconsole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.dto.response.AuditLogDto;
import tn.steg.opsconsole.service.AuditService;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public List<AuditLogDto> listRecent() {
        return auditService.listRecent();
    }
}
