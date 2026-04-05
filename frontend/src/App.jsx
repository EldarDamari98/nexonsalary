import { Navigate, Route, Routes } from "react-router-dom";
import AppLayout from "./components/layout/AppLayout";
import DashboardPage from "./pages/DashboardPage";
import EmployeesPage from "./pages/EmployeesPage";
import BalanceImportPage from "./pages/BalanceImportPage";
import BalancesPage from "./pages/BalancesPage";

function App() {
  return (
    <AppLayout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/employees" element={<EmployeesPage />} />
        <Route path="/balance-import" element={<BalanceImportPage />} />
        <Route path="/balances" element={<BalancesPage />} />
      </Routes>
    </AppLayout>
  );
}

export default App;