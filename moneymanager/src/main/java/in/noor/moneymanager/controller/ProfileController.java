// Profile Controller
package in.noor.moneymanager.controller;

import in.noor.moneymanager.dto.AuthDTO;
import in.noor.moneymanager.dto.ProfileDTO;
import in.noor.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
//@RequestMapping("/api/v1.0") // UNCOMMENT THIS LINE
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Value("${money.manager.frontend.url_forgot_password}")
    private String frontendURL_for_activate;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    // For Update Profile Image and Name

    @PutMapping("/profile/image")
    public ResponseEntity<ProfileDTO> updateProfileImage(
            @RequestBody Map<String, String> request
    ) {
        String imageUrl = request.get("profileImageUrl");
        ProfileDTO updatedProfile = profileService.updateProfileImage(imageUrl);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/profile/name")
    public ResponseEntity<ProfileDTO> updateProfileName(
            @RequestBody Map<String, String> request
    ) {
        String fullName = request.get("fullName");
        ProfileDTO updatedProfile = profileService.updateProfileName(fullName);
        return ResponseEntity.ok(updatedProfile);
    }


// Closed that

//    @GetMapping("/activate")
//    public ResponseEntity<String> activateProfile(@RequestParam String token) {
//        boolean isActivated = profileService.activateProfile(token);
//        if (isActivated) {
//            return ResponseEntity.ok("Activated Successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token is not valid or expired");
//        }
//    }



//    @GetMapping("/activate")
//    public void activateProfile(
//            @RequestParam String token,
//            HttpServletResponse response
//    ) throws IOException {
//
//        boolean isActivated = profileService.activateProfile(token);
//
//        if (isActivated) {
//            response.sendRedirect(frontendURL_for_activate + "/activation-success");
//        } else {
//            response.sendRedirect(frontendURL_for_activate + "/activation-failed");
//        }
//    }


    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        try {
            boolean isActivated = profileService.activateProfile(token);

            String redirectUrl = isActivated
                    ? frontendURL_for_activate + "/activation-success"
                    : frontendURL_for_activate + "/activation-failed";

            String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="refresh" content="0; url=%s">
                <script>window.location.href='%s';</script>
            </head>
            <body>
                <p>Redirecting...</p>
            </body>
            </html>
            """.formatted(redirectUrl, redirectUrl);

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(html);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body("<html><body><h1>Error: " + e.getMessage() + "</h1></body></html>");
        }
    }




    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account is not active. Please activate your account first."
                ));
            }
            Map<String, Object> response = profileService.authenticatedAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile() {
        ProfileDTO profileDTO = profileService.getPublicProfile(null);
        return ResponseEntity.ok(profileDTO);
    }

    //Forgot Password - REMOVE @CrossOrigin since it's handled by SecurityConfig
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

// Add validation
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is required"));
        }

        try {
            String token = profileService.forgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "Reset link sent to email", "token", token));
        } catch (Exception e) {
// Log the full error for debugging
            System.err.println("Forgot password error: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Reset Password
//    @PostMapping("/reset-password")
//    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
//        String token = request.get("token");
//        String newPassword = request.get("newPassword");
//
//// Add validation
//        if (token == null || token.trim().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("message", "Token is required"));
//        }
//        if (newPassword == null || newPassword.trim().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("message", "New password is required"));
//        }
//
//        try {
//            boolean success = profileService.resetPassword(token, newPassword);
//            return ResponseEntity.ok(Map.of(
//                    "message", success ? "Password reset successfully" : "Invalid or expired token"
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("message", e.getMessage()));
//        }
//    }
@PostMapping("/reset-password")
public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
    log.info("Reset password request received with token in body");

    String token = request.get("token");
    String newPassword = request.get("newPassword");

    // Add validation
    if (token == null || token.trim().isEmpty()) {
        log.warn("Token is missing in request body");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Token is required"));
    }

    if (newPassword == null || newPassword.trim().isEmpty()) {
        log.warn("New password is missing in request body");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "New password is required"));
    }

    if (newPassword.trim().length() < 6) {
        log.warn("New password is too short");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Password must be at least 6 characters long"));
    }

    try {
        log.info("Processing password reset with token from body");
        boolean success = profileService.resetPassword(token.trim(), newPassword.trim());
        log.info("Password reset processed, success: {}", success);

        return ResponseEntity.ok(Map.of(
                "message", success ? "Password reset successfully" : "Invalid or expired token"
        ));
    } catch (Exception e) {
        log.error("Reset password error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }
}

}