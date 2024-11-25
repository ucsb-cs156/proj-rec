import { Button, Form, Row, Col } from "react-bootstrap";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function RecommendationRequestForm({
  initialContents,
  submitAction,
  buttonLabel = "Create",
}) {
  // Stryker disable all
  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm({ defaultValues: initialContents || {} });
  // Stryker restore all

  const [professors, setProfessors] = useState([]);
  const [recommendationTypes, setRecommendationTypes] = useState([]);

  //queries endpoint to get list of professors
  useEffect(() => {
    const getProfessors = async () => {
      try {
        const response = await fetch("/api/admin/users/professors");
        if (!response.ok) {
          throw new Error("Failed to fetch professors");
        }
        const data = await response.json();
        setProfessors(data);
      } catch (error) {
        console.error("Error fetching professors:", error);
      }
    };
    const getRequestTypes = async () => {
      try {
        const response = await fetch("/api/requesttype/all");
        if (!response.ok) {
          throw new Error("Failed to fetch request types");
        }
        const data = await response.json();
        setRecommendationTypes(data);
      } catch (error) {
        console.error("Error fetching request types:", error);
      }
    };

    getProfessors();
    getRequestTypes();
  }, []);

  const navigate = useNavigate();

  // For explanation, see: https://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
  // Note that even this complex regex may still need some tweaks

  // Stryker disable Regex

  return (
    <Form onSubmit={handleSubmit(submitAction)}>
      <Row>
        {initialContents && (
          <Col>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="id">Id</Form.Label>
              <Form.Control
                data-testid="RecommendationRequestForm-id"
                id="id"
                type="text"
                {...register("id")}
                value={initialContents.id}
                disabled
              />
            </Form.Group>
          </Col>
        )}
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="professor">
              Professor
            </Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-professor"
              id="professor"
              type="string"
              isInvalid={Boolean(errors.professor)}
              {...register("professor", {})}
            >
              {professors.map((professor) => (
                <option key={professor.id} value={professor.id}>
                  {professor.name}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="recommendationType">
              Recommendation Type
            </Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-recommendationType"
              id="recommendationType"
              type="string"
              isInvalid={Boolean(errors.recommendationType)}
              {...register("recommendationType", {})}
            >
              {recommendationTypes.map((recommendationType) => (
                <option key={recommendationType.id} value={recommendationType.id}>
                  {recommendationType.requestType}
                </option>
              ))}
              <option value="Other">Other</option>
            </Form.Select>
          </Form.Group>
        </Col>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="details">Details</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-details"
              id="details"
              type="text"
              isInvalid={Boolean(errors.details)}
              {...register("details")}
            />
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Button type="submit" data-testid="RecommendationRequestForm-submit">
            {buttonLabel}
          </Button>
          <Button
            variant="Secondary"
            onClick={() => navigate(-1)}
            data-testid="RecommendationRequestForm-cancel"
          >
            Cancel
          </Button>
        </Col>
      </Row>
    </Form>
  );
}

export default RecommendationRequestForm;
