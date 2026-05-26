package edu.cit.oswa.tradersguardian.service;

import org.springframework.stereotype.Service;

import edu.cit.oswa.tradersguardian.dto.DashboardStats;
import edu.cit.oswa.tradersguardian.dto.TradePlanRequest;
import edu.cit.oswa.tradersguardian.entity.AccountSettings;
import edu.cit.oswa.tradersguardian.entity.TradePlan;
import edu.cit.oswa.tradersguardian.entity.TradePlan.Outcome;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.repository.TradePlanRepository;

import java.util.List;

@Service
public class TradePlanService {

    private final TradePlanRepository repo;
    private final AccountSettingsService settingsService;

    public TradePlanService(TradePlanRepository repo, AccountSettingsService settingsService) {
        this.repo = repo;
        this.settingsService = settingsService;
    }

    public TradePlan create(User user, TradePlanRequest request) {
        TradePlan plan = new TradePlan();
        plan.setUser(user);
        plan.setSymbol(request.getSymbol());
        plan.setTradeType(request.getTradeType());
        plan.setEntryPrice(request.getEntryPrice());
        plan.setStopLoss(request.getStopLoss());
        plan.setTakeProfit(request.getTakeProfit());
        plan.setPositionSize(request.getPositionSize());
        plan.setRiskAmount(request.getRiskAmount());
        plan.setRiskPercent(request.getRiskPercent());
        plan.setNotes(request.getNotes());
        plan.setStatus(TradePlan.Status.PENDING);
        return repo.save(plan);
    }

    public List<TradePlan> getAllForUser(User user) {
        return repo.findByUserOrderByCreatedAtDesc(user);
    }

    public TradePlan approve(Long id, User user) {
        TradePlan plan = repo.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Trade plan not found"));
        plan.setStatus(TradePlan.Status.APPROVED);
        plan.setDisapprovalReason(null);
        return repo.save(plan);
    }

    public TradePlan disapprove(Long id, User user, String reason) {
        TradePlan plan = repo.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Trade plan not found"));
        plan.setStatus(TradePlan.Status.DISAPPROVED);
        plan.setDisapprovalReason(reason);
        return repo.save(plan);
    }

    /**
     * Record whether an approved trade was a WIN or LOSS.
     * If it's a LOSS, add the actual loss amount to the user's currentDailyLoss.
     */
    public TradePlan recordOutcome(Long id, User user, Outcome outcome, Double profitLossAmount) {
        TradePlan plan = repo.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .filter(p -> p.getStatus() == TradePlan.Status.APPROVED)
                .orElseThrow(() -> new RuntimeException("Approved trade plan not found"));

        // Undo previous outcome effect if re-recording
        boolean hadPreviousLoss = plan.getOutcome() == Outcome.LOSS;
        Double previousLossAmount = plan.getProfitLossAmount();
        plan.setOutcome(outcome);
        plan.setProfitLossAmount(profitLossAmount);

        // Update daily loss in account settings
        AccountSettings settings = settingsService.getOrCreate(user);
        double current = settings.getCurrentDailyLoss();

        if (hadPreviousLoss && previousLossAmount != null && previousLossAmount < 0) {
            // Undo the previous loss contribution (convert to positive for subtraction)
            current = Math.max(0, current + previousLossAmount);
        }
        if (outcome == Outcome.LOSS && profitLossAmount != null && profitLossAmount < 0) {
            // Add the new loss amount (convert to positive for addition)
            current = current + Math.abs(profitLossAmount);
        }
        settings.setCurrentDailyLoss(current);
        settingsService.save(settings);

        return repo.save(plan);
    }

    public void delete(Long id, User user) {
        TradePlan plan = repo.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Trade plan not found"));

        // If this was a loss trade, undo its contribution to daily loss
        if (plan.getOutcome() == Outcome.LOSS && plan.getProfitLossAmount() != null && plan.getProfitLossAmount() < 0) {
            AccountSettings settings = settingsService.getOrCreate(user);
            double current = Math.max(0, settings.getCurrentDailyLoss() + plan.getProfitLossAmount());
            settings.setCurrentDailyLoss(current);
            settingsService.save(settings);
        }

        repo.delete(plan);
    }

    public DashboardStats getStats(User user) {
        long total = repo.countByUser(user);
        long approved = repo.countByUserAndStatus(user, TradePlan.Status.APPROVED);
        long disapproved = repo.countByUserAndStatus(user, TradePlan.Status.DISAPPROVED);
        long wins = repo.countByUserAndOutcome(user, Outcome.WIN);
        long losses = repo.countByUserAndOutcome(user, Outcome.LOSS);

        AccountSettings settings = settingsService.getOrCreate(user);
        return new DashboardStats(total, approved, disapproved, wins, losses,
                settings.getCurrentDailyLoss(), settings.getDailyLossLimit(), settings.getAccountBalance());
    }
}
