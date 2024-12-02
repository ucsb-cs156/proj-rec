import { Button, Form, Row, Col } from "react-bootstrap";
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

  const navigate = useNavigate();


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
      </Row>

      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="professorEmail">Professor Email</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-professorEmail"
              id="professorEmail"
              type="text"
              isInvalid={Boolean(errors.professorEmail)}
              {...register("professorEmail", {
                required: "professorEmail is required.",
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.professorEmail?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="recommendationType">Request Type</Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-recommendationType"
              id="recommendationType"
              type="string"
              isInvalid={Boolean(errors.recommendationType)}
              {...register("recommendationType", {
                required: "Recommendation Type is required.",
              })}
            >
              <option value="">--Select an Option --</option>
              <option value="Placeholder1">Placeholder 1</option>
              <option value="Placeholder2">Placeholder 2</option>
              <option value="Other">Other</option>
            </Form.Select>
            <Form.Control.Feedback type="invalid">
              {errors.recommendationType?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
    
      </Row>
      <Row>
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
          <Form.Control.Feedback type="invalid">
            {errors.details?.message}
          </Form.Control.Feedback>
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
