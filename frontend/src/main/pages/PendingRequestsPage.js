import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import PendingRequestsTable from "main/components/PendingRequests/PendingRequestsTable";
import { useBackend } from "main/utils/useBackend";

const PendingRequestsPage = () => {
  const { data: pendingRequests, error: _error, status: _status } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    ["/api/requests/pending"],
    { method: "GET", url: "/api/requests/pending" },
    []
  );

  return (
    <BasicLayout>
      <h1>Pending Requests</h1>
      <PendingRequestsTable requests={pendingRequests} />
    </BasicLayout>
  );
};

export default PendingRequestsPage;
