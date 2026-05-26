package edu.cit.oswa.tradersguardian.repository;

import edu.cit.oswa.tradersguardian.entity.TradePlan;
import edu.cit.oswa.tradersguardian.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradePlanRepository extends JpaRepository<TradePlan, Long> {
    List<TradePlan> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
    long countByUserAndStatus(User user, TradePlan.Status status);
    long countByUserAndOutcome(User user, TradePlan.Outcome outcome);
}
