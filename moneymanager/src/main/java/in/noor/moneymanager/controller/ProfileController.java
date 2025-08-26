package in.noor.moneymanager.controller;

import in.noor.moneymanager.dto.AuthDTO;
import in.noor.moneymanager.dto.ProfileDTO;
import in.noor.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")

    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
     ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
     return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }
@GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Activated Successfully");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token is not valid or expired");
        }
}
@PostMapping("/login")
public ResponseEntity<Map<String ,Object>> login(@RequestBody AuthDTO authDTO) {
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message","Account is not active. Please activate your account first."
                ));
            }
            Map<String,Object> response = profileService.authenticatedAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                   "message",e.getMessage()
         ));
        }
}

@GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile() {
      ProfileDTO profileDTO =  profileService.getPublicProfile(null);
      return ResponseEntity.ok(profileDTO);
}


// Added my me deleted

}
