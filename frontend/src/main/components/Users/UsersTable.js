import OurTable, {ButtonColumn} from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";

export default function UsersTable({ users }) {
  
  // Toggle Admin User
  function cellToAxiosParamsToggleAdmin(cell) {
    return {
      url: "/api/admin/users/toggleAdmin",
      method: "POST",
      params: {
          id: cell.row.values.id
      }
    };
  }
  // Stryker disable all : hard to test for query caching
  const toggleAdminMutation = useBackendMutation(
    cellToAxiosParamsToggleAdmin,
    {},
    ["/api/admin/users"]
  );
  // Stryker enable all 

  // Stryker disable next-line all 
  const toggleAdminCallBack = (cell) => {
    toggleAdminMutation.mutate(cell);
  };

  // Toggle Professor User
  function cellToAxiosParamsToggleProfessor(cell) {
    return {
      url: "/api/admin/users/toggleProfessor",
      method: "POST",
      params: {
          id: cell.row.values.id
      }
    };
  }

  // Stryker disable all : hard to test for query caching
  const toggleProfessorMutation = useBackendMutation(
    cellToAxiosParamsToggleProfessor,
    {},
    ["/api/admin/users"]
  );
  // Stryker enable all 

  // Stryker disable next-line all 
  const toggleProfessorCallBack = (cell) => {
    toggleProfessorMutation.mutate(cell);
  };

  // Toggle Student User
  function cellToAxiosParamsToggleStudent(cell) {
    return {
      url: "/api/admin/users/toggleStudent",
      method: "POST",
      params: {
          id: cell.row.values.id
      }
    };
  }

  // Stryker disable all : hard to test for query caching
  const toggleStudentMutation = useBackendMutation(
    cellToAxiosParamsToggleStudent,
    {},
    ["/api/admin/users"]
  );
  // Stryker enable all 

  // Stryker disable next-line all 
  const toggleStudentCallBack = (cell) => {
    toggleStudentMutation.mutate(cell);
  };

  const columns = [
    {
      Header: "id",
      accessor: "id", // accessor is the "key" in the data
    },
    {
      Header: "First Name",
      accessor: "givenName",
    },
    {
      Header: "Last Name",
      accessor: "familyName",
    },
    {
      Header: "Email",
      accessor: "email",
    },
    {
      Header: "Admin",
      id: "admin",
      accessor: (row, _rowIndex) => String(row.admin), // hack needed for boolean values to show up
    },
    {
      Header: "Professor",
      id: "professor",
      accessor: (row, _rowIndex) => String(row.professor), 
    },
    {
      Header: "Student",
      id: "student",
      accessor: (row, _rowIndex) => String(row.student),
    }
  ];

  const buttonColumns = [
    ...columns,
    ButtonColumn("Toggle Admin", "primary", toggleAdminCallBack, "UsersTable"),
    ButtonColumn("Toggle Professor", "primary", toggleProfessorCallBack, "UsersTable"),
    ButtonColumn("Toggle Student", "primary", toggleStudentCallBack, "UsersTable"),
  ]

  return <OurTable 
    data={users}
    columns={buttonColumns} 
    testid={"UsersTable"} 
  />;
}
