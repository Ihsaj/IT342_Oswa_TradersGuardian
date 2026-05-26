package edu.cit.oswa.tradersguardian.repository;

import edu.cit.oswa.tradersguardian.entity.AccountSettings;
import edu.cit.oswa.tradersguardian.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountSettingsRepository extends JpaRepository<AccountSettings, Long> {
    Optional<AccountSettings> findByUser(User user);
}
