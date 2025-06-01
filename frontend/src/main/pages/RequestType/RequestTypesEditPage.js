import React from "react";
import { useParams } from "react-router-dom";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

export default function RequestTypesEditPage() {
  const { id } = useParams();

  return (
    <BasicLayout>
      <div data-testid="RequestTypesEditPage">
        <h1>Request Types Edit Page for id={id} (Placeholder)</h1>
      </div>
    </BasicLayout>
  );
}
