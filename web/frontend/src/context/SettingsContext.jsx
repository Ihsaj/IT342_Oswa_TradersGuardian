import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import dashboardService from '../services/dashboardService';

const SettingsContext = createContext();

/**
 * Provides account settings to all pages.
 * When settings are saved, every page that reads from this context
 * sees the update immediately — no refetch needed.
 */
export const SettingsProvider = ({ children }) => {
  const [settings, setSettings] = useState(null);
  const [settingsLoading, setSettingsLoading] = useState(true);

  const fetchSettings = useCallback(async () => {
    if (!localStorage.getItem('authToken')) {
      setSettingsLoading(false);
      return;
    }
    try {
      setSettingsLoading(true);
      const res = await dashboardService.getSettings();
      setSettings(res.data.data);
    } catch (err) {
      console.error('Failed to fetch settings', err);
    } finally {
      setSettingsLoading(false);
    }
  }, []);

  // Fetch on mount
  useEffect(() => {
    fetchSettings();
  }, [fetchSettings]);

  /**
   * Save settings to backend AND update the shared state immediately.
   * Every page that reads `settings` will see the new values instantly.
   */
  const saveSettings = useCallback(async (data) => {
    const res = await dashboardService.updateSettings(data);
    setSettings(res.data.data); // update shared state immediately
    return res;
  }, []);

  /** Reset settings on logout */
  const clearSettings = useCallback(() => {
    setSettings(null);
  }, []);

  const value = {
    settings,
    settingsLoading,
    fetchSettings,
    saveSettings,
    clearSettings,
  };

  return (
    <SettingsContext.Provider value={value}>
      {children}
    </SettingsContext.Provider>
  );
};

export const useSettings = () => {
  const context = useContext(SettingsContext);
  if (!context) {
    throw new Error('useSettings must be used within SettingsProvider');
  }
  return context;
};
