import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "react-query";
import RequestTypesIndexPage from "main/pages/RequestType/RequestTypesIndexPage";

jest.mock("main/utils/systemInfo", () => ({
  useSystemInfo: () => ({
    data: { springH2ConsoleEnabled: false },
  }),
}));

const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });

describe("RequestTypesIndexPage", () => {
  test("renders placeholder heading", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypesIndexPage />
        </MemoryRouter>
      </QueryClientProvider>
    );

    expect(screen.getByTestId("RequestTypesIndexPage")).toHaveTextContent(
      "Request Types Index Page"
    );
  });
});
