import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const S = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#0d0d0d",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: "32px 16px",
    position: "relative",
    overflow: "hidden",
    backgroundImage: `
      linear-gradient(rgba(0,200,224,0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0,200,224,0.03) 1px, transparent 1px)
    `,
    backgroundSize: "48px 48px",
    boxSizing: "border-box",
  },
  glow: {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 600,
    height: 600,
    borderRadius: "50%",
    background: "radial-gradient(circle, rgba(0,200,224,0.07) 0%, transparent 70%)",
    pointerEvents: "none",
  },
  wrapper: {
    width: "100%",
    maxWidth: 440,
    position: "relative",
    zIndex: 10,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    gap: 28,
    animation: "fadeUp 0.5s ease both",
  },
  brand: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    gap: 10,
  },
  logoBox: {
    width: 56,
    height: 56,
    borderRadius: 14,
    backgroundColor: "#00c8e0",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    boxShadow: "0 0 28px rgba(0,200,224,0.3)",
    flexShrink: 0,
  },
  title: {
    fontSize: 22,
    fontWeight: 700,
    color: "#f0f0f0",
    margin: 0,
    letterSpacing: "-0.3px",
  },
  subtitle: {
    fontSize: 13,
    color: "#888",
    margin: 0,
  },
  card: {
    width: "100%",
    backgroundColor: "#1a1a1a",
    border: "1px solid #2a2a2a",
    borderRadius: 20,
    padding: 32,
    display: "flex",
    flexDirection: "column",
    gap: 16,
    boxSizing: "border-box",
  },
  errorBanner: {
    backgroundColor: "rgba(255,77,106,0.1)",
    border: "1px solid #ff4d6a",
    borderRadius: 8,
    padding: "10px 14px",
    fontSize: 13,
    color: "#ff4d6a",
  },
  row: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: 12,
  },
  field: {
    display: "flex",
    flexDirection: "column",
    gap: 6,
  },
  label: {
    fontSize: 12,
    fontWeight: 500,
    color: "#ccc",
    letterSpacing: "0.02em",
  },
  input: {
    width: "100%",
    backgroundColor: "#222",
    border: "1px solid #2a2a2a",
    borderRadius: 8,
    padding: "11px 14px",
    fontSize: 13,
    color: "#f0f0f0",
    outline: "none",
    boxSizing: "border-box",
    transition: "border-color 0.2s",
    colorScheme: "dark",
  },
  inputWrap: {
    position: "relative",
  },
  eyeBtn: {
    position: "absolute",
    right: 12,
    top: "50%",
    transform: "translateY(-50%)",
    background: "none",
    border: "none",
    padding: 0,
    cursor: "pointer",
    color: "#888",
    display: "flex",
    alignItems: "center",
  },
  fieldErr: {
    fontSize: 11,
    color: "#ff4d6a",
  },
  strengthBarsWrap: {
    display: "flex",
    flexDirection: "column",
    gap: 6,
    marginTop: 2,
  },
  strengthBars: {
    display: "flex",
    gap: 4,
  },
  strengthBar: {
    flex: 1,
    height: 3,
    borderRadius: 99,
    transition: "background 0.3s",
  },
  strengthLabel: {
    fontSize: 11,
    color: "#888",
  },
  checkboxRow: {
    display: "flex",
    alignItems: "flex-start",
    gap: 10,
    fontSize: 13,
    color: "#888",
    cursor: "pointer",
  },
  checkbox: {
    width: 15,
    height: 15,
    marginTop: 2,
    cursor: "pointer",
    accentColor: "#00c8e0",
    flexShrink: 0,
  },
  link: {
    color: "#00c8e0",
    textDecoration: "none",
  },
  registerBtn: {
    width: "100%",
    backgroundColor: "#00c8e0",
    color: "#0d0d0d",
    fontWeight: 700,
    fontSize: 14,
    padding: "13px 0",
    borderRadius: 8,
    border: "none",
    cursor: "pointer",
    boxShadow: "0 0 18px rgba(0,200,224,0.25)",
    transition: "opacity 0.2s",
    marginTop: 4,
  },
  registerBtnDisabled: {
    opacity: 0.5,
    cursor: "not-allowed",
  },
  divider: {
    display: "flex",
    alignItems: "center",
    gap: 12,
    color: "#555",
    fontSize: 12,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: "#2a2a2a",
  },
  loginText: {
    textAlign: "center",
    fontSize: 13,
    color: "#888",
    margin: 0,
  },
  loginLink: {
    color: "#00c8e0",
    fontWeight: 600,
    textDecoration: "none",
  },
  overlay: {
    position: "fixed",
    inset: 0,
    zIndex: 50,
    backgroundColor: "rgba(13,13,13,0.92)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexDirection: "column",
    gap: 16,
    animation: "fadeIn 0.3s ease",
  },
  successIcon: {
    width: 64,
    height: 64,
    borderRadius: "50%",
    backgroundColor: "rgba(0,224,154,0.12)",
    border: "2px solid #00e09a",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 28,
    color: "#00e09a",
  },
  successTitle: {
    fontSize: 22,
    fontWeight: 700,
    color: "#f0f0f0",
    margin: 0,
  },
  successSubtitle: {
    fontSize: 14,
    color: "#888",
    margin: 0,
  },
  successLink: {
    color: "#00c8e0",
    fontWeight: 600,
    textDecoration: "none",
    marginTop: 8,
    fontSize: 14,
  },
};

