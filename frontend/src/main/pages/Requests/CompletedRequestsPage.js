import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useBackend } from "main/utils/useBackend";
import RecommendationRequestTable from "main/components/RecommendationRequest/RecommendationRequestTable";
import { useCurrentUser } from "main/utils/currentUser";

export default function CompletedRequestsPage() {
  const { data: currentUser } = useCurrentUser();

  const {
    data: requests,
    error: _error,
    status: _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    ["/api/recommendationrequest/professor/all"],
    {
      // Stryker disable next-line all : GET is the default, so mutating this to "" doesn't introduce a bug
      method: "GET",
      url: "/api/recommendationrequest/professor/all",
    },
    // Stryker disable next-line all : it's hard to test GET requests that are still in progress
    [],
  );

  const completedRequests = requests.filter(
    (request) => request.status === "COMPLETED" || request.status === "DENIED",
  );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Completed Requests</h1>
        <div data-testid="RecommendationRequestTable">
          <RecommendationRequestTable
            requests={completedRequests}
            currentUser={currentUser}
          />
        </div>
      </div>
    </BasicLayout>
  );
}
