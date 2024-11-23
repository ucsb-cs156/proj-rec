import React from "react";
import RequestTypeTable from "main/components/RequestType/RequestTypeTable";
import { requesttypeFixtures } from "fixtures/requesttypeFixtures";
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
  requestTypes: [],
  currentUser: currentUserFixtures.userOnly,
};

export const ThreeItemsOrdinaryUser = Template.bind({});

ThreeItemsOrdinaryUser.args = {
  requestTypes: requesttypeFixtures.threeRequestTypes,
  currentUser: currentUserFixtures.userOnly,
};

export const ThreeItemsAdminUser = Template.bind({});
ThreeItemsAdminUser.args = {
  requestTypes: requesttypeFixtures.threeRequestTypes,
  currentUser: currentUserFixtures.adminUser,
};

ThreeItemsAdminUser.parameters = {
  msw: [
    http.delete("/api/request-types", () => {
      return HttpResponse.json(
        { message: "Request Type deleted successfully" },
        { status: 200 },
      );
    }),
  ],
};