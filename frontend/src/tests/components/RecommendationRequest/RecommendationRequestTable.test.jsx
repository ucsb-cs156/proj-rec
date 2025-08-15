import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";
import RecommendationRequestTable from "main/components/RecommendationRequest/RecommendationRequestTable";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter, Routes, Route } from "react-router";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import { hasRole } from "main/utils/currentUser";
import { toast } from "react-toastify";
import { vi } from "vitest";
import { onUpdateStatusSuccess } from "main/utils/RecommendationRequestUtils";

const mockedNavigate = vi.fn();
let mockedLocation = { pathname: "" };

vi.mock("react-router", async (importOriginal) => {
  return {
    ...(await importOriginal()),
    useNavigate: () => mockedNavigate,
    useLocation: () => mockedLocation,
  };
});

describe("RecommendationRequestTable apiEndpoint invalidation", () => {
  let queryClient;
  let invalidateSpy;
  const axiosMock = new AxiosMockAdapter(axios);

  beforeEach(() => {
    queryClient = new QueryClient();
    invalidateSpy = vi.spyOn(queryClient, "invalidateQueries");
    axiosMock.onDelete().reply(200, { message: "deleted" });
  });

  afterEach(() => {
    axiosMock.resetHistory();
    vi.clearAllMocks();
  });

  test("invalidates '/api/recommendationrequest/requester/all' on a non-pending/non-completed page", async () => {
    mockedLocation = { pathname: "/profile" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/profile"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUserFixtures.userOnly}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() =>
      expect(
        screen.getByTestId("RecommendationRequestTable-cell-row-0-col-id"),
      ).toHaveTextContent("2"),
    );

    fireEvent.click(
      screen.getByTestId(
        "RecommendationRequestTable-cell-row-0-col-Delete-button",
      ),
    );

    await waitFor(() =>
      expect(axiosMock.history.delete[0].url).toEqual(
        "/api/recommendationrequest",
      ),
    );

    await waitFor(() =>
      expect(invalidateSpy).toHaveBeenCalledWith(
        "/api/recommendationrequest/requester/all",
      ),
    );
  });

  test("invalidates '/api/recommendationrequest/professor/all' on a pending page", async () => {
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUserFixtures.professorUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() =>
      expect(
        screen.getByTestId("RecommendationRequestTable-cell-row-0-col-id"),
      ).toHaveTextContent("2"),
    );

    fireEvent.click(
      screen.getByTestId(
        "RecommendationRequestTable-cell-row-0-col-Delete-button",
      ),
    );

    await waitFor(() =>
      expect(axiosMock.history.delete[0].url).toEqual(
        "/api/recommendationrequest/professor",
      ),
    );

    await waitFor(() =>
      expect(invalidateSpy).toHaveBeenCalledWith(
        "/api/recommendationrequest/professor/all",
      ),
    );
  });
});

