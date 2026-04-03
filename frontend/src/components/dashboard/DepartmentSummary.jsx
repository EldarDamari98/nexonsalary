function DepartmentSummary({ summary }) {
  return (
    <div className="card">
      <h3>Employees by Department</h3>

      <table className="simple-table">
        <thead>
          <tr>
            <th>Department</th>
            <th>Employees</th>
          </tr>
        </thead>
        <tbody>
          {summary.map((item) => (
            <tr key={item.department}>
              <td>{item.department}</td>
              <td>{item.count}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default DepartmentSummary;