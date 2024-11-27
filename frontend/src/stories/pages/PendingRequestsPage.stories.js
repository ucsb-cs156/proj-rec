import React from "react";
import PendingRequestsPage from "main/pages/PendingRequestsPage";
import { QueryClient, QueryClientProvider } from "react-query";
import { pendingrequestsFixtures } from "fixtures/pendingrequestsFixtures";

const queryClient = new QueryClient();

export default {
  title: "pages/PendingRequestsPage",
  component: PendingRequestsPage,
};

const Template = () => (
  <QueryClientProvider client={queryClient}>
    <PendingRequestsPage />
  </QueryClientProvider>
);

export const Default = Template.bind({});
Default.args = {
  pendingRequests: pendingrequestsFixtures.threeRequests,
};
