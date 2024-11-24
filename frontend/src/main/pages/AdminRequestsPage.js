import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

import { useBackend } from "main/utils/useBackend";
const AdminRequestsPage = () => {
  const { error: _error, status: _status } = useBackend();
  // Stryker disable next-line all : don't test internal caching of React Query

  return (
    <BasicLayout>
      <h2>All Recommendation Requests</h2>
    </BasicLayout>
  );
};

export default AdminRequestsPage;
