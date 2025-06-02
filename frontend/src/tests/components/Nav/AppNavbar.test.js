import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { currentUserFixtures } from "fixtures/currentUserFixtures";

import AppNavbar from "main/components/Nav/AppNavbar";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

describe("AppNavbar tests", () => {
  const queryClient = new QueryClient();

  /** helper: centralises the “render in router + query-client” boilerplate */
  const renderNavbar = (user, sysInfo = systemInfoFixtures.showingBoth) =>
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AppNavbar currentUser={user} systemInfo={sysInfo} />
        </MemoryRouter>
      </QueryClientProvider>,
    );

  /* ---------- basic rendering ---------- */

  test("renders correctly for regular logged-in user", async () => {
    renderNavbar(currentUserFixtures.userOnly);
    await screen.findByText("Welcome, pconrad.cis@gmail.com");
  });

  test("renders correctly for admin user", async () => {
    renderNavbar(currentUserFixtures.adminUser);
    await screen.findByText("Welcome, phtcon@ucsb.edu");
    expect(screen.getByTestId("appnavbar-admin-dropdown")).toBeInTheDocument();
  });

  test("renders Admin dropdown for professor user", async () => {
    renderNavbar(currentUserFixtures.professorUser);
    expect(
      await screen.findByTestId("appnavbar-admin-dropdown"),
    ).toBeInTheDocument();
  });

  test("renders H2Console and Swagger links correctly", async () => {
    renderNavbar(currentUserFixtures.adminUser, systemInfoFixtures.showingBoth);
    await screen.findByText("H2Console");
    expect(screen.getByText("Swagger")).toBeInTheDocument();
  });

  /* ---------- localhost banner ---------- */

  test("renders AppNavbarLocalhost on http://localhost:3000", async () => {
    delete window.location;
    window.location = new URL("http://localhost:3000");

    renderNavbar(currentUserFixtures.userOnly);
    await screen.findByTestId("AppNavbarLocalhost");
    expect(screen.getByTestId("AppNavbarLocalhost-message1").textContent).toBe(
      "Running on http://localhost:3000/ with no backend.",
    );
    expect(screen.getByTestId("AppNavbarLocalhost-message2").textContent).toBe(
      "You probably want http://localhost:8080 instead.",
    );
  });

  test("renders AppNavbarLocalhost on http://127.0.0.1:3000", async () => {
    delete window.location;
    window.location = new URL("http://127.0.0.1:3000");
    renderNavbar(currentUserFixtures.userOnly);
    await screen.findByTestId("AppNavbarLocalhost");
  });

  test("does NOT render AppNavbarLocalhost on localhost:8080", async () => {
    delete window.location;
    window.location = new URL("http://localhost:8080");
    renderNavbar(currentUserFixtures.userOnly);
    await screen.findByTestId("AppNavbar");
    expect(screen.queryByTestId(/AppNavbarLocalhost/i)).toBeNull();
  });

  /* ---------- oauth fallback ---------- */

  test("when oauthLogin undefined, default value is used", async () => {
    renderNavbar(
      currentUserFixtures.notLoggedIn,
      systemInfoFixtures.oauthLoginUndefined,
    );
    await screen.findByText("Log In");
    expect(screen.getByText("Log In")).toHaveAttribute(
      "href",
      "/oauth2/authorization/google",
    );
  });

  /* ---------- request-page links ---------- */

  test("request links render for professor users", () => {
    renderNavbar(currentUserFixtures.professorUser);
    expect(screen.getByText("Pending Requests")).toBeInTheDocument();
    expect(screen.getByText("Completed Requests")).toBeInTheDocument();
    expect(screen.getByText("Statistics")).toBeInTheDocument();
  });

  test("request links render for normal users", () => {
    renderNavbar(currentUserFixtures.userOnly);
    expect(screen.getByText("Pending Requests")).toBeInTheDocument();
    expect(screen.getByText("Completed Requests")).toBeInTheDocument();
    expect(screen.getByText("Statistics")).toBeInTheDocument();
  });

  test("request links are hidden when not logged in", () => {
    renderNavbar(null);
    expect(screen.queryByText("Pending Requests")).not.toBeInTheDocument();
    expect(screen.queryByText("Completed Requests")).not.toBeInTheDocument();
    expect(screen.queryByText("Statistics")).not.toBeInTheDocument();
  });

  /* ---------- admin dropdown ---------- */

  test("Admin dropdown does NOT render for normal user", () => {
    renderNavbar(currentUserFixtures.userOnly);
    expect(screen.queryByTestId("appnavbar-admin-dropdown")).toBeNull();
  });

  test("Admin dropdown does NOT render when not logged in", () => {
    renderNavbar(null);
    expect(screen.queryByTestId("appnavbar-admin-dropdown")).toBeNull();
  });

  test("'Users' link visible only for ROLE_ADMIN", async () => {
    /* ---- as ADMIN ---- */
    const { rerender } = renderNavbar(currentUserFixtures.adminUser);

    userEvent.click(screen.getByRole("button", { name: /admin/i }));
    expect(await screen.findByText("Users")).toBeInTheDocument();

    /* ---- as PROFESSOR ---- */
    rerender(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AppNavbar currentUser={currentUserFixtures.professorUser} />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const adminToggle = screen.queryByRole("button", { name: /admin/i });
    if (adminToggle) userEvent.click(adminToggle);
    expect(screen.queryByText("Users")).not.toBeInTheDocument();
  });

  /* ---------- Settings dropdown (kills remaining mutants) ---------- */

  describe("Settings dropdown visibility", () => {
    test("Settings dropdown and 'Request Types' link render for ROLE_ADMIN", async () => {
      renderNavbar(currentUserFixtures.adminUser);

      userEvent.click(await screen.findByRole("button", { name: /settings/i }));
      expect(
        await screen.findByTestId("appnavbar-requesttypes"),
      ).toBeInTheDocument();
    });

    test("Settings dropdown and 'Request Types' link render for ROLE_PROFESSOR", async () => {
      renderNavbar(currentUserFixtures.professorUser);

      userEvent.click(await screen.findByRole("button", { name: /settings/i }));
      expect(
        await screen.findByTestId("appnavbar-requesttypes"),
      ).toBeInTheDocument();
    });

    test("Settings dropdown does NOT render for normal user", () => {
      renderNavbar(currentUserFixtures.userOnly);
      expect(screen.queryByTestId("appnavbar-settings-dropdown")).toBeNull();
    });

    test("Settings dropdown does NOT render when not logged in", () => {
      renderNavbar(null);
      expect(screen.queryByTestId("appnavbar-settings-dropdown")).toBeNull();
    });
  });
});
