import { useEffect, useState } from "react";
import { getAllEmployees } from "../api/employeeApi";

function DashboardPage() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        const data = await getAllEmployees();
        setEmployees(data);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, []);

  if (loading) return <h2>Loading dashboard...</h2>;
  if (error) return <h2>Error: {error}</h2>;

  const totalEmployees = employees.length;
  const totalSalary = employees.reduce(
    (sum, employee) => sum + Number(employee.salary),
    0
  );
  const averageSalary =
    totalEmployees === 0 ? 0 : Math.round(totalSalary / totalEmployees);

  const departmentMap = {};
  for (const employee of employees) {
    if (!departmentMap[employee.department]) {
      departmentMap[employee.department] = 0;
    }
    departmentMap[employee.department] += 1;
  }

  const departmentSummary = Object.entries(departmentMap).map(
    ([department, count]) => ({
      department,
      count,
    })
  );

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>Overview of employee and salary data</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <p className="stat-card-title">Total Employees</p>
          <h3 className="stat-card-value">{totalEmployees}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Total Salary</p>
          <h3 className="stat-card-value">₪{totalSalary}</h3>
        </div>

        <div className="stat-card">
          <p className="stat-card-title">Average Salary</p>
          <h3 className="stat-card-value">₪{averageSalary}</h3>
        </div>
      </div>

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
            {departmentSummary.map((item) => (
              <tr key={item.department}>
                <td>{item.department}</td>
                <td>{item.count}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default DashboardPage;