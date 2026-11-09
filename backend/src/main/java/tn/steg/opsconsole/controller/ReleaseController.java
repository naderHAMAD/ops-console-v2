package tn.steg.opsconsole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.repository.ReleaseRepository;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleaseController {

    private final ReleaseRepository releaseRepository;

    @GetMapping
    public List<Map<String, Object>> listAll() {
        return releaseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(r -> Map.<String, Object>of(
                        "version", r.getVersion(),
                        "label", r.getLabel(),
                        "isStable", r.isStable()
                ))
                .toList();
    }
}