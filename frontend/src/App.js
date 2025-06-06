import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "main/pages/HomePage";
import ProfilePage from "main/pages/ProfilePage";
import AdminUsersPage from "main/pages/AdminUsersPage";
import SettingsPage from "main/pages/SettingsPage";
import AdminRequestsPage from "main/pages/AdminRequestsPage";

import PendingRequestsPage from "main/pages/Requests/PendingRequestsPage";
import CompletedRequestsPage from "main/pages/Requests/CompletedRequestsPage";
import StatisticsPage from "main/pages/Requests/StatisticsPage";
import StudentProfileIndexPage from "main/pages/StudentProfile/StudentProfileIndexPage";
import StudentProfileCreatePage from "main/pages/StudentProfile/StudentProfileCreatePage";
import StudentProfileEditPage from "main/pages/StudentProfile/StudentProfileEditPage";

import RequestTypesCreatePage from "main/pages/RequestTypes/RequestTypesCreatePage";
import RequestTypesEditPage from "main/pages/RequestTypes/RequestTypesEditPage";
import RequestTypesIndexPage from "main/pages/RequestTypes/RequestTypesIndexPage";

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
          <>
            <Route exact path="/admin/users" element={<AdminUsersPage />} />
            <Route
              exact
              path="/admin/requests"
              element={<AdminRequestsPage />}
            />
          </>
        )}
        {(hasRole(currentUser, "ROLE_ADMIN") ||
          hasRole(currentUser, "ROLE_PROFESSOR")) && (
          <>
            <Route exact path="/settings" element={<SettingsPage />} />
          </>
        )}
        {(hasRole(currentUser, "ROLE_ADMIN") ||
          hasRole(currentUser, "ROLE_PROFESSOR")) && (
          <>
            <Route
              exact
              path="/settings/requesttypes"
              element={<RequestTypesIndexPage />}
            />
          </>
        )}
        {(hasRole(currentUser, "ROLE_ADMIN") ||
          hasRole(currentUser, "ROLE_PROFESSOR")) && (
          <>
            <Route
              exact
              path="/settings/requesttypes/edit"
              element={<RequestTypesEditPage />}
            />
            <Route
              exact
              path="/settings/requesttypes/create"
              element={<RequestTypesCreatePage />}
            />
          </>
        )}
        {(hasRole(currentUser, "ROLE_PROFESSOR") ||
          hasRole(currentUser, "ROLE_STUDENT")) && (
          <>
            <Route
              exact
              path="/requests/pending"
              element={<PendingRequestsPage />}
            />
            <Route
              exact
              path="/requests/completed"
              element={<CompletedRequestsPage />}
            />
            <Route
              exact
              path="/requests/statistics"
              element={<StatisticsPage />}
            />
            <Route
              exact
              path="/requests"
              element={<StudentProfileIndexPage />}
            />
            <Route
              exact
              path="/requests/create"
              element={<StudentProfileCreatePage />}
            />
            <Route
              exact
              path="/requests/edit/:id"
              element={<StudentProfileEditPage />}
            />
          </>
        )}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
