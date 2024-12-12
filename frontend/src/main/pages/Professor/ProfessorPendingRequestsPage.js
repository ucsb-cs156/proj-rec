import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

import { useBackend } from "main/utils/useBackend";
const ProfessorPendingRequestsPage = () => {
  const { error: _error, status: _status } = useBackend();
  // Stryker disable next-line all : don't test internal caching of React Query

  return (
    <BasicLayout>
      <h2>Pending Requests</h2>
    </BasicLayout>
  );
};

export default ProfessorPendingRequestsPage;
