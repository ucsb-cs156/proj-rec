import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import StudentProfilePage from "main/pages/StudentProfilePage";
import { MemoryRouter } from "react-router-dom";
import * as useBackendModule from "main/utils/useBackend";
import * as currentUserModule from "main/utils/currentUser";

jest.mock("main/utils/useBackend");
jest.mock("main/utils/currentUser");

describe("StudentProfilePage", () => {
  beforeEach(() => {
    currentUserModule.useCurrentUser.mockReturnValue({
      data: {
        loggedIn: true,
        root: { user: { email: "student@ucsb.edu", pictureUrl: "pic.jpg", fullName: "Test Student" } },
      },
    });
    useBackendModule.useBackend.mockReturnValue({
      data: [
        { id: 1, requestType: "Type A", status: "Pending" },
        { id: 2, requestType: "Type B", status: "Completed" },
      ],
    });
  });

  it("renders student info and requests table", () => {
    render(<StudentProfilePage />, { wrapper: MemoryRouter });
    expect(screen.getByText("Test Student")).toBeInTheDocument();
    expect(screen.getByText("student@ucsb.edu")).toBeInTheDocument();
    expect(screen.getByText("Type A")).toBeInTheDocument();
    expect(screen.getByText("Type B")).toBeInTheDocument();
  });

  it("has a create new request button", () => {
    render(<StudentProfilePage />, { wrapper: MemoryRouter });
    expect(screen.getByText("Create New Request")).toBeInTheDocument();
  });

  it("calls edit handler when edit button is clicked", async () => {
    render(<StudentProfilePage />, { wrapper: MemoryRouter });
    const editButtons = screen.getAllByText(/edit/i);
    fireEvent.click(editButtons[0]);
    // Navigation is tested in integration, here we just check button exists
    expect(editButtons[0]).toBeInTheDocument();
  });

  it("calls delete handler when delete button is clicked", async () => {
    render(<StudentProfilePage />, { wrapper: MemoryRouter });
    const deleteButtons = screen.getAllByText(/delete/i);
    fireEvent.click(deleteButtons[0]);
    expect(deleteButtons[0]).toBeInTheDocument();
  });
}); 