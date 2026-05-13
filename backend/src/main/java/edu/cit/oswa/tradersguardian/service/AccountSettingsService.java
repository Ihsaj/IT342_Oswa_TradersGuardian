package edu.cit.oswa.tradersguardian.service;

import org.springframework.stereotype.Service;

import edu.cit.oswa.tradersguardian.dto.AccountSettingsRequest;
import edu.cit.oswa.tradersguardian.entity.AccountSettings;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.repository.AccountSettingsRepository;

@Service
public class AccountSettingsService {

    private final AccountSettingsRepository repo;

    public AccountSettingsService(AccountSettingsRepository repo) {
        this.repo = repo;
    }

    /**
     * Returns the settings for a user, creating defaults if none exist yet.
     */
    public AccountSettings getOrCreate(User user) {
        return repo.findByUser(user).orElseGet(() -> {
            AccountSettings settings = new AccountSettings();
            settings.setUser(user);
            return repo.save(settings);
        });
    }

    public AccountSettings update(User user, AccountSettingsRequest request) {
        AccountSettings settings = getOrCreate(user);
        settings.setAccountBalance(request.getAccountBalance());
        settings.setRiskPerTrade(request.getRiskPerTrade());
        settings.setDailyLossLimit(request.getDailyLossLimit());
        return repo.save(settings);
    }
}
