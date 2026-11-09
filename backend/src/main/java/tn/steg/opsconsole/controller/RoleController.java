package tn.steg.opsconsole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.repository.RoleRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public List<Map<String, String>> listAll() {
        return roleRepository.findAll().stream()
                .map(r -> Map.of("id", r.getId().toString(), "name", r.getName()))
                .toList();
    }
}