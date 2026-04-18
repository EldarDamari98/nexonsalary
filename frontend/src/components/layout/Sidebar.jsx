import { NavLink } from "react-router-dom";

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">NexonSalary</div>

      <nav className="sidebar-nav">
        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/agents"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Agents
        </NavLink>

        <NavLink
          to="/balance-import"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Balance Import
        </NavLink>

        <NavLink
          to="/balances"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Balances
        </NavLink>

        <NavLink
          to="/commissions"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Commissions
        </NavLink>
        <NavLink to="/statistics" className="sidebar-link">
          Statistics
        </NavLink>
      </nav>
    </aside>
  );
}

export default Sidebar;