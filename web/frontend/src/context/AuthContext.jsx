import React, { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

/**
 * Decode the JWT payload locally (base64 only — no signature verification).
 * The backend verifies the signature on every real API call.
 * Returns the payload object, or null if the token is malformed.
 */
function decodeJwtPayload(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // On mount: restore session from the stored token WITHOUT a backend call.
  // We only need the email from the token payload — the backend validates
  // the signature on every real API request anyway.
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      const payload = decodeJwtPayload(token);
      if (payload && payload.sub) {
        // Token is structurally valid — restore the session immediately.
        setUser({ email: payload.sub });
        setIsAuthenticated(true);
        // Optionally enrich the user object in the background (non-blocking).
        authService.me()
          .then(res => setUser(res.data.data))
          .catch(() => { /* ignore — token is still valid, user stays logged in */ });
      } else {
        // Malformed token — clear it.
        localStorage.removeItem('authToken');
      }
    }
    setLoading(false);
  }, []);

  const register = async (userData) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authService.register(userData);
      return response.data;
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Registration failed';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authService.login(credentials);
      const { data } = response.data;

      // Store token
      localStorage.setItem('authToken', data.token);

      // Decode locally so we're never dependent on a second network call
      const payload = decodeJwtPayload(data.token);
      setUser({ email: payload?.sub || credentials.email });
      setIsAuthenticated(true);

      // Fetch full user profile in the background
      authService.me()
        .then(res => setUser(res.data.data))
        .catch(() => { /* non-critical */ });

      return response.data;
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Login failed';
      setError(errorMessage);
      setUser(null);
      setIsAuthenticated(false);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setIsAuthenticated(false);
    setError(null);
  };

  const value = {
    user,
    loading,
    error,
    isAuthenticated,
    register,
    login,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
