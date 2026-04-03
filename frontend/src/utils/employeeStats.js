export function getEmployeeStats(employees) {
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

  return {
    totalEmployees,
    totalSalary,
    averageSalary,
    departmentSummary,
  };
}