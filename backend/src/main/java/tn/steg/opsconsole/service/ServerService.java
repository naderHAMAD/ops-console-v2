package tn.steg.opsconsole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.steg.opsconsole.dto.response.ServerDto;
import tn.steg.opsconsole.repository.ServerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;

    @Transactional(readOnly = true)
    public List<ServerDto> listAll() {
        return serverRepository.findAll().stream().map(this::toDto).toList();
    }

    private ServerDto toDto(tn.steg.opsconsole.domain.Server s) {
        return new ServerDto(
                s.getId(), s.getHostname(), s.getIpAddress(), s.getJbossVersion(),
                s.getStatus(), s.getLastUpdate(), s.getRegion().getName()
        );
    }
}
