import { render, screen } from "@testing-library/react";
import usersFixtures from "fixtures/usersFixtures";
import UsersTable from "main/components/Users/UsersTable";
import { QueryClient, QueryClientProvider } from "react-query";
import userEvent from "@testing-library/user-event";
import { useBackendMutation } from "main/utils/useBackend";
jest.mock("main/utils/useBackend");

describe("UserTable tests", () => {
  const queryClient = new QueryClient();

  test("renders without crashing for empty table", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <UsersTable users={[]} />
      </QueryClientProvider>,
    );
  });

  test("renders without crashing for three users", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <UsersTable users={usersFixtures.threeUsers} />
      </QueryClientProvider>,
    );
  });

  test("Has the expected colum headers and content", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <UsersTable users={usersFixtures.threeUsers} />
      </QueryClientProvider>,
    );

    const expectedHeaders = [
      "id",
      "First Name",
      "Last Name",
      "Email",
      "Admin",
      "Professor",
    ];
    const expectedFields = [
      "id",
      "givenName",
      "familyName",
      "email",
      "admin",
      "professor",
    ];
    const testId = "UsersTable";

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
    expect(
      screen.getByTestId(`${testId}-cell-row-0-col-admin`),
    ).toHaveTextContent("true");
    expect(
      screen.getByTestId(`${testId}-cell-row-0-col-professor`),
    ).toHaveTextContent("false");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "2",
    );
    expect(
      screen.getByTestId(`${testId}-cell-row-1-col-admin`),
    ).toHaveTextContent("false");
    expect(
      screen.getByTestId(`${testId}-cell-row-1-col-professor`),
    ).toHaveTextContent("false");
    expect(screen.getByTestId(`${testId}-cell-row-2-col-id`)).toHaveTextContent(
      "3",
    );
    expect(
      screen.getByTestId(`${testId}-cell-row-2-col-professor`),
    ).toHaveTextContent("true");
    expect(
      screen.getByTestId(`${testId}-cell-row-2-col-admin`),
    ).toHaveTextContent("false");
  });
});

describe("UsersTable toggle callbacks", () => {
  const queryClient = new QueryClient();

  let mutateAdmin;
  let mutateProfessor;

  beforeEach(() => {
    mutateAdmin = jest.fn();
    mutateProfessor = jest.fn();

    useBackendMutation
      .mockImplementationOnce(() => ({ mutate: mutateAdmin }))
      .mockImplementationOnce(() => ({ mutate: mutateProfessor }));
  });

  test("clicking Toggle Admin calls the admin mutate with the right cell", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <UsersTable users={usersFixtures.threeUsers} />
      </QueryClientProvider>,
    );

    const toggleAdminButtons = screen.getAllByRole("button", { name: "Toggle Admin" });
    expect(toggleAdminButtons).toHaveLength(3);

    await userEvent.click(toggleAdminButtons[0]);

    expect(mutateAdmin).toHaveBeenCalledWith(
      expect.objectContaining({
        row: expect.objectContaining({ index: 0 }),
      })
    );
  });

  test("clicking Toggle Professor calls the professor mutate with the right cell", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <UsersTable users={usersFixtures.threeUsers} />
      </QueryClientProvider>,
    );

    const toggleProfButtons = screen.getAllByRole("button", { name: "Toggle Professor" });
    expect(toggleProfButtons).toHaveLength(3);

    await userEvent.click(toggleProfButtons[2]);

    expect(mutateProfessor).toHaveBeenCalledWith(
      expect.objectContaining({
        row: expect.objectContaining({ index: 2 }),
      })
    );
  });
});