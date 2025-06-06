import { fireEvent, render, screen, waitFor } from "@testing-library/react";

import { BrowserRouter as Router } from "react-router-dom";

import RecommendationRequestForm from "main/components/RecommendationRequest/RecommendationRequestForm";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";

import { QueryClient, QueryClientProvider } from "react-query";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import usersFixtures from "fixtures/usersFixtures";
import recommendationTypeFixtures from "fixtures/recommendationTypeFixtures";

const mockedNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("RecommendationRequestForm tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);
  beforeEach(() => {
    jest.clearAllMocks();
    axiosMock.reset();
    axiosMock.resetHistory();

    axiosMock
      .onGet("/api/admin/users/professors")
      .reply(200, usersFixtures.userOnly);
    axiosMock
      .onGet("/api/requesttypes/all")
      .reply(200, recommendationTypeFixtures.fourTypes);

    global.fetch = jest.fn((url) => {
      if (url.includes("/api/admin/users/professors")) {
        return Promise.resolve({
          json: () => Promise.resolve(usersFixtures.twoProfessors),
        });
      }

      if (url.includes("/api/requesttypes/all")) {
        return Promise.resolve({
          json: () => Promise.resolve(recommendationTypeFixtures.fourTypes),
        });
      }

      return Promise.reject(new Error("Unhandled fetch: " + url));
    });
  });

  afterEach(() => {
    jest.resetAllMocks();
  });
  const queryClient = new QueryClient();

  const expectedHeaders = ["Professor", "Recommendation Type", "Details"];
  const testId = "RecommendationRequestForm";

  test("renders correctly with no initialContents", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm />
        </Router>
      </QueryClientProvider>,
    );

    expect(await screen.findByText(/Create/)).toBeInTheDocument();

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });
  });

  test("renders correctly when passing in initialContents", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm
            initialContents={recommendationRequestFixtures.oneRecommendation}
          />
        </Router>
      </QueryClientProvider>,
    );

    expect(await screen.findByText(/Create/)).toBeInTheDocument();

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expect(await screen.findByTestId(`${testId}-id`)).toBeInTheDocument();
    expect(screen.getByText(`Id`)).toBeInTheDocument();
  });

  test("that the options are filled correctly", async () => {
    global.fetch
      .mockResolvedValueOnce({
        json: () => Promise.resolve(usersFixtures.twoProfessors), // for professors
      })
      .mockResolvedValueOnce({
        json: () => Promise.resolve(recommendationTypeFixtures.fourTypes), // for recommendation types
      });
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm />
        </Router>
      </QueryClientProvider>,
    );
    await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2)); // Ensure fetch was called twice

    // Assert: Check that fetch was called with the correct URLs
    expect(global.fetch).toHaveBeenCalledWith("/api/admin/users/professors");
    expect(global.fetch).toHaveBeenCalledWith("/api/requesttypes/all");
    await waitFor(() => {
      usersFixtures.twoProfessors.forEach((professor) => {
        expect(screen.getByText(professor.fullName)).toBeInTheDocument();
      });
      expect(screen.getByText("Select a professor")).toBeInTheDocument();
    });
    await waitFor(() => {
      recommendationTypeFixtures.fourTypes.forEach((type) => {
        expect(screen.getByText(type.requestType)).toBeInTheDocument();
      });
    });
  });

  test("that the correct error appears when the gets are called for the options", async () => {
    const consoleErrorMock = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});
    global.fetch = jest.fn().mockRejectedValueOnce(new Error("Network error"));
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm />
        </Router>
      </QueryClientProvider>,
    );
    await waitFor(() => {
      // Here, you can check for side effects or verify the console error is called.
      // In this case, we assume the error is logged to the console.
      expect(global.console.error).toHaveBeenCalledWith(
        "Error fetching request types",
      );
    });
    await waitFor(() => {
      // Here, you can check for side effects or verify the console error is called.
      // In this case, we assume the error is logged to the console.
      expect(global.console.error).toHaveBeenCalledWith(
        "Error fetching professors",
      );
    });
    consoleErrorMock.mockRestore();
  });

  test("that the initial value of professors and recommendationTypes is only defaults", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm />
        </Router>
      </QueryClientProvider>,
    );
    expect(screen.getByText("No professors available")).toBeInTheDocument();
    expect(
      screen.getByText(
        "No recommendation types available, use Other in details",
      ),
    ).toBeInTheDocument();
    expect(screen.getByText("Other")).toBeInTheDocument();

    // Assert that no professor options are rendered yet
    const options = screen.queryAllByRole("option");
    expect(options).toHaveLength(3);
  });

  test("that navigate(-1) is called when Cancel is clicked", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm />
        </Router>
      </QueryClientProvider>,
    );
    expect(await screen.findByTestId(`${testId}-cancel`)).toBeInTheDocument();
    const cancelButton = screen.getByTestId(`${testId}-cancel`);

    fireEvent.click(cancelButton);

    await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));
  });

  test("that the correct validations are performed", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm />
        </Router>
      </QueryClientProvider>,
    );

    expect(await screen.findByText(/Create/)).toBeInTheDocument();
    const submitButton = screen.getByText(/Create/);
    fireEvent.click(submitButton);

    await screen.findByText(/Please select a professor/);
    expect(
      screen.getByText(/Please select a recommendation type/),
    ).toBeInTheDocument();
    expect(screen.getByText(/Please select a due date/)).toBeInTheDocument();
  });

  test("submitAction is called with correctly formatted dueDate", async () => {
    const mockSubmit = jest.fn();

    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RecommendationRequestForm
            initialContents={{
              id: 1,
              professor_id: "1",
              recommendationType: "Other",
              details: "Test details",
              dueDate: "2025-05-18",
            }}
            professorVals={usersFixtures.twoProfessors}
            recommendationTypeVals={recommendationTypeFixtures.fourTypes}
            submitAction={mockSubmit}
          />
        </Router>
      </QueryClientProvider>,
    );

    fireEvent.change(screen.getByTestId("RecommendationRequestForm-dueDate"), {
      target: { value: "2025-05-19" },
    });

    fireEvent.click(screen.getByTestId("RecommendationRequestForm-submit"));

    await waitFor(() =>
      expect(mockSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          dueDate: "2025-05-19T00:00:00",
        }),
      ),
    );
  });
});
