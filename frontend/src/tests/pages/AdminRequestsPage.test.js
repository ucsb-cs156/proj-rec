import { render, waitFor, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import AdminRequestsPage from "main/pages/AdminRequestsPage";
// import request fixtures when they are done
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import mockConsole from "jest-mock-console";

import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

describe("AdminRequestsPage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);

  const testId = "RequestsTable";

  beforeEach(() => {
    axiosMock.reset();
    axiosMock.resetHistory();
    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
  });

  // add tests for frontend table when backend its done

  test("renders empty page when backend unavailable", async () => {
    const queryClient = new QueryClient();
    axiosMock.onGet("/api/admin/requests").timeout();

    const restoreConsole = mockConsole();

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <AdminRequestsPage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    expect(screen.getByText(/All Recommendation Requests/)).toBeInTheDocument();
  });
});
