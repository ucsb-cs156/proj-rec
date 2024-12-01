import React from "react";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { recommendationRequestFixtures } from "fixtures/recommendationRequestFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import CompletedRequestsPage from "main/pages/Requests/CompletedRequestsPage";
import { http, HttpResponse } from "msw";

export default {
  title: "pages/Requests/CompletedRequestsPage",
  component: CompletedRequestsPage,
};

const Template = () => <CompletedRequestsPage />;

export const Empty = Template.bind({});
Empty.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.professorUser);
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither);
    }),
    http.get("/api/recommendationrequest/professor/all", () => {
      return HttpResponse.json([]);
    }),
  ],
};

export const CompletedAndDeniedRequests = Template.bind({});
CompletedAndDeniedRequests.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.professorUser);
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither);
    }),
    http.get("/api/recommendationrequest/professor/all", () => {
      return HttpResponse.json(recommendationRequestFixtures.mixedRequests);
    }),
  ],
};

export const OnlyCompletedRequests = Template.bind({});
OnlyCompletedRequests.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.professorUser);
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither);
    }),
    http.get("/api/recommendationrequest/professor/all", () => {
      return HttpResponse.json([
        recommendationRequestFixtures.mixedRequests[0],
      ]);
    }),
  ],
};

export const OnlyDeniedRequests = Template.bind({});
OnlyDeniedRequests.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.professorUser);
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither);
    }),
    http.get("/api/recommendationrequest/professor/all", () => {
      return HttpResponse.json([
        recommendationRequestFixtures.mixedRequests[1],
      ]);
    }),
  ],
};
