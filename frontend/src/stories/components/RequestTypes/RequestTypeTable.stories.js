import React from "react";
import RequestTypeTable from "main/components/RequestTypes/RequestTypeTable";
import { requestTypeFixtures } from "fixtures/requestTypeFixtures";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import { http, HttpResponse } from "msw";

export default {
  title: "components/RequestTypes/RequestTypeTable",
  component: RequestTypeTable,
};

const Template = (args) => {
  return <RequestTypeTable {...args} />;
};

export const Empty = Template.bind({});

Empty.args = {
  requestTypes: [],
  currentUser: currentUserFixtures.userOnly,
};

export const ThreeItemsOrdinaryUser = Template.bind({});

ThreeItemsOrdinaryUser.args = {
  requestTypes: requestTypeFixtures.threeRequestTypes,
  currentUser: currentUserFixtures.userOnly,
};

export const ThreeItemsAdminUser = Template.bind({});
ThreeItemsAdminUser.args = {
  requestTypes: requestTypeFixtures.threeRequestTypes,
  currentUser: currentUserFixtures.adminUser,
};

ThreeItemsAdminUser.parameters = {
  msw: [
    http.delete("/api/requesttypes", () => {
      return HttpResponse.json(
        { message: "Request Type deleted successfully" },
        { status: 200 },
      );
    }),
  ],
};
