import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import CompletedRequestsPage from "main/pages/Requests/CompletedRequestsPage";
import { http, HttpResponse } from "msw";
import React from "react";

export default {
  title: "pages/Requests/CompletedRequestsPage",
  component: CompletedRequestsPage,
};

const Template = () => <CompletedRequestsPage />;

export const Empty = Template.bind({});
Empty.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.userOnly);
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither);
    }),
    http.get("/api/recommendationrequest/professor/all", () => {
      return HttpResponse.json([]);
    }),
  ],
};

export const ThreeItemsProfessor = Template.bind({});
ThreeItemsProfessor.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.userOnly);
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither);
    }),
    http.get("/api/recommendationrequest/professor/all", () => {
      return HttpResponse.json(
        recommendationRequestFixtures.threeRecommendations
      );
    }),
  ],
};
