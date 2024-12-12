import { render, waitFor, fireEvent, screen } from "@testing-library/react";
import RecommendationRequestForm from "main/components/RecommendationRequest/RecommendationRequestForm";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";
import { BrowserRouter as Router } from "react-router-dom";

const mockedNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedNavigate,
}));

describe("RecommendationRequestForm tests", () => {
  test("renders correctly", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );
    await screen.findByText(/Professor Email/);
    await screen.findByText(/Create/);
  });

  test("renders correctly when passing in a RecommendationRequest", async () => {
    render(
      <Router>
        <RecommendationRequestForm
          initialContents={
            recommendationRequestFixtures.oneRecommendationRequest
          }
        />
      </Router>,
    );
    await screen.findByTestId("RecommendationRequestForm-id");
    expect(screen.getByText(/Id/)).toBeInTheDocument();
    expect(screen.getByTestId("RecommendationRequestForm-id")).toHaveValue("1");
  });

  test("Correct error messages on bad input", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );
    const recommendationTypesField = screen.getByTestId(
      "RecommendationRequestForm-recommendationType",
    );
    const detailsField = screen.getByTestId(
      "RecommendationRequestForm-details",
    );
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.change(recommendationTypesField, { target: { value: "" } });
    fireEvent.change(detailsField, { target: { value: "" } });
    fireEvent.click(submitButton);

    await screen.findByText(/Recommendation Type is required./);
    expect(screen.getByText(/professorEmail is required./)).toBeInTheDocument();
  });

  test("Correct error messages on missing input", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.click(submitButton);

    await screen.findByText(/Recommendation Type is required./);

    expect(
      screen.getByText(/Recommendation Type is required./),
    ).toBeInTheDocument();
  });

  test("No error messages on good input", async () => {
    const mockSubmitAction = jest.fn();

    render(
      <Router>
        <RecommendationRequestForm submitAction={mockSubmitAction} />
      </Router>,
    );

    const professorEmailField = screen.getByTestId(
      "RecommendationRequestForm-professorEmail",
    );

    const detailsField = screen.getByTestId(
      "RecommendationRequestForm-details",
    );
    const recommendationTypeField = screen.getByTestId(
      "RecommendationRequestForm-recommendationType",
    );
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.change(professorEmailField, {
      target: { value: "drsmith@university.edu" },
    });
    fireEvent.change(detailsField, {
      target: { value: "Letter for graduate school" },
    });
    fireEvent.change(recommendationTypeField, {
      target: { value: "Placeholder1" },
    });
    fireEvent.click(submitButton);

    await waitFor(() => expect(mockSubmitAction).toHaveBeenCalled());

    expect(screen.queryByText(/is required/)).not.toBeInTheDocument();
  });

  test("that navigate(-1) is called when Cancel is clicked", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );
    const cancelButton = screen.getByTestId("RecommendationRequestForm-cancel");

    fireEvent.click(cancelButton);

    await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));
  });
});
