import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter as Router } from "react-router";
import { QueryClient, QueryClientProvider } from "react-query";

import RequestTypeForm from "main/components/RequestType/RequestTypeForm";
import requestTypeFixtures from "fixtures/requestTypeFixtures";
import { vi } from "vitest";

const mockedNavigate = vi.fn();

vi.mock("react-router", async (importOriginal) => ({
  ...(await importOriginal()),
  useNavigate: () => mockedNavigate,
}));

describe("RequestTypeForm tests", () => {
  const queryClient = new QueryClient();
  const testId = "RequestTypeForm";

  const renderForm = (props = {}) =>
    render(
      <QueryClientProvider client={queryClient}>
        <Router>
          <RequestTypeForm submitAction={vi.fn()} {...props} />
        </Router>
      </QueryClientProvider>,
    );

  test("renders correctly with no initialContents", async () => {
    renderForm();

    expect(await screen.findByText("Create")).toBeInTheDocument();
    expect(screen.getByTestId(`${testId}-requestType`)).toBeInTheDocument();
  });

  test("renders correctly with initialContents", async () => {
    const initialContents = requestTypeFixtures.oneType[0];

    renderForm({ initialContents });

    expect(await screen.findByTestId(`${testId}-id`)).toBeInTheDocument();
    expect(screen.getByTestId(`${testId}-id`)).toHaveValue(
      String(initialContents.id),
    );
    expect(screen.getByTestId(`${testId}-requestType`)).toHaveValue(
      initialContents.requestType,
    );
  });

  test("calls submitAction with correct data on valid input", async () => {
    const mockSubmitAction = vi.fn();
    renderForm({ submitAction: mockSubmitAction });

    const requestTypeInput = await screen.findByTestId(`${testId}-requestType`);
    const submitButton = screen.getByTestId(`${testId}-submit`);

    fireEvent.change(requestTypeInput, {
      target: { value: "NewType" },
    });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockSubmitAction).toHaveBeenCalledWith(
        { requestType: "NewType" },
        expect.anything(),
      );
    });
  });

  test("shows validation error on missing input", async () => {
    renderForm();

    const submitButton = await screen.findByTestId(`${testId}-submit`);
    fireEvent.click(submitButton);

    expect(
      await screen.findByText("requestType is required."),
    ).toBeInTheDocument();
  });

  test("does NOT show validation error when requestType is provided", async () => {
    renderForm();

    const requestTypeInput = await screen.findByTestId(`${testId}-requestType`);
    const submitButton = screen.getByTestId(`${testId}-submit`);

    fireEvent.change(requestTypeInput, {
      target: { value: "ValidType" },
    });

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(
        screen.queryByText("requestType is required."),
      ).not.toBeInTheDocument();
    });
  });

  test("navigates back on cancel click", async () => {
    renderForm();

    const cancelButton = await screen.findByTestId(`${testId}-cancel`);
    fireEvent.click(cancelButton);

    await waitFor(() => {
      expect(mockedNavigate).toHaveBeenCalledWith(-1);
    });
  });

  test("renders with custom button label", async () => {
    renderForm({ buttonLabel: "Save Request Type" });

    expect(await screen.findByText("Save Request Type")).toBeInTheDocument();
  });
});
