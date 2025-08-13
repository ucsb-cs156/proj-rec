import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter, useNavigate } from "react-router";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";

import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import StudentProfilePage from "main/pages/StudentProfilePage";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import usersFixtures from "fixtures/usersFixtures";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";
import recommendationTypeFixtures from "fixtures/recommendationTypeFixtures";
import { useBackend } from "main/utils/useBackend";
import { vi } from "vitest";

// Mock useBackend to verify the correct parameters
vi.mock("main/utils/useBackend", async (importOriginal) => {
  return {
    ...(await importOriginal()),
    useBackend: vi.fn(),
  };
});

// Mock useNavigate
vi.mock("react-router", async (importOriginal) => ({
  ...(await importOriginal()),
  useNavigate: vi.fn(),
}));

describe("StudentProfilePage tests", () => {
  const queryClient = new QueryClient();
  const navigateMock = vi.fn();
  let axiosMock;

  beforeEach(() => {
    axiosMock = new AxiosMockAdapter(axios);
    useNavigate.mockReturnValue(navigateMock);
    navigateMock.mockClear();

    // Reset the mock implementation to return a default value
    useBackend.mockImplementation(() => ({
      data: [],
      error: null,
      status: "success",
    }));
  });

  afterEach(() => {
    axiosMock.restore();
    vi.clearAllMocks();
  });

  test("useBackend is called with the correct parameters", async () => {
    // Setup the mock implementation
    useBackend.mockImplementation(() => {
      return {
        data: recommendationRequestFixtures.oneRecommendation,
        error: null,
        status: "success",
      };
    });

    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
    axiosMock
      .onGet("/api/admin/users/professors")
      .reply(200, usersFixtures.twoProfessors);
    axiosMock
      .onGet("/api/requesttypes/all")
      .reply(200, recommendationTypeFixtures.fourTypes);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <StudentProfilePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    // Verify that useBackend was called with the correct parameters
    await waitFor(() => {
      // Check that the endpoint parameter is correct and non-empty
      expect(useBackend).toHaveBeenCalledWith(
        ["/api/recommendationrequest/requester/all"],
        expect.objectContaining({
          method: "GET",
          url: "/api/recommendationrequest/requester/all",
        }),
        expect.any(Array),
      );
    });

    // Check specific parameters to kill mutants
    const useBackendCalls = useBackend.mock.calls;

    // Verify that the first parameter is the correct non-empty array
    expect(useBackendCalls[0][0]).toEqual([
      "/api/recommendationrequest/requester/all",
    ]);
    expect(useBackendCalls[0][0]).not.toEqual([]);
    expect(useBackendCalls[0][0]).not.toEqual([""]);

    // Verify that the method parameter is "GET" and not empty
    expect(useBackendCalls[0][1].method).toBe("GET");
    expect(useBackendCalls[0][1].method).not.toBe("");

    // Verify the third parameter (dependency array)
    expect(useBackendCalls[0][2]).toEqual([]);
    expect(useBackendCalls[0][2]).not.toEqual(["Stryker was here"]);
  });

  test("renders correctly for regular logged in user", async () => {
    useBackend.mockImplementation(() => {
      return {
        data: recommendationRequestFixtures.oneRecommendation,
        error: null,
        status: "success",
      };
    });

    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
    axiosMock
      .onGet("/api/admin/users/professors")
      .reply(200, usersFixtures.twoProfessors);
    axiosMock
      .onGet("/api/requesttypes/all")
      .reply(200, recommendationTypeFixtures.fourTypes);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <StudentProfilePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    expect(await screen.findByText("Create New Request")).toBeInTheDocument();
  });

  test("renders request data and checks edit functionality", async () => {
    const requestsData = [
      {
        id: 1,
        requester: { fullName: "Student Name", email: "test@example.com" },
        professor: { fullName: "Professor Name", email: "prof@example.com" },
        recommendationType: "Letter of Recommendation",
        details: "Test details",
        status: "Pending",
      },
    ];

    useBackend.mockImplementation(() => {
      return {
        data: requestsData,
        error: null,
        status: "success",
      };
    });

    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
    axiosMock
      .onGet("/api/admin/users/professors")
      .reply(200, usersFixtures.twoProfessors);
    axiosMock
      .onGet("/api/requesttypes/all")
      .reply(200, recommendationTypeFixtures.fourTypes);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <StudentProfilePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await screen.findByTestId("RecommendationRequestTable-header-group-0");

    // Find and click the edit button
    const editButton = await screen.findByTestId(
      "RecommendationRequestTable-cell-row-0-col-Edit-button",
    );
    fireEvent.click(editButton);

    expect(navigateMock).toHaveBeenCalledWith("/requests/edit/1");
  });

  test("checks create new request button", async () => {
    useBackend.mockImplementation(() => {
      return {
        data: [],
        error: null,
        status: "success",
      };
    });

    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
    axiosMock
      .onGet("/api/admin/users/professors")
      .reply(200, usersFixtures.twoProfessors);
    axiosMock
      .onGet("/api/requesttypes/all")
      .reply(200, recommendationTypeFixtures.fourTypes);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <StudentProfilePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    // Find and click the create new request button
    const createButton = await screen.findByText("Create New Request");
    fireEvent.click(createButton);

    expect(navigateMock).toHaveBeenCalledWith("/requests/create");
  });
});
