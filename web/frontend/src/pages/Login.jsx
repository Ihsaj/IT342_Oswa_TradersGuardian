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
    padding: "16px",
    position: "relative",
    overflow: "hidden",
    backgroundImage: `
      linear-gradient(rgba(0,200,224,0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0,200,224,0.03) 1px, transparent 1px)
    `,
    backgroundSize: "48px 48px",
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
    gap: 18,
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
  forgotWrap: {
    textAlign: "right",
    marginTop: -6,
  },
  forgotLink: {
    fontSize: 12,
    color: "#00c8e0",
    textDecoration: "none",
  },
  loginBtn: {
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
  },
  loginBtnDisabled: {
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
  registerText: {
    textAlign: "center",
    fontSize: 13,
    color: "#888",
    margin: 0,
  },
  registerLink: {
    color: "#00c8e0",
    fontWeight: 600,
    textDecoration: "none",
  },
};

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [emailErr, setEmailErr] = useState(false);
  const [passwordErr, setPasswordErr] = useState(false);
  const [apiError, setApiError] = useState("");
  const [emailFocus, setEmailFocus] = useState(false);
  const [passwordFocus, setPasswordFocus] = useState(false);

  const navigate = useNavigate();
  const { login, loading } = useAuth();

  const validateForm = () => {
    let valid = true;
    const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    setEmailErr(!emailOk);
    if (!emailOk) valid = false;
    setPasswordErr(!password);
    if (!password) valid = false;
    return valid;
  };

  const submit = async (e) => {
    e.preventDefault();
    setApiError("");
    if (!validateForm()) return;
    try {
      await login({ email, password });
      navigate("/dashboard");
    } catch (err) {
      setApiError(err.response?.data?.error || err.response?.data?.message || "Invalid credentials. Please try again.");
    }
  };

  return (
    <div style={S.page}>
      <div style={S.glow} />

      <div style={S.wrapper}>
        {/* Brand */}
        <div style={S.brand}>
          <div style={S.logoBox}>
            <svg
              width="26" height="26"
              fill="none"
              stroke="#0d0d0d"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
              viewBox="0 0 24 24"
            >
              <polyline points="3 17 9 11 13 15 21 7" />
              <polyline points="14 7 21 7 21 14" />
            </svg>
          </div>
          <h1 style={S.title}>Trader's Guardian</h1>
          <p style={S.subtitle}>Sign in to your trading account</p>
        </div>

        {/* Card */}
        <div style={S.card}>
          {/* API Error */}
          {apiError && <div style={S.errorBanner}>{apiError}</div>}

          {/* Email */}
          <div style={S.field}>
            <label htmlFor="email" style={S.label}>Email</label>
            <input
              id="email"
              type="email"
              placeholder="trader@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onFocus={() => setEmailFocus(true)}
              onBlur={() => setEmailFocus(false)}
              style={{
                ...S.input,
                borderColor: emailErr ? "#ff4d6a" : emailFocus ? "#00c8e0" : "#2a2a2a",
                boxShadow: emailFocus ? "0 0 0 3px rgba(0,200,224,0.12)" : "none",
              }}
            />
            {emailErr && <span style={S.fieldErr}>Please enter a valid email.</span>}
          </div>

          {/* Password */}
          <div style={S.field}>
            <label htmlFor="password" style={S.label}>Password</label>
            <div style={S.inputWrap}>
              <input
                id="password"
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                onFocus={() => setPasswordFocus(true)}
                onBlur={() => setPasswordFocus(false)}
                style={{
                  ...S.input,
                  paddingRight: 40,
                  borderColor: passwordErr ? "#ff4d6a" : passwordFocus ? "#00c8e0" : "#2a2a2a",
                  boxShadow: passwordFocus ? "0 0 0 3px rgba(0,200,224,0.12)" : "none",
                }}
              />
              <button
                type="button"
                onClick={() => setShowPassword((v) => !v)}
                style={S.eyeBtn}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? (
                  <svg width="16" height="16" fill="none" stroke="#888" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <line x1="1" y1="1" x2="23" y2="23" />
                  </svg>
                ) : (
                  <svg width="16" height="16" fill="none" stroke="#888" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>
            {passwordErr && <span style={S.fieldErr}>Password is required.</span>}
          </div>

          {/* Forgot Password */}
          <div style={S.forgotWrap}>
            <a href="/forgot-password" style={S.forgotLink}>Forgot password?</a>
          </div>

          {/* Login Button */}
          <button
            onClick={submit}
            disabled={loading}
            style={{ ...S.loginBtn, ...(loading ? S.loginBtnDisabled : {}) }}
          >
            {loading ? (
              <svg
                style={{ animation: "spin 1s linear infinite", display: "block", margin: "0 auto" }}
                width="20" height="20" fill="none" stroke="#0d0d0d" strokeWidth="2.5" viewBox="0 0 24 24"
              >
                <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
              </svg>
            ) : "Login"}
          </button>

          {/* Divider */}
          <div style={S.divider}>
            <div style={S.dividerLine} />
            <span>or</span>
            <div style={S.dividerLine} />
          </div>

          {/* Register */}
          <p style={S.registerText}>
            Don't have an account?{" "}
            <a href="/register" style={S.registerLink}>Register here</a>
          </p>
        </div>
      </div>

      <style>{`
        @keyframes fadeUp {
          from { opacity: 0; transform: translateY(20px); }
          to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes spin {
          from { transform: rotate(0deg); }
          to   { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}
