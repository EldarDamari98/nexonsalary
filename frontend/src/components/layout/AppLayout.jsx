import Sidebar from "./Sidebar";
import Topbar from "./Topbar";

function AppLayout({ children, onLogout }) {
  return (
    <div className="app-shell">
      <Sidebar />
      <div className="app-main">
        <Topbar onLogout={onLogout} />
        <main className="app-content">{children}</main>
      </div>
    </div>
  );
}

export default AppLayout;