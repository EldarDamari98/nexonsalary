import { useEffect, useMemo, useState } from "react";
import { getAllEmployees } from "../api/employeeApi";
import EmployeeTable from "../components/employees/EmployeeTable";
import EmployeeFilters from "../components/employees/EmployeeFilters";

function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentFilter, setDepartmentFilter] = useState("");

  async function loadEmployees() {
    try {
      setLoading(true);
      setError("");

      const data = await getAllEmployees();
      setEmployees(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadEmployees();
  }, []);

  const departments = useMemo(() => {
    return [...new Set(employees.map((employee) => employee.department))];
  }, [employees]);

  const filteredEmployees = useMemo(() => {
    return employees.filter((employee) => {
      const fullText =
        `${employee.firstName} ${employee.lastName} ${employee.role} ${employee.department}`.toLowerCase();

      const matchesSearch = fullText.includes(searchTerm.toLowerCase());
      const matchesDepartment =
        departmentFilter === "" || employee.department === departmentFilter;

      return matchesSearch && matchesDepartment;
    });
  }, [employees, searchTerm, departmentFilter]);

  if (loading) return <h2>Loading employees...</h2>;
  if (error) return <h2>Error: {error}</h2>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Employees</h1>
          <p>Search, filter and review employee records</p>
        </div>

        <button className="primary-btn" onClick={loadEmployees}>
          Refresh
        </button>
      </div>

      <EmployeeFilters
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
        departmentFilter={departmentFilter}
        onDepartmentChange={setDepartmentFilter}
        departments={departments}
      />

      <EmployeeTable employees={filteredEmployees} />
    </div>
  );
}

export default EmployeesPage;