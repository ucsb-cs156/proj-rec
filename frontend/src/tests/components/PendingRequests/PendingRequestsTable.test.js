import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import PendingRequestsTable from "main/components/PendingRequests/PendingRequestsTable";
import { pendingrequestsFixtures } from "fixtures/pendingrequestsFixtures";

describe("PendingRequestsTable tests", () => {
  const queryClient = new QueryClient();

  test("renders without crashing for an empty table", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable requests={[]} />
      </QueryClientProvider>,
    );

    // Check for the presence of the header group or any header
    const headerGroup = screen.getByTestId("PendingRequestsTable-header-group-0");
    expect(headerGroup).toBeInTheDocument();
  });

  test("renders the correct headers and content for one request", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable requests={pendingrequestsFixtures.oneRequest} />
      </QueryClientProvider>,
    );

    // Verify headers
    const expectedHeaders = ["ID", "Student Email", "Status", "Request Date"];
    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    // Verify content for the single row
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-id")).toHaveTextContent("1");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-studentEmail")).toHaveTextContent(
      "student1@example.com",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-status")).toHaveTextContent(
      "Pending",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-requestDate")).toHaveTextContent(
      "2024-11-01T10:00:00",
    );
  });

  test("renders the correct headers and content for three requests", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <PendingRequestsTable requests={pendingrequestsFixtures.threeRequests} />
      </QueryClientProvider>,
    );

    const expectedHeaders = ["ID", "Student Email", "Status", "Request Date"];

    // Verify headers
    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    // Verify content for the first row
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-id")).toHaveTextContent("1");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-studentEmail")).toHaveTextContent(
      "student1@example.com",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-status")).toHaveTextContent(
      "Pending",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-0-col-requestDate")).toHaveTextContent(
      "2024-11-01T10:00:00",
    );

    // Verify content for the second row
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-id")).toHaveTextContent("2");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-studentEmail")).toHaveTextContent(
      "student2@example.com",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-status")).toHaveTextContent(
      "Pending",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-1-col-requestDate")).toHaveTextContent(
      "2024-11-02T12:00:00",
    );

    // Verify content for the third row
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-id")).toHaveTextContent("3");
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-studentEmail")).toHaveTextContent(
      "student3@example.com",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-status")).toHaveTextContent(
      "Accepted",
    );
    expect(screen.getByTestId("PendingRequestsTable-cell-row-2-col-requestDate")).toHaveTextContent(
      "2024-11-03T14:00:00",
    );
  });
});
