function Topbar({ onLogout }) {
  return (
    <header className="topbar">
      <div>
        <h2 className="topbar-title">NexonSalary</h2>
        <p className="topbar-subtitle">Manage agents, imports and balances data</p>
      </div>
      <button className="logout-btn" onClick={onLogout}>Sign Out</button>
    </header>
  );
}

export default Topbar;