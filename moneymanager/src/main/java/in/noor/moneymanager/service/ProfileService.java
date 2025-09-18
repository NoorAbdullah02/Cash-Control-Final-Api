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
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        String activationLink = activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your CashControl account";
        String body = "Click on the following link to activate your CashControl account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
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

        return true;
    }


}