const EyeIcon = ({ visible }) =>
  visible ? (
    <svg width="16" height="16" fill="none" stroke="#888" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
      <line x1="1" y1="1" x2="23" y2="23" />
    </svg>
  ) : (
    <svg width="16" height="16" fill="none" stroke="#888" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );

export default function Register() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [terms, setTerms] = useState(false);
  const [success, setSuccess] = useState(false);
  const [passwordStrength, setPasswordStrength] = useState(0);
  const [apiError, setApiError] = useState("");
  const [errors, setErrors] = useState({
    firstName: false, lastName: false, email: false,
    password: false, confirmPassword: false, terms: false,
  });
  const [focus, setFocus] = useState({
    firstName: false, lastName: false, email: false,
    password: false, confirmPassword: false,
  });

  const navigate = useNavigate();
  const { register, loading } = useAuth();

  const checkPasswordStrength = (value) => {
    let score = 0;
    if (value.length >= 8) score++;
    if (/[A-Z]/.test(value)) score++;
    if (/[0-9]/.test(value)) score++;
    if (/[^A-Za-z0-9]/.test(value)) score++;
    setPasswordStrength(score);
  };

  const getStrengthLabel = () => {
    const labels = ["Enter a password", "Too weak", "Could be stronger", "Getting there", "Strong password"];
    return labels[passwordStrength] || labels[0];
  };

  const getStrengthColor = () => {
    if (passwordStrength === 0) return "#2a2a2a";
    if (passwordStrength === 1) return "#ff4d6a";
    if (passwordStrength === 2) return "#ffaa00";
    if (passwordStrength === 3) return "#00c8e0";
    return "#00e09a";
  };

  const validateForm = () => {
    const newErrors = {
      firstName: !firstName.trim(),
      lastName: !lastName.trim(),
      email: !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email),
      password: password.length < 8,
      confirmPassword: password !== confirmPassword,
      terms: !terms,
    };
    setErrors(newErrors);
    return !Object.values(newErrors).some((e) => e);
  };

  const submit = async (e) => {
    e.preventDefault();
    setApiError("");
    if (!validateForm()) return;
    try {
      await register({ firstname: firstName, lastname: lastName, email, password });
      setSuccess(true);
      setTimeout(() => navigate("/login"), 2500);
    } catch (err) {
      setApiError(err.response?.data?.message || "Registration failed. Please try again.");
    }
  };

  const inputStyle = (field, hasError, withPadding = false) => ({
    ...S.input,
    paddingRight: withPadding ? 40 : 14,
    borderColor: hasError ? "#ff4d6a" : focus[field] ? "#00c8e0" : "#2a2a2a",
    boxShadow: focus[field] ? "0 0 0 3px rgba(0,200,224,0.12)" : "none",
  });

  const onFocus = (field) => setFocus((f) => ({ ...f, [field]: true }));
  const onBlur  = (field) => setFocus((f) => ({ ...f, [field]: false }));

  return (
    <div style={S.page}>
      <div style={S.glow} />

      <div style={S.wrapper}>
        {/* Brand */}
        <div style={S.brand}>
          <div style={S.logoBox}>
            <svg width="26" height="26" fill="none" stroke="#0d0d0d" strokeWidth="2.5"
              strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
              <polyline points="3 17 9 11 13 15 21 7" />
              <polyline points="14 7 21 7 21 14" />
            </svg>
          </div>
          <h1 style={S.title}>Trader's Guardian</h1>
          <p style={S.subtitle}>Create your trading account</p>
        </div>

        {/* Card */}
        <div style={S.card}>
          {apiError && <div style={S.errorBanner}>{apiError}</div>}

          {/* First & Last Name */}
          <div style={S.row}>
            <div style={S.field}>
              <label style={S.label}>First Name</label>
              <input
                type="text"
                placeholder="John"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                onFocus={() => onFocus("firstName")}
                onBlur={() => onBlur("firstName")}
                style={inputStyle("firstName", errors.firstName)}
              />
              {errors.firstName && <span style={S.fieldErr}>Required.</span>}
            </div>
            <div style={S.field}>
              <label style={S.label}>Last Name</label>
              <input
                type="text"
                placeholder="Doe"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                onFocus={() => onFocus("lastName")}
                onBlur={() => onBlur("lastName")}
                style={inputStyle("lastName", errors.lastName)}
              />
              {errors.lastName && <span style={S.fieldErr}>Required.</span>}
            </div>
          </div>

          {/* Email */}
          <div style={S.field}>
            <label style={S.label}>Email</label>
            <input
              type="email"
              placeholder="trader@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onFocus={() => onFocus("email")}
              onBlur={() => onBlur("email")}
              style={inputStyle("email", errors.email)}
            />
            {errors.email && <span style={S.fieldErr}>Enter a valid email address.</span>}
          </div>

          {/* Password */}
          <div style={S.field}>
            <label style={S.label}>Password</label>
            <div style={S.inputWrap}>
              <input
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(e) => { setPassword(e.target.value); checkPasswordStrength(e.target.value); }}
                onFocus={() => onFocus("password")}
                onBlur={() => onBlur("password")}
                style={inputStyle("password", errors.password, true)}
              />
              <button
                type="button"
                onClick={() => setShowPassword((v) => !v)}
                style={S.eyeBtn}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                <EyeIcon visible={showPassword} />
              </button>
            </div>
            <div style={S.strengthBarsWrap}>
              <div style={S.strengthBars}>
                {[...Array(4)].map((_, i) => (
                  <div
                    key={i}
                    style={{
                      ...S.strengthBar,
                      backgroundColor: i < passwordStrength ? getStrengthColor() : "#2a2a2a",
                    }}
                  />
                ))}
              </div>
              <span style={S.strengthLabel}>{getStrengthLabel()}</span>
            </div>
            {errors.password && <span style={S.fieldErr}>Password must be at least 8 characters.</span>}
          </div>

          {/* Confirm Password */}
          <div style={S.field}>
            <label style={S.label}>Confirm Password</label>
            <div style={S.inputWrap}>
              <input
                type={showConfirmPassword ? "text" : "password"}
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                onFocus={() => onFocus("confirmPassword")}
                onBlur={() => onBlur("confirmPassword")}
                style={inputStyle("confirmPassword", errors.confirmPassword, true)}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword((v) => !v)}
                style={S.eyeBtn}
                aria-label={showConfirmPassword ? "Hide password" : "Show password"}
              >
                <EyeIcon visible={showConfirmPassword} />
              </button>
            </div>
            {errors.confirmPassword && <span style={S.fieldErr}>Passwords do not match.</span>}
          </div>

          {/* Terms */}
          <div>
            <label style={S.checkboxRow}>
              <input
                type="checkbox"
                checked={terms}
                onChange={(e) => setTerms(e.target.checked)}
                style={S.checkbox}
              />
              <span>
                I agree to the{" "}
                <a href="#" style={S.link}>Terms of Service</a>
                {" "}and{" "}
                <a href="#" style={S.link}>Privacy Policy</a>
              </span>
            </label>
            {errors.terms && (
              <span style={{ ...S.fieldErr, display: "block", marginTop: 6 }}>
                You must accept the terms.
              </span>
            )}
          </div>

          {/* Register Button */}
          <button
            onClick={submit}
            disabled={loading}
            style={{ ...S.registerBtn, ...(loading ? S.registerBtnDisabled : {}) }}
          >
            {loading ? (
              <svg
                style={{ animation: "spin 1s linear infinite", display: "block", margin: "0 auto" }}
                width="20" height="20" fill="none" stroke="#0d0d0d" strokeWidth="2.5" viewBox="0 0 24 24"
              >
                <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
              </svg>
            ) : "Create Account"}
          </button>

          {/* Divider */}
          <div style={S.divider}>
            <div style={S.dividerLine} />
            <span>or</span>
            <div style={S.dividerLine} />
          </div>

          {/* Login Link */}
          <p style={S.loginText}>
            Already have an account?{" "}
            <a href="/login" style={S.loginLink}>Sign in here</a>
          </p>
        </div>
      </div>

      {/* Success Overlay */}
      {success && (
        <div style={S.overlay}>
          <div style={S.successIcon}>✓</div>
          <h2 style={S.successTitle}>Account Created!</h2>
          <p style={S.successSubtitle}>Welcome to Trader's Guardian. You're all set.</p>
          <a href="/login" style={S.successLink}>Go to Login →</a>
        </div>
      )}

      <style>{`
        @keyframes fadeUp {
          from { opacity: 0; transform: translateY(20px); }
          to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes fadeIn {
          from { opacity: 0; }
          to   { opacity: 1; }
        }
        @keyframes spin {
          from { transform: rotate(0deg); }
          to   { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}
