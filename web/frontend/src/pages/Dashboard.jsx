import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const navigate = useNavigate();
  const { user, loading, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0d0d0d] flex items-center justify-center">
        <div className="text-center">
          <svg className="animate-spin h-12 w-12 text-[#00c8e0] mx-auto" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p className="mt-4 text-[#888]">Loading...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-[#0d0d0d]">
      {/* Navigation */}
      <nav className="bg-[#1a1a1a] border-b border-[#2a2a2a] sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-[#00c8e0] rounded-lg flex items-center justify-center">
                <svg className="w-6 h-6 stroke-[#0d0d0d]" fill="none" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                  <polyline points="3 17 9 11 13 15 21 7"></polyline>
                  <polyline points="14 7 21 7 21 14"></polyline>
                </svg>
              </div>
              <h1 className="text-xl font-semibold text-[#f0f0f0]">Trader's Guardian</h1>
            </div>

            <div className="flex items-center gap-4">
              <div className="hidden sm:flex items-center gap-3">
                <div className="w-10 h-10 bg-[#2a2a2a] rounded-full flex items-center justify-center border border-[#3a3a3a]">
                  <span className="text-[#00c8e0] font-medium text-sm">
                    {user.email.charAt(0).toUpperCase()}
                  </span>
                </div>
                <div className="flex flex-col">
                  <span className="text-sm font-medium text-[#f0f0f0]">{user.firstname || 'User'}</span>
                  <span className="text-xs text-[#888]">{user.email}</span>
                </div>
              </div>
              <button
                onClick={handleLogout}
                className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-medium bg-[#00c8e0] text-[#0d0d0d] hover:bg-[#00b0c8] transition-colors duration-200"
              >
                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-3xl p-8 mb-8" style={{ boxShadow: '0 0 20px rgba(0,200,224,0.1)' }}>
          <div className="flex items-center gap-6">
            <div className="w-16 h-16 bg-gradient-to-br from-[#00c8e0] to-[#00b0c8] rounded-full flex items-center justify-center flex-shrink-0">
              <span className="text-[#0d0d0d] font-bold text-2xl">
                {user.email.charAt(0).toUpperCase()}
              </span>
            </div>
            <div>
              <h2 className="text-3xl font-bold text-[#f0f0f0]">Welcome back, {user.firstname || 'Trader'}!</h2>
              <p className="text-[#888] mt-1">{user.email}</p>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          {/* Total Projects Card */}
          <div className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-2xl p-6 hover:border-[#00c8e0] transition-colors duration-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-[#888]">Total Trades</p>
                <p className="text-3xl font-bold text-[#f0f0f0] mt-2">0</p>
              </div>
              <div className="w-14 h-14 bg-[#2a2a2a] rounded-2xl flex items-center justify-center">
                <svg className="w-7 h-7 text-[#00c8e0]" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                </svg>
              </div>
            </div>
          </div>

          {/* Winning Trades Card */}
          <div className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-2xl p-6 hover:border-[#00e09a] transition-colors duration-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-[#888]">Winning Trades</p>
                <p className="text-3xl font-bold text-[#00e09a] mt-2">0</p>
              </div>
              <div className="w-14 h-14 bg-[#2a2a2a] rounded-2xl flex items-center justify-center">
                <svg className="w-7 h-7 text-[#00e09a]" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
          </div>

          {/* Win Rate Card */}
          <div className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-2xl p-6 hover:border-[#ff4d6a] transition-colors duration-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-[#888]">Win Rate</p>
                <p className="text-3xl font-bold text-[#f0f0f0] mt-2">--</p>
              </div>
              <div className="w-14 h-14 bg-[#2a2a2a] rounded-2xl flex items-center justify-center">
                <svg className="w-7 h-7 text-[#ff4d6a]" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-3xl p-8">
          <h3 className="text-xl font-bold text-[#f0f0f0] mb-6">Recent Activity</h3>
          <div className="flex flex-col items-center justify-center py-12 text-center">
            <svg className="w-12 h-12 text-[#2a2a2a] mx-auto mb-4" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p className="text-[#888]">No recent activity yet</p>
            <p className="text-xs text-[#666] mt-2">Your trades will appear here</p>
          </div>
        </div>
      </main>
    </div>
  );
}