describe("UserTable tests", () => {
  const queryClient = new QueryClient();

  test("Has the expected column headers and content for ordinary user", () => {
    const currentUser = currentUserFixtures.userOnly;
    mockedLocation = { pathname: "/profile" };

    expect(hasRole(currentUser, "ROLE_USER")).toBe(true);
    expect(hasRole(currentUser, "ROLE_ADMIN")).toBe(false);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/profile"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const expectedHeaders = [
      "id",
      "Professor Name",
      "Professor Email",
      "Requester Name",
      "Requester Email",
      "Recommendation Type",
      "Details",
      "Status",
      "Submission Date",
      "Last Modified Date",
      "Completion Date",
      "Due Date",
    ];
    const expectedFields = [
      "id",
      "professor.fullName",
      "professor.email",
      "requester.fullName",
      "requester.email",
      "recommendationType",
      "details",
      "status",
      "submissionDate",
      "lastModifiedDate",
      "completionDate",
      "dueDate",
    ];
    const testId = "RecommendationRequestTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(
      screen.getByTestId(`${testId}-cell-row-0-col-submissionDate`),
    ).toHaveTextContent("01/02/2022 02:00");

    expect(
      screen.getByTestId(`${testId}-cell-row-1-col-lastModifiedDate`),
    ).toHaveTextContent("02/02/2022 02:00");

    expect(
      screen.getByTestId(`${testId}-cell-row-0-col-completionDate`),
    ).toHaveTextContent("06/02/2022 02:00");

    expect(
      screen.getByTestId(`${testId}-cell-row-1-col-completionDate`),
    ).toHaveTextContent("");

    expect(
      screen.getByTestId(`${testId}-cell-row-0-col-dueDate`),
    ).toHaveTextContent("09/02/2022 02:00");

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
      "2",
    );
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "3",
    );

    const editButton = screen.queryByTestId(
      `${testId}-cell-row-0-col-Edit-button`,
    );
    expect(editButton).toBeInTheDocument();

    expect(editButton).toHaveClass("btn btn-primary");

    const deleteButton = screen.queryByTestId(
      `${testId}-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();
  });

  test("Has the expected column headers and content for adminUser", () => {
    const currentUser = currentUserFixtures.adminUser;
    mockedLocation = { pathname: "/profile" };

    expect(hasRole(currentUser, "ROLE_ADMIN")).toBe(true);
    expect(hasRole(currentUser, "ROLE_USER")).toBe(true);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/profile"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const expectedHeaders = [
      "id",
      "Professor Name",
      "Professor Email",
      "Requester Name",
      "Requester Email",
      "Recommendation Type",
      "Details",
      "Status",
      "Submission Date",
      "Last Modified Date",
      "Completion Date",
      "Due Date",
    ];
    const expectedFields = [
      "id",
      "professor.fullName",
      "professor.email",
      "requester.fullName",
      "requester.email",
      "recommendationType",
      "details",
      "status",
      "submissionDate",
      "lastModifiedDate",
      "completionDate",
      "dueDate",
    ];
    const testId = "RecommendationRequestTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
      "2",
    );
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "3",
    );

    const deleteButton = screen.getByTestId(
      `${testId}-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();
    expect(deleteButton).toHaveClass("btn-danger");

    const editButton = screen.queryByTestId(
      `${testId}-cell-row-0-col-Edit-button`,
    );
    expect(editButton).not.toBeInTheDocument();
  });

  test("Edit button navigates to the edit page for user", async () => {
    const currentUser = currentUserFixtures.userOnly;
    mockedLocation = { pathname: "/profile" };

    expect(hasRole(currentUser, "ROLE_USER")).toBe(true);
    expect(hasRole(currentUser, "ROLE_ADMIN")).toBe(false);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/profile"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(
        screen.getByTestId(`RecommendationRequestTable-cell-row-0-col-id`),
      ).toHaveTextContent("2");
    });

    const editButton = screen.getByTestId(
      `RecommendationRequestTable-cell-row-0-col-Edit-button`,
    );
    expect(editButton).toBeInTheDocument();

    fireEvent.click(editButton);

    await waitFor(() =>
      expect(mockedNavigate).toHaveBeenCalledWith("/requests/edit/2"),
    );
  });

  //Added for mutation coverage for the case in which the user is neither a user nor an admin
  test("A user with no roles has expected content", () => {
    const currentUser = currentUserFixtures.notLoggedIn;
    mockedLocation = { pathname: "/profile" };

    expect(hasRole(currentUser, "ROLE_USER")).toBe(undefined);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/profile"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const testId = "RecommendationRequestTable";

    const editButton = screen.queryByTestId(
      `${testId}-cell-row-0-col-Edit-button`,
    );
    expect(editButton).not.toBeInTheDocument();

    const deleteButton = screen.queryByTestId(
      `${testId}-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).not.toBeInTheDocument();
  });

  //for user
  test("Delete button calls delete callback (for requester)", async () => {
    // arrange
    const currentUser = currentUserFixtures.userOnly;
    mockedLocation = { pathname: "/profile" };

    const axiosMock = new AxiosMockAdapter(axios);
    axiosMock
      .onDelete("/api/recommendationrequest")
      .reply(200, { message: "Recommendation Request deleted" });

    // act - render the component
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/profile"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    // assert - check that the expected content is rendered

    await waitFor(() => {
      expect(
        screen.getByTestId(`RecommendationRequestTable-cell-row-0-col-id`),
      ).toHaveTextContent("2");
    });

    const deleteButton = screen.getByTestId(
      `RecommendationRequestTable-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();

    // act - click the delete button
    fireEvent.click(deleteButton);

    // assert - check that the delete endpoint was called
    await waitFor(() => expect(axiosMock.history.delete.length).toBe(1));
    expect(axiosMock.history.delete[0].url).toEqual(
      "/api/recommendationrequest",
    );
    expect(axiosMock.history.delete[0].params).toEqual({ id: 2 });
  });

  //for admin
  test("Delete button calls delete callback (admin)", async () => {
    // arrange
    const currentUser = currentUserFixtures.adminUser;
    mockedLocation = { pathname: "/requests/completed" };

    const axiosMock = new AxiosMockAdapter(axios);
    axiosMock
      .onDelete("/api/recommendationrequest")
      .reply(200, { message: "Recommendation Request deleted" });

    // act - render the component
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/completed"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    // assert - check that the expected content is rendered

    await waitFor(() => {
      expect(
        screen.getByTestId(`RecommendationRequestTable-cell-row-0-col-id`),
      ).toHaveTextContent("2");
    });

    const deleteButton = screen.getByTestId(
      `RecommendationRequestTable-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();

    // act - click the delete button
    fireEvent.click(deleteButton);

    // assert - check that the delete endpoint was called

    await waitFor(() => expect(axiosMock.history.delete.length).toBe(1));
    expect(axiosMock.history.delete[0].url).toEqual(
      "/api/recommendationrequest/admin",
    );
    expect(axiosMock.history.delete[0].params).toEqual({ id: 2 });
  });

  //for professor
  test("Delete button calls delete callback (professor)", async () => {
    // arrange
    const currentUser = currentUserFixtures.professorUser;
    mockedLocation = { pathname: "/requests/pending" };

    const axiosMock = new AxiosMockAdapter(axios);
    axiosMock
      .onDelete("/api/recommendationrequest")
      .reply(200, { message: "Recommendation Request deleted" });

    // act - render the component
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <RecommendationRequestTable
            requests={recommendationRequestFixtures.threeRecommendations}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    // assert - check that the expected content is rendered

    await waitFor(() => {
      expect(
        screen.getByTestId(`RecommendationRequestTable-cell-row-0-col-id`),
      ).toHaveTextContent("2");
    });

    const deleteButton = screen.getByTestId(
      `RecommendationRequestTable-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();

    // act - click the delete button
    fireEvent.click(deleteButton);

    // assert - check that the delete endpoint was called

    await waitFor(() => expect(axiosMock.history.delete.length).toBe(1));
    expect(axiosMock.history.delete[0].url).toEqual(
      "/api/recommendationrequest/professor",
    );
    expect(axiosMock.history.delete[0].params).toEqual({ id: 2 });
  });
});

