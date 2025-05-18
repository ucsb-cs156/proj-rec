import React from "react";
import RequestTypeTable from "main/components/RequestType/RequestTypeTable";
import { requestFixtures } from "fixtures/requestFixtures";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import { http, HttpResponse } from "msw";

export default {
  title: "components/RequestType/RequestTypeTable",
  component: RequestTypeTable,
};

const Template = (args) => {
  return <RequestTypeTable {...args} />;
};

export const Empty = Template.bind({});

Empty.args = {
  requests: [],
};

export const FourItemsOrdinaryUser = Template.bind({});

FourItemsOrdinaryUser.args = {
  requests: requestFixtures.fourTypes,
  currentUser: currentUserFixtures.userOnly,
};

export const FourItemsProfessorUser = Template.bind({});
FourItemsProfessorUser.args = {
  requests: requestFixtures.fourTypes,
  currentUser: currentUserFixtures.professorUser,
};

FourItemsProfessorUser.parameters = {
  msw: [
    http.delete("/api/requesttypes", () => {
      return HttpResponse.json({}, { status: 200 });
    }),
  ],
};
