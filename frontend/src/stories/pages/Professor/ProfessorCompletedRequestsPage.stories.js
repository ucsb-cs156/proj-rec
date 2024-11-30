import React from "react";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { http, HttpResponse } from "msw";

import ProfessorCompletedRequestsPage from "main/pages/Professor/ProfessorCompletedRequestsPage";

export default {
  title: "pages/Professor/ProfessorCompletedRequestsPage",
  component: ProfessorCompletedRequestsPage,
};

const Template = () => <ProfessorCompletedRequestsPage storybook={true} />;

export const Default = Template.bind({});
Default.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.userOnly, {
        status: 200,
      });
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither, {
        status: 200,
      });
    }),
    // post requests when backend is done
  ],
};
