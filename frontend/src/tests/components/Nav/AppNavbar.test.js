import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import AppNavbar from "main/components/Nav/AppNavbar";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

describe("AppNavbar tests", () => {
  const queryClient = new QueryClient();
  const renderNavbar = (currentUser, systemInfo = {}, props = {}) =>
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AppNavbar
            currentUser={currentUser}
            systemInfo={systemInfo}
            doLogin={jest.fn()}
            doLogout={jest.fn()}
            {...props}
          />
        </MemoryRouter>
      </QueryClientProvider>
    );

  /* ------------------------------------------------------------------
   * Existing tests (unchanged)
   * ------------------------------------------------------------------ */
  test("renders correctly for regular logged in user", async () => {
    renderNavbar(currentUserFixtures.userOnly);
    await screen.findByText("Welcome, pconrad.cis@gmail.com");
  });

  test("renders correctly for admin user", async () => {
    renderNavbar(currentUserFixtures.adminUser);
    await screen.findByText("Welcome, phtcon@ucsb.edu");
    expect(screen.getByTestId("appnavbar-admin-dropdown")).toBeInTheDocument();
  });

  test("renders H2Console and Swagger links correctly", async () => {
    renderNavbar(
      currentUserFixtures.adminUser,
      systemInfoFixtures.showingBoth
    );
    await screen.findByText("H2Console");
    expect(screen.getByText("Swagger")).toBeInTheDocument();
  });

  test("renders the AppNavbarLocalhost when on http://localhost:3000", async () => {
    delete window.location;
    window.location = new URL("http://localhost:3000");
    renderNavbar(
      currentUserFixtures.userOnly,
      systemInfoFixtures.showingBoth
    );
    await screen.findByTestId("AppNavbarLocalhost");
  });

  test("renders the AppNavbarLocalhost when on http://127.0.0.1:3000", async () => {
    delete window.location;
    window.location = new URL("http://127.0.0.1:3000");
    renderNavbar(
      currentUserFixtures.userOnly,
      systemInfoFixtures.showingBoth
    );
    await screen.findByTestId("AppNavbarLocalhost");
  });

  test("does NOT render the AppNavbarLocalhost when on localhost:8080", async () => {
    delete window.location;
    window.location = new URL("http://localhost:8080");
    renderNavbar(
      currentUserFixtures.userOnly,
      systemInfoFixtures.showingBoth
    );
    expect(screen.queryByTestId(/AppNavbarLocalhost/i)).toBeNull();
  });

  test("when oauthlogin undefined, default value is used", async () => {
    renderNavbar(
      currentUserFixtures.notLoggedIn,
      systemInfoFixtures.oauthLoginUndefined
    );
    const loginBtn = await screen.findByText("Log In");
    expect(loginBtn).toHaveAttribute("href", "/oauth2/authorization/google");
  });

  /* professor / student pages already tested … (existing) */
  test("renders the three prof pages correctly for professor users", async () => {
    renderNavbar(
      currentUserFixtures.professorUser,
      systemInfoFixtures.showingBoth
    );
    expect(screen.getByText("Pending Requests")).toBeInTheDocument();
    expect(screen.getByText("Completed Requests")).toBeInTheDocument();
    expect(screen.getByText("Statistics")).toBeInTheDocument();
  });

  test("renders the three prof pages correctly for student users", async () => {
    renderNavbar(
      currentUserFixtures.studentUser,
      systemInfoFixtures.showingBoth
    );
    expect(screen.getByText("Pending Requests")).toBeInTheDocument();
    expect(screen.getByText("Completed Requests")).toBeInTheDocument();
    expect(screen.getByText("Statistics")).toBeInTheDocument();
  });

  test("the three prof pages do not show for normal users", () => {
    renderNavbar(
      currentUserFixtures.userOnly,
      systemInfoFixtures.showingBoth
    );
    expect(screen.queryByText("Pending Requests")).not.toBeInTheDocument();
    expect(screen.queryByText("Completed Requests")).not.toBeInTheDocument();
    expect(screen.queryByText("Statistics")).not.toBeInTheDocument();
  });

  test("the three prof pages do not show when not logged in", () => {
    renderNavbar(null, systemInfoFixtures.showingBoth);
    expect(screen.queryByText("Pending Requests")).not.toBeInTheDocument();
    expect(screen.queryByText("Completed Requests")).not.toBeInTheDocument();
    expect(screen.queryByText("Statistics")).not.toBeInTheDocument();
  });

  /* ------------------------------------------------------------------
   * NEW tests to kill surviving mutants in AppNavbar.js
   * ------------------------------------------------------------------ */

  test("Admin dropdown hidden for normal user", () => {
    renderNavbar(currentUserFixtures.userOnly);
    expect(
      screen.queryByTestId("appnavbar-admin-dropdown")
    ).not.toBeInTheDocument();
  });

  test("Admin dropdown hidden when not logged in", () => {
    renderNavbar(null);
    expect(
      screen.queryByTestId("appnavbar-admin-dropdown")
    ).not.toBeInTheDocument();
  });

  test("Request Types link shows for admin and professor, not for normal user", () => {
    // Admin should see it
    const { rerender } = renderNavbar(currentUserFixtures.adminUser);
    expect(screen.getByText("Request Types")).toBeInTheDocument();

    // Professor should see it
    rerender(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AppNavbar currentUser={currentUserFixtures.professorUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );
    expect(screen.getByText("Request Types")).toBeInTheDocument();

    // Normal user should NOT see it
    rerender(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AppNavbar currentUser={currentUserFixtures.userOnly} />
        </MemoryRouter>
      </QueryClientProvider>
    );
    expect(screen.queryByText("Request Types")).not.toBeInTheDocument();
  });

  test("'Users' link appears only for ROLE_ADMIN", () => {
    // Admin sees "Users"
    const { rerender } = renderNavbar(currentUserFixtures.adminUser);
    expect(screen.getByText("Users")).toBeInTheDocument();

    // Professor does NOT see "Users"
    rerender(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AppNavbar currentUser={currentUserFixtures.professorUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );
    expect(screen.queryByText("Users")).not.toBeInTheDocument();
  });
});
