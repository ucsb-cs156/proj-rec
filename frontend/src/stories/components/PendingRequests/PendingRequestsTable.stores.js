import React from "react";
import PendingRequestsTable from "main/components/PendingRequests/PendingRequestsTable";
import { pendingrequestsFixtures } from "fixtures/pendingrequestsFixtures";

export default {
  title: "components/PendingRequestsTable",
  component: PendingRequestsTable,
};

const Template = (args) => <PendingRequestsTable {...args} />;

export const Empty = Template.bind({});
Empty.args = {
  requests: [],
};

export const WithData = Template.bind({});
WithData.args = {
  requests: pendingrequestsFixtures.threeRequests,
};
