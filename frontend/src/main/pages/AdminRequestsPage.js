import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
// import RequestTable from "main/components/Users/RequestTable";

import { useBackend } from "main/utils/useBackend";
const AdminRequestsPage = () => {
  const { error: _error, status: _status } =
    useBackend();
    // Stryker disable next-line all : don't test internal caching of React Query

  return (
    <BasicLayout>
      <h2>All Recommendation Requests</h2>
      {/* <RequestTable requests={requests} /> */}
    </BasicLayout>
  );
};

export default AdminRequestsPage;
