package tn.steg.opsconsole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.steg.opsconsole.domain.AuditLog;
import tn.steg.opsconsole.domain.User;
import tn.steg.opsconsole.dto.response.AuditLogDto;
import tn.steg.opsconsole.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String targetType, UUID targetId, String details) {
        AuditLog entry = AuditLog.builder()
                .user(user)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .details(details)
                .build();
        auditLogRepository.save(entry);
    }

    public List<AuditLogDto> listRecent() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc().stream()
                .map(e -> new AuditLogDto(
                        e.getId(),
                        e.getUser() != null ? e.getUser().getFullName() : "système",
                        e.getAction(),
                        e.getTargetType(),
                        e.getDetails(),
                        e.getTimestamp()
                ))
                .toList();
    }
}
