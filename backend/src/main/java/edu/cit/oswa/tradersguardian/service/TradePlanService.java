package edu.cit.oswa.tradersguardian.service;

import org.springframework.stereotype.Service;

import edu.cit.oswa.tradersguardian.dto.DashboardStats;
import edu.cit.oswa.tradersguardian.dto.TradePlanRequest;
import edu.cit.oswa.tradersguardian.entity.TradePlan;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.repository.TradePlanRepository;

import java.util.List;

@Service
public class TradePlanService {

    private final TradePlanRepository repo;

    public TradePlanService(TradePlanRepository repo) {
        this.repo = repo;
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

    public void delete(Long id, User user) {
        TradePlan plan = repo.findById(id)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Trade plan not found"));
        repo.delete(plan);
    }

    public DashboardStats getStats(User user) {
        long total = repo.countByUser(user);
        long approved = repo.countByUserAndStatus(user, TradePlan.Status.APPROVED);
        long disapproved = repo.countByUserAndStatus(user, TradePlan.Status.DISAPPROVED);
        return new DashboardStats(total, approved, disapproved);
    }
}
