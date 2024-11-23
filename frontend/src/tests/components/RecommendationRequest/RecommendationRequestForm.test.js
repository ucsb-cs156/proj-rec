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
    await screen.findByText(/Professor Name/);
    await screen.findByText(/Create/);
  });

  test("renders correctly when passing in a RecommendationRequest", async () => {
    render(
      <Router>
        <RecommendationRequestForm initialContents={recommendationRequestFixtures.oneRecommendationRequest} />
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
    const recommendationTypesField = screen.getByTestId("RecommendationRequestForm-recommendationTypes");
    const detailsField = screen.getByTestId("RecommendationRequestForm-details");
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.change(recommendationTypesField, { target: { value: "" } });
    fireEvent.change(detailsField, { target: { value: "" } });
    fireEvent.click(submitButton);

    await screen.findByText(/Request Type is required./);
    expect(screen.getByText(/details is required./)).toBeInTheDocument();
  });

  test("Correct error messages on missing input", async () => {
    render(
      <Router>
        <RecommendationRequestForm />
      </Router>,
    );
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.click(submitButton);

    await screen.findByText(/professorName is required./);
    expect(screen.getByText(/professorEmail is required./)).toBeInTheDocument();
    expect(screen.getByText(/requesterName is required./)).toBeInTheDocument();
    expect(screen.getByText(/Request Type is required./)).toBeInTheDocument();
    expect(screen.getByText(/details is required./)).toBeInTheDocument();
  });

  test("No error messages on good input", async () => {
    const mockSubmitAction = jest.fn();

    render(
      <Router>
        <RecommendationRequestForm submitAction={mockSubmitAction} />
      </Router>,
    );

    const professorNameField = screen.getByTestId("RecommendationRequestForm-professorName");
    const professorEmailField = screen.getByTestId("RecommendationRequestForm-professorEmail");
    const requesterNameField = screen.getByTestId("RecommendationRequestForm-requesterName");
    const detailsField = screen.getByTestId("RecommendationRequestForm-details");
    const recommendationTypesField = screen.getByTestId("RecommendationRequestForm-recommendationTypes");
    const submitButton = screen.getByTestId("RecommendationRequestForm-submit");

    fireEvent.change(professorNameField, { target: { value: "Dr. Smith" } });
    fireEvent.change(professorEmailField, { target: { value: "drsmith@university.edu" } });
    fireEvent.change(requesterNameField, { target: { value: "Student A" } });
    fireEvent.change(detailsField, { target: { value: "Letter for graduate school" } });
    fireEvent.change(recommendationTypesField, { target: { value: "Placeholder1" } });
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
