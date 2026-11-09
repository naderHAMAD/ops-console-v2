package tn.steg.opsconsole.service;

import dev.samstevens.totp.code.CodeVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tn.steg.opsconsole.domain.User;
import tn.steg.opsconsole.dto.request.LoginRequest;
import tn.steg.opsconsole.dto.request.VerifyOtpRequest;
import tn.steg.opsconsole.dto.response.AuthResponse;
import tn.steg.opsconsole.repository.UserRepository;
import tn.steg.opsconsole.security.JwtService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CodeVerifier totpVerifier;

    public AuthResponse login(LoginRequest request) {
        // Vérifie email + mot de passe (lève une exception si invalide)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

        if (user.is2faEnabled()) {
            // Le frontend doit maintenant appeler /api/auth/verify-otp
            return AuthResponse.otpChallenge();
        }

        return buildTokens(user);
    }

    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

        boolean valid = totpVerifier.isValidCode(user.getTotpSecret(), request.code());
        if (!valid) {
            throw new IllegalArgumentException("Code de vérification invalide");
        }

        return buildTokens(user);
    }
    /** Émet un nouvel access token à partir d'un refresh token encore valide. */
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken); // lève une exception si expiré/invalide

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Refresh token invalide ou expiré");
        }

        return buildTokens(user);
    }

    private AuthResponse buildTokens(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("region", user.getRegion() != null ? user.getRegion().getName() : null);

        String accessToken = jwtService.generateAccessToken(userDetails, claims);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken,
                false,
                user.getFullName(),
                user.getRole().getName(),
                user.getRegion() != null ? user.getRegion().getName() : null
        );
    }
}
