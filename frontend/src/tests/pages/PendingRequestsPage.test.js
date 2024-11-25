import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import PendingRequestsPage from "main/pages/PendingRequestsPage";
import { pendingrequestsFixtures } from "fixtures/pendingrequestsFixtures";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

describe("PendingRequestsPage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);
  const queryClient = new QueryClient();

  beforeEach(() => {
    axiosMock.reset();
    axiosMock.onGet("/api/requests/pending").reply(200, pendingrequestsFixtures.threeRequests);
    axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
  });

  test("renders correctly with mock data", async () => {
    render(
      <MemoryRouter>
        <QueryClientProvider client={queryClient}>
          <PendingRequestsPage />
        </QueryClientProvider>
      </MemoryRouter>
    );
  });
});
