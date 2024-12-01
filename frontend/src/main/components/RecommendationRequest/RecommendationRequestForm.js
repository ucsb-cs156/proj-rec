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

  // For explanation, see: https://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
  // Note that even this complex regex may still need some tweaks

  // Stryker disable Regex

  // Stryker disable next-line all

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
            <Form.Label htmlFor="professorName">Professor Name</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-professorName"
              id="professorName"
              type="text"
              isInvalid={Boolean(errors.professorName)}
              {...register("professorName", {
                required: "professorName is required.",
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.professorName?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
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
            <Form.Label htmlFor="requesterName">Requester Name</Form.Label>
            <Form.Control
              data-testid="RecommendationRequestForm-requesterName"
              id="requesterName"
              type="text"
              isInvalid={Boolean(errors.requesterName)}
              {...register("requesterName", {
                required: "requesterName is required.",
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.requesterName?.message}
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
              {...register("details", {
                required: "details is required.",
              })}
            />
            <Form.Control.Feedback type="invalid">
              {errors.details?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="recommendationTypes">Request Type</Form.Label>
            <Form.Select
              data-testid="RecommendationRequestForm-recommendationTypes"
              id="recommendationTypes"
              type="string"
              isInvalid={Boolean(errors.recommendationTypes)}
              {...register("recommendationTypes", {
                required: "Request Type is required.",
              })}
            >
              <option value="">--Select an Option --</option>
              <option value="Placeholder1">Placeholder 1</option>
              <option value="Placeholder2">Placeholder 2</option>
              <option value="Other">Other</option>
            </Form.Select>
            <Form.Control.Feedback type="invalid">
              {errors.recommendationTypes?.message}
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
