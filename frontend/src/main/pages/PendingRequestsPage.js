import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import PendingRequestsTable from "main/components/PendingRequests/PendingRequestsTable";
import { useBackend } from "main/utils/useBackend";

const PendingRequestsPage = () => {
  const {
    data: pendingRequests,
    error: _error,
    status: _status,
  } = useBackend(
    /* Stryker disable mutation */
    ["/api/requests/pending"],
    { method: "GET", url: "/api/requests/pending" },
    [],
    /* Stryker enable mutation */
  );

  return (
    <BasicLayout>
      <h1>Pending Requests</h1>
      <PendingRequestsTable requests={pendingRequests} />
    </BasicLayout>
  );
};

export default PendingRequestsPage;
