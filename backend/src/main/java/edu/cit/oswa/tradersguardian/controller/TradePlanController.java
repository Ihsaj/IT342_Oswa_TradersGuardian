package edu.cit.oswa.tradersguardian.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.cit.oswa.tradersguardian.dto.ApiResponse;
import edu.cit.oswa.tradersguardian.dto.DashboardStats;
import edu.cit.oswa.tradersguardian.dto.TradePlanRequest;
import edu.cit.oswa.tradersguardian.entity.TradePlan;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.exception.InvalidTokenException;
import edu.cit.oswa.tradersguardian.service.AuthService;
import edu.cit.oswa.tradersguardian.service.TradePlanService;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"})
public class TradePlanController {

    private final TradePlanService tradePlanService;
    private final AuthService authService;

    public TradePlanController(TradePlanService tradePlanService, AuthService authService) {
        this.tradePlanService = tradePlanService;
        this.authService = authService;
    }

    /** Shared token resolver — uses the never-expired clock from AuthService */
    private User resolveUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Missing or invalid authorization header");
        }
        try {
            String email = authService.parseEmailFromToken(authHeader.substring(7));
            return authService.getUserByEmail(email);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    /** GET /api/trades — list all trade plans for current user */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TradePlan>>> getAll(
            @RequestHeader("Authorization") String authHeader) {
        User user = resolveUser(authHeader);
        List<TradePlan> plans = tradePlanService.getAllForUser(user);
        return ResponseEntity.ok(ApiResponse.success("Trade plans fetched", plans));
    }

    /** POST /api/trades — create a new trade plan */
    @PostMapping
    public ResponseEntity<ApiResponse<TradePlan>> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TradePlanRequest request) {
        User user = resolveUser(authHeader);
        TradePlan plan = tradePlanService.create(user, request);
        return ResponseEntity.ok(ApiResponse.success("Trade plan created", plan));
    }

    /** PUT /api/trades/{id}/approve — approve a trade plan */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TradePlan>> approve(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        User user = resolveUser(authHeader);
        TradePlan plan = tradePlanService.approve(id, user);
        return ResponseEntity.ok(ApiResponse.success("Trade plan approved", plan));
    }

    /** PUT /api/trades/{id}/disapprove — disapprove a trade plan */
    @PutMapping("/{id}/disapprove")
    public ResponseEntity<ApiResponse<TradePlan>> disapprove(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        User user = resolveUser(authHeader);
        String reason = body != null ? body.get("reason") : null;
        TradePlan plan = tradePlanService.disapprove(id, user, reason);
        return ResponseEntity.ok(ApiResponse.success("Trade plan disapproved", plan));
    }

    /** DELETE /api/trades/{id} — delete a trade plan */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        User user = resolveUser(authHeader);
        tradePlanService.delete(id, user);
        return ResponseEntity.ok(ApiResponse.success("Trade plan deleted", null));
    }

    /** GET /api/trades/stats — dashboard quick statistics */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStats>> stats(
            @RequestHeader("Authorization") String authHeader) {
        User user = resolveUser(authHeader);
        DashboardStats stats = tradePlanService.getStats(user);
        return ResponseEntity.ok(ApiResponse.success("Stats fetched", stats));
    }

    /** PUT /api/trades/{id}/outcome — record WIN or LOSS for an approved trade */
    @PutMapping("/{id}/outcome")
    public ResponseEntity<ApiResponse<edu.cit.oswa.tradersguardian.entity.TradePlan>> recordOutcome(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body) {
        User user = resolveUser(authHeader);
        String outcomeStr = (String) body.get("outcome");
        edu.cit.oswa.tradersguardian.entity.TradePlan.Outcome outcome =
                edu.cit.oswa.tradersguardian.entity.TradePlan.Outcome.valueOf(outcomeStr);
        Double profitLossAmount = body.get("profitLossAmount") != null
                ? ((Number) body.get("profitLossAmount")).doubleValue()
                : null;
        edu.cit.oswa.tradersguardian.entity.TradePlan plan =
                tradePlanService.recordOutcome(id, user, outcome, profitLossAmount);
        return ResponseEntity.ok(ApiResponse.success("Outcome recorded", plan));
    }
}
