import { compareValues } from "main/utils/sortHelper";
import React, { useState } from "react";
import { Form } from "react-bootstrap";

const SingleRequestDropdown = ({
  requests,
  request,
  setrequest,
  controlId,
  onChange = null,
  label = "Request Area",
}) => {
  const localSearchrequest = localStorage.getItem(controlId);

  const [requestState, setrequestState] = useState(
    // Stryker disable next-line all : not sure how to test/mock local storage
    localSearchrequest || request,
  );

  const handleRequestOnChange = (event) => {
    localStorage.setItem(controlId, event.target.value);
    setrequestState(event.target.value);
    setrequest(event.target.value);
    if (onChange != null) {
      onChange(event);
    }
  };

  requests.sort(compareValues("requestCode"));

  return (
    <Form.Group controlId={controlId}>
      <Form.Label>{label}</Form.Label>
      <Form.Control
        as="select"
        value={requestState}
        onChange={handleRequestOnChange}
      >
        {requests.map(function (object) {
          const key = `${controlId}-option-${object.id}`;
          return (
            <option key={key} data-testid={key}>
              {object.requestType}
            </option>
          );
        })}
      </Form.Control>
    </Form.Group>
  );
};

export default SingleRequestDropdown;