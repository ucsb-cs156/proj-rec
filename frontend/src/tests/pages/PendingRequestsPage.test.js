// src/tests/components/PendingRequests/PendingRequestsTable.test.js

import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import PendingRequestsTable from "main/components/PendingRequests/PendingRequestsTable";
import { pendingrequestsFixtures } from "fixtures/pendingrequestsFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import { toast } from "react-toastify";

// Mock react-toastify to prevent actual toasts during tests
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe("PendingRequestsTable tests", () => {
  const queryClient = new QueryClient();
  const axiosMock = new AxiosMockAdapter(axios);

  beforeEach(() => {
    axiosMock.reset();
    axiosMock.resetHistory();
    jest.clearAllMocks();
  });

  test("renders without crashing for an empty table", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable requests={[]} />
      </QueryClientProvider>,
    );

    const headerGroup = screen.getByTestId("PendingRequestsTable-header-group-0");
    expect(headerGroup).toBeInTheDocument();
  });

  test("renders the correct headers and content for one request", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable requests={pendingrequestsFixtures.oneRequest} />
      </QueryClientProvider>,
    );

    const expectedHeaders = ["ID", "Student Email", "Status", "Request Date"];
    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-id")).toHaveTextContent("1");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-studentEmail")).toHaveTextContent("student1@example.com");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-status")).toHaveTextContent("Pending");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-requestDate")).toHaveTextContent("2024-11-01T10:00:00");
  });

  test("renders the correct headers and content for three requests", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable
          requests={pendingrequestsFixtures.threeRequests}
        />
      </QueryClientProvider>,
    );

    const expectedHeaders = ["ID", "Student Email", "Status", "Request Date"];
    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    // Row 0
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-id")).toHaveTextContent("1");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-studentEmail")).toHaveTextContent("student1@example.com");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-status")).toHaveTextContent("Pending");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-requestDate")).toHaveTextContent("2024-11-01T10:00:00");

    // Row 1
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-id")).toHaveTextContent("2");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-studentEmail")).toHaveTextContent("student2@example.com");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-status")).toHaveTextContent("Pending");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-requestDate")).toHaveTextContent("2024-11-02T12:00:00");

    // Row 2
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-id")).toHaveTextContent("3");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-studentEmail")).toHaveTextContent("student3@example.com");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-status")).toHaveTextContent("Accepted");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-requestDate")).toHaveTextContent("2024-11-03T14:00:00");
  });

  test("renders Accept and Deny buttons only for Pending requests", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable requests={pendingrequestsFixtures.threeRequests} />
      </QueryClientProvider>,
    );

    // Only two Pending requests should have "Accept" and "Deny" buttons
    const acceptButtons = screen.getAllByRole('button', { name: 'Accept' });
    const denyButtons = screen.getAllByRole('button', { name: 'Deny' });

    expect(acceptButtons).toHaveLength(2);
    expect(denyButtons).toHaveLength(2);
  });

  test("clicking Accept sends the correct API call", async () => {
    axiosMock.onPost("/api/requests/updateStatus").reply((config) => {
      const requestData = JSON.parse(config.data);
      return [200, { id: requestData.id, status: requestData.status }];
    });

    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable
          requests={pendingrequestsFixtures.threeRequests}
        />
      </QueryClientProvider>,
    );

    const acceptButtons = screen.getAllByRole('button', { name: 'Accept' });
    fireEvent.click(acceptButtons[0]);

    await waitFor(() => {
      expect(axiosMock.history.post.length).toBe(1);
      const postRequest = axiosMock.history.post[0];
      const requestData = JSON.parse(postRequest.data);
      expect(requestData).toEqual({
        id: 1,
        status: "Accepted",
      });
    });

    expect(toast.success).toHaveBeenCalledWith("Request successfully accepted");
  });

  test("clicking Deny sends the correct API call", async () => {
    axiosMock.onPost("/api/requests/updateStatus").reply((config) => {
      const requestData = JSON.parse(config.data);
      return [200, { id: requestData.id, status: requestData.status }];
    });

    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable
          requests={pendingrequestsFixtures.threeRequests}
        />
      </QueryClientProvider>,
    );

    const denyButtons = screen.getAllByRole('button', { name: 'Deny' });
    fireEvent.click(denyButtons[0]);

    await waitFor(() => {
      expect(axiosMock.history.post.length).toBe(1);
      const postRequest = axiosMock.history.post[0];
      const requestData = JSON.parse(postRequest.data);
      expect(requestData).toEqual({
        id: 1,
        status: "Denied",
      });
    });

    expect(toast.success).toHaveBeenCalledWith("Request successfully denied");
  });
});