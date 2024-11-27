import { render, screen, waitFor } from "@testing-library/react";
import CompletedRequestsPage from "main/pages/Requests/CompletedRequestsPage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";

describe("CompletedRequestsPage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);
  const queryClient = new QueryClient();

  beforeEach(() => {
    axiosMock.reset();
    axiosMock.resetHistory();
  });

  test("Renders completed requests for professor", async () => {
    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
    axiosMock
      .onGet("/api/recommendationrequest/professor/all")
      .reply(200, recommendationRequestFixtures.threeRecommendations);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CompletedRequestsPage />
        </MemoryRouter>
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText("Completed Requests")).toBeInTheDocument();
    });

    expect(axiosMock.history.get.length).toBe(3);
    expect(
      screen.getByTestId("RecommendationRequestTable")
    ).toBeInTheDocument();
  });

  test("Renders empty table when no completed requests", async () => {
    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
    axiosMock.onGet("/api/recommendationrequest/professor/all").reply(200, []);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CompletedRequestsPage />
        </MemoryRouter>
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText("Completed Requests")).toBeInTheDocument();
    });

    expect(axiosMock.history.get.length).toBe(3);
    expect(
      screen.getByTestId("RecommendationRequestTable")
    ).toBeInTheDocument();
  });
});
