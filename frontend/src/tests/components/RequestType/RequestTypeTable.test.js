import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import { requestFixtures } from "fixtures/requestFixtures";
import RequestTypeTable from "main/components/RequestType/RequestTypeTable";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import { hasRole } from "main/utils/currentUser";

const mockedNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("UserTable tests", () => {
  const queryClient = new QueryClient();

  test("Has the expected column headers and content for ordinary user", () => {
    const currentUser = currentUserFixtures.userOnly;

    expect(hasRole(currentUser, "ROLE_USER")).toBe(true);
    expect(hasRole(currentUser, "ROLE_ADMIN")).toBe(false);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const expectedHeaders = ["id", "Request Type"];
    const expectedFields = ["id", "requestType"];
    const testId = "RequestTypeTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
      "1",
    );
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "2",
    );
  });

  test("Has the expected column headers and content for professorUser", () => {
    const currentUser = currentUserFixtures.professorUser;

    expect(hasRole(currentUser, "ROLE_PROFESSOR")).toBe(true);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const expectedHeaders = ["id", "Request Type"];
    const expectedFields = ["id", "requestType"];
    const testId = "RequestTypeTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
      "1",
    );
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "2",
    );

    const deleteButton = screen.getByTestId(
      `${testId}-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();
    expect(deleteButton).toHaveClass("btn-danger");

    const editButton = screen.queryByTestId(
      `${testId}-cell-row-0-col-Edit-button`,
    );
    expect(editButton).toBeInTheDocument();
    expect(editButton).toHaveClass("btn-primary");
  });

  test("Has the expected column headers and content for adminUser", () => {
    const currentUser = currentUserFixtures.adminUser;

    expect(hasRole(currentUser, "ROLE_ADMIN")).toBe(true);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const expectedHeaders = ["id", "Request Type"];
    const expectedFields = ["id", "requestType"];
    const testId = "RequestTypeTable";

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
      "1",
    );
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "2",
    );

    const deleteButton = screen.getByTestId(
      `${testId}-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();
    expect(deleteButton).toHaveClass("btn-danger");

    const editButton = screen.getByTestId(
      `${testId}-cell-row-0-col-Edit-button`,
    );
    expect(editButton).toBeInTheDocument();
    expect(editButton).toHaveClass("btn-primary");
  });

  test("Edit button navigates to the edit page for professorUser", async () => {
    const currentUser = currentUserFixtures.professorUser;

    expect(hasRole(currentUser, "ROLE_PROFESSOR")).toBe(true);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(
        screen.getByTestId(`RequestTypeTable-cell-row-0-col-id`),
      ).toHaveTextContent("1");
    });

    const editButton = screen.getByTestId(
      `RequestTypeTable-cell-row-0-col-Edit-button`,
    );
    expect(editButton).toBeInTheDocument();
    expect(editButton).toHaveClass("btn-primary");

    fireEvent.click(editButton);

    await waitFor(() =>
      expect(mockedNavigate).toHaveBeenCalledWith("/requesttypes/edit/1"),
    );
  });

  test("Edit button navigates to the edit page for adminUser", async () => {
    const currentUser = currentUserFixtures.adminUser;

    expect(hasRole(currentUser, "ROLE_ADMIN")).toBe(true);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(
        screen.getByTestId(`RequestTypeTable-cell-row-0-col-id`),
      ).toHaveTextContent("1");
    });

    const editButton = screen.getByTestId(
      `RequestTypeTable-cell-row-0-col-Edit-button`,
    );
    expect(editButton).toBeInTheDocument();
    expect(editButton).toHaveClass("btn-primary");

    fireEvent.click(editButton);

    await waitFor(() =>
      expect(mockedNavigate).toHaveBeenCalledWith("/requesttypes/edit/1"),
    );
  });

  //Added for mutation coverage for the case in which the user is neither a user nor an admin
  test("A user with no roles has expected content", () => {
    const currentUser = currentUserFixtures.notLoggedIn;

    expect(hasRole(currentUser, "ROLE_USER")).toBe(undefined);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    const testId = "RequestTypeTable";

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
  test("Delete button calls delete callback (for professorUser)", async () => {
    // arrange
    const currentUser = currentUserFixtures.professorUser;

    const axiosMock = new AxiosMockAdapter(axios);
    axiosMock
      .onDelete("/api/requesttypes")
      .reply(200, { message: "Request Type deleted" });

    // act - render the component
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    // assert - check that the expected content is rendered

    await waitFor(() => {
      expect(
        screen.getByTestId(`RequestTypeTable-cell-row-0-col-id`),
      ).toHaveTextContent("1");
    });

    const deleteButton = screen.getByTestId(
      `RequestTypeTable-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();

    // act - click the delete button
    fireEvent.click(deleteButton);

    // assert - check that the delete endpoint was called

    await waitFor(() => expect(axiosMock.history.delete.length).toBe(1));
    expect(axiosMock.history.delete[0].params).toEqual({ id: 1 });
  });

  test("Delete button calls delete callback (for adminUser)", async () => {
    const currentUser = currentUserFixtures.adminUser;

    const axiosMock = new AxiosMockAdapter(axios);
    axiosMock
      .onDelete("/api/requesttypes")
      .reply(200, { message: "Request Type deleted" });

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestTypeTable
            requestTypes={requestFixtures.fourTypes}
            currentUser={currentUser}
          />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(
        screen.getByTestId(`RequestTypeTable-cell-row-0-col-id`),
      ).toHaveTextContent("1");
    });

    const deleteButton = screen.getByTestId(
      `RequestTypeTable-cell-row-0-col-Delete-button`,
    );
    expect(deleteButton).toBeInTheDocument();

    fireEvent.click(deleteButton);

    await waitFor(() => expect(axiosMock.history.delete.length).toBe(1));
    expect(axiosMock.history.delete[0].params).toEqual({ id: 1 });
  });
});
