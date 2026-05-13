import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

const dashboardService = {
  // Account Settings
  getSettings: () => apiClient.get('/settings'),
  updateSettings: (data) => apiClient.put('/settings', data),

  // Trade Plans
  getTrades: () => apiClient.get('/trades'),
  createTrade: (data) => apiClient.post('/trades', data),
  approveTrade: (id) => apiClient.put(`/trades/${id}/approve`),
  disapproveTrade: (id, reason) => apiClient.put(`/trades/${id}/disapprove`, { reason }),
  deleteTrade: (id) => apiClient.delete(`/trades/${id}`),

  // Dashboard stats
  getStats: () => apiClient.get('/trades/stats'),
};

export default dashboardService;
