import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "react-query";
import RequestTypesCreatePage from "main/pages/RequestType/RequestTypesCreatePage";

// mock out /api/systemInfo so BasicLayout doesn't hit the network
jest.mock("main/utils/systemInfo", () => ({
  useSystemInfo: () => ({
    data: { springH2ConsoleEnabled: false },
  }),
}));

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false } },
});

describe("RequestTypesCreatePage", () => {
  test("renders placeholder heading", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypesCreatePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    expect(screen.getByTestId("RequestTypesCreatePage")).toHaveTextContent(
      "Request Types Create Page",
    );
  });
});
