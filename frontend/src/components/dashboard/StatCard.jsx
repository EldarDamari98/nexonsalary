function StatCard({ title, value }) {
  return (
    <div className="stat-card">
      <p className="stat-card-title">{title}</p>
      <h3 className="stat-card-value">{value}</h3>
    </div>
  );
}

export default StatCard;