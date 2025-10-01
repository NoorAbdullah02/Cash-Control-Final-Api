package in.noor.moneymanager.service;

import in.noor.moneymanager.dto.AuthDTO;
import in.noor.moneymanager.dto.ProfileDTO;
import in.noor.moneymanager.entity.ProfileEntity;
import in.noor.moneymanager.repository.ProfileRepository;
import in.noor.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;



    @Value("${app.activation.url}")
    private String activationURL;


    @Value("${money.manager.frontend.url_forgot_password}")
    private String frontendURL_forResetToken;

    @Transactional
//    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
//        ProfileEntity newProfile = toEntity(profileDTO);
//        newProfile.setActivationToken(UUID.randomUUID().toString());
//        newProfile = profileRepository.save(newProfile);
//
//        String activationLink = activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
//        String subject = "Activate your CashControl account";
//        String body = "Click on the following link to activate your CashControl account: " + activationLink;
//        emailService.sendEmail(newProfile.getEmail(), subject, body);
//
//        return toDTO(newProfile);
//    }


    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        // Create and save profile
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setActivationTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Token expires in 15 minites
        newProfile = profileRepository.save(newProfile);

        // Send beautiful activation email
        sendActivationEmail(newProfile);

        return toDTO(newProfile);
    }

    private void sendActivationEmail(ProfileEntity profile) {
        String activationLink = activationURL + "/api/v1.0/activate?token=" + profile.getActivationToken();

        String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f7fa;">
            <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f4f7fa;">
                <tr>
                    <td style="padding: 40px 20px;">
                        <table role="presentation" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1);">
                            
                            <!-- Header -->
                            <tr>
                                <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 30px; text-align: center;">
                                    <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: 700;">💰 CashControl</h1>
                                    <p style="margin: 10px 0 0; color: rgba(255,255,255,0.9); font-size: 16px;">Your Financial Journey Starts Here</p>
                                </td>
                            </tr>
                            
                            <!-- Main Content -->
                            <tr>
                                <td style="padding: 50px 40px;">
                                    <h2 style="margin: 0 0 20px; color: #333333; font-size: 26px; font-weight: 600;">Welcome Aboard! 🎉</h2>
                                    <p style="margin: 0 0 20px; color: #666666; font-size: 16px; line-height: 1.6;">
                                        Hi %s,<br><br>
                                        We're thrilled to have you join CashControl! You're just one click away from taking control of your finances.
                                    </p>
                                    <p style="margin: 0 0 30px; color: #666666; font-size: 16px; line-height: 1.6;">
                                        To get started, please activate your account by clicking the button below:
                                    </p>
                                    
                                    <!-- CTA Button -->
                                    <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                                        <tr>
                                            <td style="text-align: center; padding: 0 0 30px;">
                                                <a href="%s" style="display: inline-block; padding: 16px 48px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: 600; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                                    Activate My Account
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
                                    
                                    <div style="height: 1px; background: linear-gradient(to right, transparent, #e0e0e0, transparent); margin: 30px 0;"></div>
                                    
                                    <p style="margin: 0 0 10px; color: #999999; font-size: 14px;">Button not working? Copy this link:</p>
                                    <p style="margin: 0 0 30px; color: #667eea; font-size: 13px; word-break: break-all; background-color: #f8f9fa; padding: 12px; border-radius: 8px; border-left: 4px solid #667eea;">%s</p>
                                    
                                    <table role="presentation" style="width: 100%%; background-color: #fff9e6; border-radius: 8px; border-left: 4px solid #ffc107;">
                                        <tr>
                                            <td style="padding: 15px 20px;">
                                                <p style="margin: 0; color: #856404; font-size: 14px;">🔒 <strong>Security Note:</strong> This link expires in 15 minutes. If you didn't create this account, please ignore this email.</p>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                            
                            <!-- Footer -->
                            <tr>
                                <td style="padding: 30px 40px; text-align: center; background-color: #667eea; color: #ffffff;">
                                    <p style="margin: 0 0 15px; font-size: 14px; opacity: 0.9;">Need help? We're here for you!</p>
                                    <p style="margin: 15px 0 0; font-size: 12px; opacity: 0.8;">© 2025 CashControl. All rights reserved.</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.formatted(profile.getFullName(), activationLink, activationLink);

        emailService.sendHtmlEmail(
                profile.getEmail(),
                "🎉 Welcome to CashControl - Activate Your Account",
                htmlTemplate
        );
    }


    public void resendActivationEmail(String email) {
        // Find profile by email
        ProfileEntity profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email address"));

        // Check if already activated
        if (profile.getIsActive()) {
            throw new RuntimeException("This account is already activated. Please login.");
        }

        // Generate new token (for security, always generate a fresh token)
        profile.setActivationToken(UUID.randomUUID().toString());
        profile.setActivationTokenExpiry(LocalDateTime.now().plusMinutes(15));
        profileRepository.save(profile);

        // Resend activation email using existing method
        sendActivationEmail(profile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
      Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
      return profileRepository.findByEmail(authentication.getName())
              .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email : " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if (email == null) {
           currentUser =  getCurrentProfile();
        }else{
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email : " + email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }


    public Map<String, Object> authenticatedAndGenerateToken(AuthDTO authDTO) {
        try{
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
        //Generate Token
          String token =  jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token",token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        }catch (Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }



   // String activationLink = activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
//   public String forgotPassword(String email) {
//       ProfileEntity profile = profileRepository.findByEmail(email)
//               .orElseThrow(() -> new RuntimeException("Email not found"));
//
//       String resetToken = UUID.randomUUID().toString();
//       profile.setResetToken(resetToken);
//       profile.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
//       profileRepository.save(profile);
//
//       // send mail
//       //String link = activationURL + "/api/v1.0/reset-password?token=" + resetToken;
//       String link = resetToken;
//
//
//       emailService.sendEmail(profile.getEmail(), "Reset Your Password",
//               "Hello " + profile.getFullName() + ",\n\nCopy the token number and reset your password:\n" + link);
//
//       return resetToken;
//   }


    public String forgotPassword(String email) {
        ProfileEntity profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String resetToken = UUID.randomUUID().toString();
        profile.setResetToken(resetToken);
        profile.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        profileRepository.save(profile);

        // Reset link (frontend handles token)
        String resetLink = frontendURL_forResetToken+"/reset-password?token=" + resetToken;


      //  String activationLink = activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();

        String emailContent =
                "<!DOCTYPE html>" +
                        "<html><body style='font-family: Arial, sans-serif;'>" +
                        "<h2>Password Reset Request</h2>" +
                        "<p>Hello <b>" + profile.getFullName() + "</b>,</p>" +
                        "<p>Your reset token (valid for 1 hour):</p>" +
                        "<div style='padding:12px;background:#f4f6f8;border:1px dashed #888;" +
                        "font-family:monospace;font-size:16px;text-align:center;'>" +
                        resetToken +
                        " <a href='" + resetLink + "' style='text-decoration:none;font-size:18px;margin-left:8px;'>📋</a>" +
                        "</div>" +
                        "<p><a href='" + resetLink + "' style='display:inline-block;" +
                        "padding:12px 20px;background:#007bff;color:white;text-decoration:none;" +
                        "border-radius:5px;'>Click Here to Reset Password</a></p>" +
                        "<p style='font-size:12px;color:#555;'>If you didn’t request this, ignore this email.</p>" +
                        "</body></html>";

        emailService.sendEmailForgotPassword(
                profile.getEmail(),
                "Reset Your Password",
                emailContent
        );

        return resetToken;
    }




//    public boolean resetPassword(String token, String newPassword) {
//        ProfileEntity profile = profileRepository.findByResetToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
//
//        if (profile.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("Reset token expired");
//        }
//
//        profile.setPassword(passwordEncoder.encode(newPassword));
//        profile.setResetToken(null);
//        profile.setResetTokenExpiry(null);
//        profileRepository.save(profile);
//
//        return true;
//    }


    public boolean resetPassword(String token, String newPassword) {
        ProfileEntity profile = profileRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (profile.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        profile.setPassword(passwordEncoder.encode(newPassword));
        profile.setResetToken(null);
        profile.setResetTokenExpiry(null);
        profileRepository.save(profile);

        // Build login URL from property
        String loginUrl = frontendURL_forResetToken + "/login";

        // Send success email
        String subject = "✅ Your Password Has Been Reset Successfully!";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; background-color: #f4f4f7; padding: 40px;">
              <div style="max-width: 600px; margin: auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                <div style="background: linear-gradient(90deg, #4CAF50, #2E7D32); padding: 20px; text-align: center;">
                  <h1 style="color: white; margin: 0;">Cash Control</h1>
                </div>
                <div style="padding: 30px; text-align: center;">
                  <h2 style="color: #333;">Password Reset Successful 🎉</h2>
                  <p style="color: #555; font-size: 16px;">
                    Hi <b>%s</b>,<br><br>
                    Your password has been <strong>successfully reset</strong>. If this wasn’t you, please secure your account immediately.
                  </p>
                  <a href="%s" style="display:inline-block; margin-top:20px; padding: 12px 20px; background: #4CAF50; color: white; text-decoration: none; border-radius: 6px; font-weight: bold;">Login Now</a>
                </div>
                <div style="background: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #777;">
                  © 2025 Cash Control. All rights reserved.
                </div>
              </div>
            </div>
            """.formatted(profile.getFullName(), loginUrl);

        emailService.sendEmailForgotPassword(profile.getEmail(), subject, htmlContent);

        return true;
    }

    // For update name and profile image

    @Transactional
    public ProfileDTO updateProfileImage(String imageUrl) {
        ProfileEntity currentUser = getCurrentProfile(); // fetch from SecurityContext
        currentUser.setProfileImageUrl(imageUrl);
        currentUser.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(currentUser);

        return toDTO(currentUser);
    }

    @Transactional
    public ProfileDTO updateProfileName(String fullName) {
        ProfileEntity currentUser = getCurrentProfile(); // fetch from SecurityContext
        currentUser.setFullName(fullName);
        currentUser.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(currentUser);

        return toDTO(currentUser);
    }




}
