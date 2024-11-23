const recommendationRequestFixtures = {
    oneRecommendationRequest: {
      id: 1,
      professorName: "Name",
      professorEmail: "Email",
      requesterName: "Name",
      recommendationTypes: "Type",
      details: "tbd",
      status: "completed",
      submissionDate: "2023-01-02",
      completionDate: "2022-01-02",
    },
    threeRecommendationRequests: [
      {
        id: 1,
        professorName: "Name",
        professorEmail: "Email",
        requesterName: "Name",
        recommendationTypes: "Type",
        details: "tbd",
        status: "completed",
        submissionDate: "2023-01-02",
        completionDate: "2022-01-02",
      },
      {
        id: 2,
        professorName: "Name2",
        professorEmail: "Email2",
        requesterName: "Name2",
        recommendationTypes: "Type2",
        details: "tbd2",
        status: "completed2",
        submissionDate: "2024-01-02",
        completionDate: "2021-01-02",
      },
      {
        id: 3,
        professorName: "Name3",
        professorEmail: "Email3",
        requesterName: "Name3",
        recommendationTypes: "Type3",
        details: "tbd3",
        status: "completed3",
        submissionDate: "2025-01-02",
        completionDate: "2020-01-02",
      },
    ],
  };
  
  export { recommendationRequestFixtures };
  