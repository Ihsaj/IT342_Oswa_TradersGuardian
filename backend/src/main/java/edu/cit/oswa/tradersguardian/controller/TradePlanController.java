package edu.cit.oswa.tradersguardian.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public TradePlanController(TradePlanService tradePlanService, AuthService authService) {
        this.tradePlanService = tradePlanService;
        this.authService = authService;
    }

    private User resolveUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Missing or invalid authorization header");
        }
        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
            return authService.getUserByEmail(claims.getSubject());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token");
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
}
