function EmployeeTable({ employees }) {
  return (
    <table style={{ width: "100%", borderCollapse: "collapse", marginTop: "20px" }}>
      <thead>
        <tr>
          <th style={thStyle}>ID</th>
          <th style={thStyle}>First Name</th>
          <th style={thStyle}>Last Name</th>
          <th style={thStyle}>Role</th>
          <th style={thStyle}>Department</th>
          <th style={thStyle}>Salary</th>
        </tr>
      </thead>
      <tbody>
        {employees.map((employee) => (
          <tr key={employee.id}>
            <td style={tdStyle}>{employee.id}</td>
            <td style={tdStyle}>{employee.firstName}</td>
            <td style={tdStyle}>{employee.lastName}</td>
            <td style={tdStyle}>{employee.role}</td>
            <td style={tdStyle}>{employee.department}</td>
            <td style={tdStyle}>{employee.salary}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

const thStyle = {
  border: "1px solid #ccc",
  padding: "10px",
  backgroundColor: "#f5f5f5",
  textAlign: "left",
};

const tdStyle = {
  border: "1px solid #ccc",
  padding: "10px",
};

export default EmployeeTable;