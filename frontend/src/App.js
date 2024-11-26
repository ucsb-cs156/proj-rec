import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "main/pages/HomePage";
import ProfilePage from "main/pages/ProfilePage";
import AdminUsersPage from "main/pages/AdminUsersPage";
import AdminRequestsPage from "main/pages/AdminRequestsPage";
import ProfessorCompletedRequestsPage from "main/pages/Professor/ProfessorCompletedRequestsPage";
import ProfessorStatisticsPage from "main/pages/Professor/ProfessorStatisticsPage";

import RequestTypeIndexPage from "main/pages/RequestType/RequestTypeIndexPage";
import RequestTypeCreatePage from "main/pages/RequestType/RequestTypeCreatePage";
import RequestTypeEditPage from "main/pages/RequestType/RequestTypeEditPage";

import { hasRole, useCurrentUser } from "main/utils/currentUser";

import "bootstrap/dist/css/bootstrap.css";
import "react-toastify/dist/ReactToastify.css";

function App() {
  const { data: currentUser } = useCurrentUser();

  return (
    <BrowserRouter>
      <Routes>
        <Route exact path="/" element={<HomePage />} />
        <Route exact path="/profile" element={<ProfilePage />} />
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <Route exact path="/admin/users" element={<AdminUsersPage />} />
        )}
        {hasRole(currentUser, "ROLE_USER") && (
          <>
            <Route
              exact
              path="/requesttype"
              element={<RequestTypeIndexPage />}
            />
          </>
        )}
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <>
            <Route
              exact
              path="/requesttype/edit/:id"
              element={<RequestTypeEditPage />}
            />
            <Route
              exact
              path="/requesttype/create"
              element={<RequestTypeCreatePage />}
            />
          </>
        )}
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <Route exact path="/admin/requests" element={<AdminRequestsPage />} />
        )}
        {hasRole(currentUser, "ROLE_PROFESSOR") && (
          <Route
            exact
            path="/requests/completed"
            element={<ProfessorCompletedRequestsPage />}
          />
        )}
        {hasRole(currentUser, "ROLE_PROFESSOR") && (
          <Route
            exact
            path="/requests/statistics"
            element={<ProfessorStatisticsPage />}
          />
        )}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
