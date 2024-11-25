import React from "react";
import OurTable from "main/components/OurTable";

export default function PendingRequestsTable({ requests }) {
  const columns = [
    {
      Header: "ID",
      accessor: "id",
    },
    {
      Header: "Student Email",
      accessor: "studentEmail",
    },
    {
      Header: "Status",
      accessor: "status",
    },
    {
      Header: "Request Date",
      accessor: "requestDate",
    },
  ];

  return (
    <OurTable
      data={requests}
      columns={columns}
      testid="PendingRequestsTable"
    />
  );
}