vi.mock("react-toastify", () => ({
  toast: vi.fn(),
}));

describe("RecommendationRequestTable update mutation", () => {
  const queryClient = new QueryClient();
  let axiosMock = AxiosMockAdapter;

  beforeEach(() => {
    axiosMock = new AxiosMockAdapter(axios);
    axiosMock
      .onPut("/api/recommendationrequest/professor")
      .reply(200, { message: "Recommendation Request updated" });
  });

  afterEach(() => {
    axiosMock.restore();
    vi.clearAllMocks();
    queryClient.clear();
  });

  test("Clicking Accept sends PUT with IN PROGRESS and toasts success", async () => {
    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const updateButton = screen.getByTestId(
      "RecommendationRequestTable-cell-row-2-col-Update-dropdown",
    );
    fireEvent.click(updateButton);
    expect(updateButton).toHaveClass("btn-info");

    fireEvent.click(await screen.findByText("Accept"));

    await waitFor(() => expect(axiosMock.history.put.length).toBe(1));

    expect(toast).toHaveBeenCalledWith("Request marked as IN PROGRESS.");
  });

  test("Clicking Deny sends PUT with DENIED and toasts success", async () => {
    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-2-col-Update-dropdown",
      ),
    );
    fireEvent.click(await screen.findByText("Deny"));

    await waitFor(() => expect(axiosMock.history.put.length).toBe(1));

    expect(toast).toHaveBeenCalledWith("Request marked as DENIED.");
  });

  test("Clicking Complete sends PUT with Completed and toasts success", async () => {
    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-3-col-Update-dropdown",
      ),
    );
    fireEvent.click(await screen.findByText("Complete"));

    await waitFor(() => expect(axiosMock.history.put.length).toBe(1));

    expect(toast).toHaveBeenCalledWith("Request marked as COMPLETED.");
  });

  test("Update button not render for a non-professor on the pending page", async () => {
    const currentUser = currentUserFixtures.userOnly;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    expect(
      screen.queryByTestId(
        "RecommendationRequestTable-cell-row-0-col-Update-dropdown",
      ),
    ).toBeNull();
  });

  test("Passes the correct status into the Accept callback", async () => {
    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-2-col-Update-dropdown",
      ),
    );

    fireEvent.click(await screen.findByText("Accept"));
    await waitFor(() => expect(axiosMock.history.put.length).toBe(1));
    expect(axiosMock.history.put[0].data).toBe(
      JSON.stringify({ status: "IN PROGRESS" }),
    );
  });

  test("Passes the correct status into the Deny callback", async () => {
    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-2-col-Update-dropdown",
      ),
    );

    fireEvent.click(await screen.findByText("Deny"));
    await waitFor(() => expect(axiosMock.history.put.length).toBe(1));
    expect(axiosMock.history.put[0].data).toBe(
      JSON.stringify({ status: "DENIED" }),
    );
  });

  test("Passes the correct status into the Complete callback", async () => {
    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;
    mockedLocation = { pathname: "/requests/pending" };

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-3-col-Update-dropdown",
      ),
    );

    fireEvent.click(await screen.findByText("Complete"));
    await waitFor(() => expect(axiosMock.history.put.length).toBe(1));
    expect(axiosMock.history.put[0].data).toBe(
      JSON.stringify({ status: "COMPLETED" }),
    );
  });

  test("On success it invalidates the correct apiEndpoint", async () => {
    const apiEndpoint = "/api/recommendationrequest/professor/all";
    const queryClient = new QueryClient();
    const invalidateSpy = vi.spyOn(queryClient, "invalidateQueries");
    mockedLocation = { pathname: "/requests/pending" };

    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-2-col-Update-dropdown",
      ),
    );
    fireEvent.click(await screen.findByText("Accept"));

    await waitFor(() =>
      expect(toast).toHaveBeenCalledWith("Request marked as IN PROGRESS."),
    );

    expect(invalidateSpy).toHaveBeenCalledWith([apiEndpoint]);
  });

  test("Fires onUpdateStatusSuccess when Accept completes", async () => {
    vi.mock("main/utils/RecommendationRequestUtils", { spy: true });
    mockedLocation = { pathname: "/requests/pending" };

    const currentUser = currentUserFixtures.professorUser;
    const rows = recommendationRequestFixtures.mixedRequests;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/requests/pending"]}>
          <Routes>
            <Route
              path="/requests/pending"
              element={
                <RecommendationRequestTable
                  requests={rows}
                  currentUser={currentUser}
                />
              }
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    fireEvent.click(
      await screen.findByTestId(
        "RecommendationRequestTable-cell-row-2-col-Update-dropdown",
      ),
    );
    fireEvent.click(await screen.findByText("Accept"));

    await waitFor(() =>
      expect(toast).toHaveBeenCalledWith("Request marked as IN PROGRESS."),
    );

    expect(onUpdateStatusSuccess).toHaveBeenCalledWith(
      "Request marked as IN PROGRESS.",
    );
    expect(onUpdateStatusSuccess).toHaveBeenCalledTimes(2);
  });
});
