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

  //queries endpoint to get list of professors
  useEffect(() => {
    const getProfessors = async () => {
      try {
        const response = await fetch("/api/admin/users");
        if (!response.ok) {
          throw new Error("Failed to fetch professors");
        }
        const data = await response.json();
        const professorUsers = data.filter(user => user.professor === true);
        setProfessors(professorUsers); // Assuming `data` is an array of professors
      } catch (error) {
        console.error("Error fetching professors:", error);
      }
    };

    getProfessors();
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
              
            </Form.Select>
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="recommendationTypes">
              Recommendation Types
            </Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-recommendationTypes"
              id="recommendationTypes"
              type="string"
              isInvalid={Boolean(errors.recommendationTypes)}
              {...register("recommendationTypes", {})}
            >
              <option value="CS Department BS/MS program">
                CS Department BS/MS program
              </option>
              <option value="Scholarship or Fellowship">
                Scholarship or Fellowship
              </option>
              <option value="MS program (other than CS Dept BS/MS)">
                MS program (other than CS Dept BS/MS)
              </option>
              <option value="PhD program">PhD program</option>
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
