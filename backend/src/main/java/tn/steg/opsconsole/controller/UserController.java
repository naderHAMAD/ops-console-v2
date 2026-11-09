package tn.steg.opsconsole.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.steg.opsconsole.dto.request.CreateUserRequest;
import tn.steg.opsconsole.dto.response.UserDto;
import tn.steg.opsconsole.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> listAll() {
        return userService.listAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody CreateUserRequest request, Authentication authentication) {
        return userService.createUser(request, authentication);
    }

    @PatchMapping("/{id}/active")
    public UserDto setActive(
            @PathVariable UUID id,
            @RequestParam boolean active,
            Authentication authentication
    ) {
        return userService.setActive(id, active, authentication);
    }
}
