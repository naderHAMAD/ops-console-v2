package tn.steg.opsconsole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.dto.response.ServerDto;
import tn.steg.opsconsole.service.ServerService;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @GetMapping
    public List<ServerDto> listAll() {
        return serverService.listAll();
    }
}
