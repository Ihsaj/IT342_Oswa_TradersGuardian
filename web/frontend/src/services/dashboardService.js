import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Single shared Axios instance — always reads the latest token from localStorage
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Attach token on every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

const dashboardService = {
  // Account Settings
  getSettings:     ()             => api.get('/settings'),
  updateSettings:  (data)         => api.put('/settings', data),

  // Trade Plans
  getTrades:       ()             => api.get('/trades'),
  createTrade:     (data)         => api.post('/trades', data),
  approveTrade:    (id)           => api.put(`/trades/${id}/approve`),
  disapproveTrade: (id, reason)   => api.put(`/trades/${id}/disapprove`, { reason }),
  recordOutcome:   (id, outcome, profitLossAmount) =>
                     api.put(`/trades/${id}/outcome`, { outcome, profitLossAmount }),
  deleteTrade:     (id)           => api.delete(`/trades/${id}`),

  // Dashboard stats
  getStats:        ()             => api.get('/trades/stats'),
};

export default dashboardService;
