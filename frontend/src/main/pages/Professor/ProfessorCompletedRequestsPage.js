import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

import { useBackend } from "main/utils/useBackend";
const ProfessorCompletedRequestsPage = () => {
  const { error: _error, status: _status } = useBackend();
  // Stryker disable next-line all : don't test internal caching of React Query

  return (
    <BasicLayout>
      <h2>Completed Requests</h2>
    </BasicLayout>
  );
};

export default ProfessorCompletedRequestsPage;
