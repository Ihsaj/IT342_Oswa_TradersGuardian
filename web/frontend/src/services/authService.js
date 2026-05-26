import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// No response interceptor — session management is handled by AuthContext.
// The user is only logged out when they explicitly call logout().

const authService = {
  register: (data) => {
    return apiClient.post('/auth/register', {
      firstname: data.firstname,
      lastname: data.lastname,
      email: data.email,
      password: data.password,
    });
  },

  login: (data) => {
    return apiClient.post('/auth/login', {
      email: data.email,
      password: data.password,
    });
  },

  me: () => {
    return apiClient.get('/user/me');
  },

  logout: () => {
    // Call backend logout endpoint first
    apiClient.post('/auth/logout').catch(() => {
      // Continue with logout even if endpoint fails
    });
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  },

  getToken: () => {
    return localStorage.getItem('authToken');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  // Expose the shared Axios instance so other services can reuse it
  // (same interceptors = consistent token attachment & 401 handling)
  _client: apiClient,
};

export default authService;

