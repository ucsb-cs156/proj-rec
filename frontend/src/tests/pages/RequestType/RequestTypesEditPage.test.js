import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "react-query";
import RequestTypesEditPage from "main/pages/RequestType/RequestTypesEditPage";

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false } },
});

describe("RequestTypesEditPage", () => {
  test("renders placeholder with ID", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={["/settings/requesttypes/create/123"]}>
          <Routes>
            <Route
              path="/settings/requesttypes/create/:id"
              element={<RequestTypesEditPage />}
            />
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    );

    expect(screen.getByTestId("RequestTypesEditPage")).toHaveTextContent(
      "id=123",
    );
  });
});
