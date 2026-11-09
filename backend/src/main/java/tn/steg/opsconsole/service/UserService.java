package tn.steg.opsconsole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.steg.opsconsole.domain.Region;
import tn.steg.opsconsole.domain.Role;
import tn.steg.opsconsole.domain.User;
import tn.steg.opsconsole.dto.request.CreateUserRequest;
import tn.steg.opsconsole.dto.response.UserDto;
import tn.steg.opsconsole.repository.RegionRepository;
import tn.steg.opsconsole.repository.RoleRepository;
import tn.steg.opsconsole.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<UserDto> listAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto createUser(CreateUserRequest request, Authentication authentication) {
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Utilisateur courant introuvable"));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable"));
        Region region = request.regionId() != null
                ? regionRepository.findById(request.regionId())
                    .orElseThrow(() -> new IllegalArgumentException("Région introuvable"))
                : null;

        User newUser = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .passwordHash(passwordEncoder.encode(request.temporaryPassword()))
                .isActive(true)
                .role(role)
                .region(region)
                .build();
        newUser = userRepository.save(newUser);

        auditService.log(admin, "USER_CREATED", "User", newUser.getId(),
                "Rôle: " + role.getName());

        return toDto(newUser);
    }

    public UserDto setActive(UUID userId, boolean active, Authentication authentication) {
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Utilisateur courant introuvable"));
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        target.setActive(active);
        userRepository.save(target);

        auditService.log(admin, active ? "USER_ACTIVATED" : "USER_DEACTIVATED", "User", target.getId(), null);
        return toDto(target);
    }

    private UserDto toDto(User u) {
        return new UserDto(
                u.getId(), u.getEmail(), u.getFullName(), u.isActive(), u.is2faEnabled(),
                u.getRole() != null ? u.getRole().getName() : null,
                u.getRegion() != null ? u.getRegion().getName() : null
        );
    }
}
