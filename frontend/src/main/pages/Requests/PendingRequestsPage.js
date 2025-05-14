import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useBackend } from "main/utils/useBackend";
import RecommendationRequestTable from "main/components/RecommendationRequest/RecommendationRequestTable";
import { hasRole, useCurrentUser } from "main/utils/currentUser";

export default function PendingRequestsPage() {
  // Stryker disable all : placeholder for future implementation
  const currentUser = useCurrentUser;

  const apiEndpoint = hasRole(currentUser, "ROLE_PROFESSOR")
    ? "/api/recommendationrequest/professor/all"
    : "/api/recommendationrequest/requester/all";

  const {
    data: requests,
    error: _error,
    status: _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    [apiEndpoint],
    { method: "GET", url: apiEndpoint },
    // Stryker disable next-line all : don't test default value of empty list
    [],
  );

  const pendingRequests = requests.filter(
    (request) => request.status === "PENDING",
  );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Pending Requests</h1>
        <RecommendationRequestTable
          requests={pendingRequests}
          currentUser={currentUser}
        />
      </div>
    </BasicLayout>
  );
}
