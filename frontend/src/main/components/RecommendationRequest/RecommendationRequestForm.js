import {
  Button,
  Form,
  Row,
  Col,
  OverlayTrigger,
  Tooltip,
} from "react-bootstrap";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function RecommendationRequestForm({
  initialContents,
  submitAction,
  buttonLabel = "Create",
  recommendationTypeVals = [],
  professorVals = [],
}) {
  // Stryker disable all
  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm({ defaultValues: initialContents || {} });
  // Stryker restore all

  const [professors, setProfessors] = useState(professorVals);
  const [recommendationTypes, setRecommendationTypes] = useState(
    recommendationTypeVals,
  );

  //queries endpoint to get list of professors
  useEffect(() => {
    const getProfessors = async () => {
      try {
        const response = await fetch("/api/admin/users/professors");
        const data = await response.json();
        setProfessors(data);
      } catch (error) {
        console.error("Error fetching professors");
      }
    };
    const getRequestTypes = async () => {
      try {
        const response = await fetch("/api/requesttypes/all");
        const data = await response.json();
        setRecommendationTypes(data);
      } catch (error) {
        console.error("Error fetching request types");
      }
    };

    getProfessors();
    getRequestTypes();
  });

  const onSubmit = (data) => {
    data.dueDate = data.dueDate + "T00:00:00";
    submitAction(data);
  };

  const navigate = useNavigate();

  // For explanation, see: https://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
  // Note that even this complex regex may still need some tweaks

  // Stryker disable Regex

  return (
    <Form onSubmit={handleSubmit(onSubmit)}>
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
            <Form.Label htmlFor="professor_id">Professor</Form.Label>
            <OverlayTrigger
              placement="top"
              overlay={
                <Tooltip>
                  This is the professor from whom you would like to request a
                  recommendation letter.
                </Tooltip>
              }
              delay="100"
            >
              <Form.Select
                data-testid="RecommendationRequestForm-professor_id"
                id="professor_id"
                type="string"
                isInvalid={Boolean(errors.professor_id)}
                {...register("professor_id", {
                  required: "Please select a professor",
                })}
                defaultValue=""
              >
                {Array.isArray(professors) && professors.length > 0 ? (
                  <>
                    <option disabled value="">
                      Select a professor
                    </option>
                    {professors.map((professor) => (
                      <option key={professor.id} value={professor.id}>
                        {professor.fullName}
                      </option>
                    ))}
                  </>
                ) : (
                  <option disabled value="">
                    No professors available
                  </option>
                )}
              </Form.Select>
            </OverlayTrigger>
            {errors.professor_id && (
              <Form.Control.Feedback type="invalid">
                {errors.professor_id.message}
              </Form.Control.Feedback>
            )}
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="recommendationType">
              Recommendation Type
            </Form.Label>
            <OverlayTrigger
              placement="top"
              overlay={
                <Tooltip>
                  This is what you request a recommendation letter for.
                </Tooltip>
              }
              delay="100"
            >
              <Form.Select
                data-testid="RecommendationRequestForm-recommendationType"
                id="recommendationType"
                type="string"
                isInvalid={Boolean(errors.recommendationType)}
                {...register("recommendationType", {
                  required: "Please select a recommendation type",
                })}
                defaultValue=""
              >
                {Array.isArray(recommendationTypes) &&
                recommendationTypes.length > 0 ? (
                  <>
                    <option disabled value="">
                      Select a recommendation type
                    </option>
                    {recommendationTypes.map((recommendationType) => (
                      <option
                        key={recommendationType.id}
                        value={recommendationType.requestType}
                      >
                        {recommendationType.requestType}
                      </option>
                    ))}
                  </>
                ) : (
                  <option disabled value="">
                    No recommendation types available, use Other in details
                  </option>
                )}
                <option value="Other">Other</option>
              </Form.Select>
            </OverlayTrigger>
            {errors.recommendationType && (
              <Form.Control.Feedback type="invalid">
                {errors.recommendationType.message}
              </Form.Control.Feedback>
            )}
          </Form.Group>
        </Col>
      </Row>
      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="details">Details</Form.Label>
            <OverlayTrigger
              placement="top"
              overlay={
                <Tooltip>
                  These are additional details that is helpful for the professor
                  to write the recommendation letter.
                </Tooltip>
              }
              delay="100"
            >
              <Form.Control
                data-testid="RecommendationRequestForm-details"
                id="details"
                type="text"
                isInvalid={Boolean(errors.details)}
                {...register("details")}
              />
            </OverlayTrigger>
          </Form.Group>
        </Col>
      </Row>

      <Row>
        <Col>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="dueDate">Due Date</Form.Label>
            <OverlayTrigger
              placement="top"
              overlay={
                <Tooltip>
                  This is when you would like the professor to submit the
                  recommendation letter by.
                </Tooltip>
              }
              delay="100"
            >
              <Form.Control
                id="dueDate"
                type="date"
                data-testid="RecommendationRequestForm-dueDate"
                isInvalid={Boolean(errors.dueDate)}
                {...register("dueDate", {
                  required: "Please select a due date",
                })}
              />
            </OverlayTrigger>
            {errors.dueDate && (
              <Form.Control.Feedback type="invalid">
                {errors.dueDate.message}
              </Form.Control.Feedback>
            )}
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
