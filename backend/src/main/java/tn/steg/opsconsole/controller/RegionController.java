package tn.steg.opsconsole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.domain.Region;
import tn.steg.opsconsole.repository.RegionRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionRepository regionRepository;

    @GetMapping
    public List<Map<String, String>> listAll() {
        return regionRepository.findAll().stream()
                .map(r -> Map.of("id", r.getId().toString(), "name", r.getName()))
                .toList();
    }
}
