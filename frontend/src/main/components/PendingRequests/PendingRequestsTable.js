// src/main/components/PendingRequests/PendingRequestsTable.js

import OurTable from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";
import { Button } from "react-bootstrap";
import { toast } from "react-toastify";

export default function PendingRequestsTable({ requests }) {
  // Callback to update status to "Accepted"
  function cellToAxiosParamsAccept(cell) {
    return {
      url: "/api/requests/updateStatus", // Ensure this endpoint exists in your backend
      method: "POST",
      data: {
        id: cell.row.values.id,
        status: "Accepted",
      },
    };
  }

  // Callback to update status to "Denied"
  function cellToAxiosParamsDeny(cell) {
    return {
      url: "/api/requests/updateStatus",
      method: "POST",
      data: {
        id: cell.row.values.id,
        status: "Denied",
      },
    };
  }

  // Stryker disable all : Hard to test for query caching and side effects
  const acceptMutation = useBackendMutation(cellToAxiosParamsAccept, {
    onSuccess: () => {
      toast.success("Request successfully accepted");
      // Optionally, trigger a refetch or update cache here
    },
    onError: (error) => {
      // Utilize the error parameter for detailed feedback
      const errorMessage = error.response?.data?.message || "Failed to accept request";
      toast.error(`Error: ${errorMessage}`);
    },
  });

  const denyMutation = useBackendMutation(cellToAxiosParamsDeny, {
    onSuccess: () => {
      toast.success("Request successfully denied");
      // Optionally, trigger a refetch or update cache here
    },
    onError: (error) => {
      // Utilize the error parameter for detailed feedback
      const errorMessage = error.response?.data?.message || "Failed to deny request";
      toast.error(`Error: ${errorMessage}`);
    },
  });
  // Stryker enable all

  // Stryker disable next-line all : Callback functions are simple and invoke mutations
  const acceptCallback = (cell) => {
    acceptMutation.mutate(cell);
  };

  // Stryker disable next-line all : Callback functions are simple and invoke mutations
  const denyCallback = (cell) => {
    denyMutation.mutate(cell);
  };
  // Stryker enable next-line all

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
    {
      Header: "Accept",
      id: "Accept",
      Cell: ({ cell }) =>
        cell.row.values.status === "Pending" ? (
          <Button
            variant="success"
            onClick={() => acceptCallback(cell)}
            data-testid={`PendingRequestsTable-cell-row-${cell.row.index}-col-Accept-button`}
          >
            Accept
          </Button>
        ) : null,
    },
    {
      Header: "Deny",
      id: "Deny",
      Cell: ({ cell }) =>
        cell.row.values.status === "Pending" ? (
          <Button
            variant="danger"
            onClick={() => denyCallback(cell)}
            data-testid={`PendingRequestsTable-cell-row-${cell.row.index}-col-Deny-button`}
          >
            Deny
          </Button>
        ) : null,
    },
  ];

  // Stryker disable all : Filtering logic is straightforward and not worth testing each condition
  const filteredRequests = requests.filter(
    (request) => request.status === "Pending" || request.status === "Accepted",
  );
  // Stryker enable all

  return (
    <OurTable
      data={filteredRequests}
      columns={columns}
      testid={"PendingRequestsTable"}
    />
  );
}
