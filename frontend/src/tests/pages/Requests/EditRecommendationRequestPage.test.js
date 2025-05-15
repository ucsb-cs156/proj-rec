import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import EditRecommendationRequestPage from "main/pages/Requests/EditRecommendationRequestPage";
import { MemoryRouter, Route, Routes } from "react-router-dom";

jest.mock("main/components/RecommendationRequest/RecommendationRequestForm", () => ({
  __esModule: true,
  default: ({ onSubmit, initialData }) => (
    <>
      <div>Initial: {initialData?.requestType}</div>
      <button onClick={() => onSubmit({ requestType: "Type B" })}>Submit</button>
    </>
  ),
}));

global.fetch = jest.fn((url) => {
  if (url.startsWith("/api/recommendationrequest?id=")) {
    return Promise.resolve({ json: () => Promise.resolve({ id: 1, requestType: "Type A", status: "Pending" }) });
  }
  return Promise.resolve({ ok: true });
});

describe("EditRecommendationRequestPage", () => {
  it("loads initial data and submits form", async () => {
    render(
      <MemoryRouter initialEntries={["/recommendation-requests/edit/1"]}>
        <Routes>
          <Route path="/recommendation-requests/edit/:id" element={<EditRecommendationRequestPage />} />
        </Routes>
      </MemoryRouter>
    );
    await waitFor(() => expect(screen.getByText(/Initial: Type A/)).toBeInTheDocument());
    const submitButton = screen.getByText("Submit");
    fireEvent.click(submitButton);
    expect(global.fetch).toHaveBeenCalledWith(
      "/api/recommendationrequest?id=1",
      expect.objectContaining({ method: "PUT" })
    );
  });
}); 