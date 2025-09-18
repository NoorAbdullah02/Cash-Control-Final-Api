package in.noor.moneymanager.repository;

import in.noor.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long> {

    // Select *from tbl_Profiles where email
       Optional <ProfileEntity> findByEmail(String email);
       // activation token?

       Optional <ProfileEntity> findByActivationToken(String activationToken);

       // reset token for Forgotton Password

         Optional <ProfileEntity> findByResetToken(String resetToken);
}
