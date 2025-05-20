import React from "react";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { http, HttpResponse } from "msw";

import StudentProfileEditPage from "main/pages/StudentProfile/StudentProfileEditPage";
import { usersFixtures } from "fixtures/usersFixtures";
import { recommendationTypeFixtures } from "fixtures/recommendationTypeFixtures";

export default {
  title: "pages/StudentProfile/StudentProfileEditPage",
  component: StudentProfileEditPage,
};

const Template = () => <StudentProfileEditPage storybook={true} />;

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

    http.get("/api/recommendationrequest", ({ request }) => {
      const url = new URL(request.url);
      const id = url.searchParams.get("id");
      if (id === "17") {
        return HttpResponse.json(
          {
            id: 17,
            professor: {
              id: 3,
              fullName: "Craig Zzyxx",
            },
            recommendationType: "Other",
            details: "Test details",
            dueDate: "2025-05-19T00:00:00",
          },
          { status: 200 },
        );
      }
      return HttpResponse.json({}, { status: 404 });
    }),

    http.put("/api/recommendationrequest", () => {
      return HttpResponse.json(
        {
          id: 17,
          professor: { id: 2, fullName: "New Professor" },
          recommendationType: "Other",
          details: "Updated details",
          dueDate: "2025-06-01T00:00:00",
        },
        { status: 200 },
      );
    }),

    http.get("/api/admin/users/professors", () => {
      return HttpResponse.json(usersFixtures.twoProfessors, { status: 200 });
    }),

    http.get("/api/requesttypes/all", () => {
      return HttpResponse.json(recommendationTypeFixtures.fourTypes, {
        status: 200,
      });
    }),
  ],
};
