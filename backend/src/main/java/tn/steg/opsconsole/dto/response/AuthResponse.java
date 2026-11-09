package tn.steg.opsconsole.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        boolean otpRequired,
        String fullName,
        String role,
        String region
) {
    public static AuthResponse otpChallenge() {
        return new AuthResponse(null, null, true, null, null, null);
    }
}
