import { render, screen } from "@testing-library/react";
import PendingRequestsTable from "main/components/PendingRequests/PendingRequestsTable";
import { pendingrequestsFixtures } from "fixtures/pendingrequestsFixtures";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

describe("PendingRequestsTable tests", () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  test("renders correctly with data", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <PendingRequestsTable requests={pendingrequestsFixtures.threeRequests} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    expect(screen.getByText("student1@example.com")).toBeInTheDocument();
    expect(screen.getByText("student2@example.com")).toBeInTheDocument();
    expect(screen.getByText("student3@example.com")).toBeInTheDocument();
  });

  test("renders correctly without data", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <PendingRequestsTable requests={[]} />
        </MemoryRouter>
      </QueryClientProvider>
    );

    const rows = screen.queryAllByRole("row");
    expect(rows).toHaveLength(1);

    expect(screen.queryByText("student1@example.com")).not.toBeInTheDocument();
    expect(screen.queryByText("student2@example.com")).not.toBeInTheDocument();
    expect(screen.queryByText("student3@example.com")).not.toBeInTheDocument();
  });
});
