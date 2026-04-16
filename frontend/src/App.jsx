import { Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./components/layout/AppLayout";
import DashboardPage from "./pages/DashboardPage";
import AgentsPage from "./pages/AgentsPage";
import BalanceImportPage from "./pages/BalanceImportPage";
import BalancesPage from "./pages/BalancesPage";
import CommissionsPage from "./pages/CommissionsPage";

function App() {
  return (
    <AppLayout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/agents" element={<AgentsPage />} />
        <Route path="/balance-import" element={<BalanceImportPage />} />
        <Route path="/balances" element={<BalancesPage />} />
        <Route path="/commissions" element={<CommissionsPage />} />
      </Routes>
    </AppLayout>
  );
}

export default App;