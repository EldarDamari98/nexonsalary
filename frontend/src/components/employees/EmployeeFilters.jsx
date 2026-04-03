function EmployeeFilters({
  searchTerm,
  onSearchChange,
  departmentFilter,
  onDepartmentChange,
  departments,
}) {
  return (
    <div className="filters-bar">
      <input
        className="input"
        type="text"
        placeholder="Search by name, role or department"
        value={searchTerm}
        onChange={(e) => onSearchChange(e.target.value)}
      />

      <select
        className="input"
        value={departmentFilter}
        onChange={(e) => onDepartmentChange(e.target.value)}
      >
        <option value="">All Departments</option>
        {departments.map((department) => (
          <option key={department} value={department}>
            {department}
          </option>
        ))}
      </select>
    </div>
  );
}

export default EmployeeFilters;