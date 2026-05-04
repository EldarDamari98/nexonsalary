import { useState } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./components/layout/AppLayout";
import DashboardPage from "./pages/DashboardPage";
import AgentsPage from "./pages/AgentsPage";
import BalanceImportPage from "./pages/BalanceImportPage";
import BalancesPage from "./pages/BalancesPage";
import CommissionsPage from "./pages/CommissionsPage";
import StatisticsPage from "./pages/StatisticsPage";
import LoginPage from "./pages/LoginPage";

/**
 * Root component of the NexonSalary application.
 *
 * Manages the login state for the entire app. If the user is not logged in,
 * the login page is shown and nothing else is accessible. Once logged in,
 * the full app layout with sidebar and all pages becomes available.
 *
 * Login state is persisted in localStorage so it survives page refreshes.
 */
function App() {
  /**
   * Initialize login state from localStorage.
   * The lazy initializer function runs only once on mount — avoids re-reading
   * localStorage on every render.
   */
  const [isLoggedIn, setIsLoggedIn] = useState(
    () => localStorage.getItem("isLoggedIn") === "true"
  );

  /**
   * Called by LoginPage when the user enters correct credentials.
   * Saves the login flag to localStorage and updates state.
   */
  function handleLogin() {
    localStorage.setItem("isLoggedIn", "true");
    setIsLoggedIn(true);
  }

  /**
   * Called when the user clicks "Sign Out" in the Topbar.
   * Removes the login flag from localStorage and returns to the login screen.
   */
  function handleLogout() {
    localStorage.removeItem("isLoggedIn");
    setIsLoggedIn(false);
  }

  // Guard: if not logged in, show only the login page
  if (!isLoggedIn) {
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <AppLayout onLogout={handleLogout}>
      <Routes>
        {/* Redirect the root path to the dashboard */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/agents" element={<AgentsPage />} />
        <Route path="/balance-import" element={<BalanceImportPage />} />
        <Route path="/balances" element={<BalancesPage />} />
        <Route path="/commissions" element={<CommissionsPage />} />
        <Route path="/statistics" element={<StatisticsPage />} />
      </Routes>
    </AppLayout>
  );
}

export default App;
