import { render, screen, fireEvent } from "@testing-library/react";
import CreateRecommendationRequestPage from "main/pages/Requests/CreateRecommendationRequestPage";
import { MemoryRouter } from "react-router-dom";

jest.mock("main/components/RecommendationRequest/RecommendationRequestForm", () => ({
  __esModule: true,
  default: ({ onSubmit }) => (
    <button onClick={() => onSubmit({ requestType: "Type A" })}>Submit</button>
  ),
}));

describe("CreateRecommendationRequestPage", () => {
  it("renders and submits form", () => {
    render(<CreateRecommendationRequestPage />, { wrapper: MemoryRouter });
    const submitButton = screen.getByText("Submit");
    expect(submitButton).toBeInTheDocument();
    global.fetch = jest.fn(() => Promise.resolve({ ok: true }));
    fireEvent.click(submitButton);
    // No assertion for redirect, but fetch should be called
    expect(global.fetch).toHaveBeenCalledWith(
      "/api/recommendationrequest/post",
      expect.objectContaining({ method: "POST" })
    );
  });
}); 