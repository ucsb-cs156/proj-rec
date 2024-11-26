const recommendationRequestFixtures = {
    oneRecommendationRequest: [
      {
        id: 1,
  
        requesterEmail: "ramonwang@ucsb.edu",
        requesterName: "Ramon Wang",
  
        professorId: 1,
        professorEmail: "pconrad@ucsb.edu",
        professorName: "Phil Conrad",
  
        requestType: "CS Department BS/MS program",
        details: "Extension deadline request :)",
  
        neededByDate: "2022-04-02T12:00",
        submissionDate: "2023-06-01T02:00:00",
        completionDate: null,
  
        status: "Completed",
      },
    ],
  
    threeRecommendationRequests: [
      {
        id: 2,
  
        requesterEmail: "noahwang@ucsb.edu",
        requesterName: "Noah Wang",
  
        professorId: 2,
        professorEmail: "dimirza@cs.ucsb.edu",
        professorName: "Diba Mirza",
  
        requestType: "Scholarship or Fellowship",
        details: "scholarship details",
  
        neededByDate: "2022-09-02T12:00",
        submissionDate: "2023-08-01T02:00:00",
        completionDate: null,
  
        status: "Pending",
      },
      {
        id: 3,
  
        requesterEmail: "kanav@ucsb.edu",
        requesterName: "Kanav Arora",
  
        professorId: 3,
        professorEmail: "mbeyeler@ucsb.edu",
        professorName: "Michael Beyeler",
  
        requestType: "PhD program",
        details: "PHD deadline details",
  
        neededByDate: "2022-17-02T12:00",
        submissionDate: "2023-04-01T02:00:00",
        completionDate: null,
  
        status: "Rejected",
      },
      {
        id: 4,
  
        requesterEmail: "logandetrick@ucsb.edu",
        requesterName: "Logan Detrick",
  
        professorId: 4,
        professorEmail: "ziad.matni@ucsb.edu",
        professorName: "Ziad Matni",
  
        requestType: "Other",
        details: "Regrade final exam",
  
        neededByDate: "2022-27-02T12:00",
        submissionDate: "2023-09-01T02:00:00",
        completionDate: null,
  
        status: "Pending",
      },
    ],
  };
  
  export { recommendationRequestFixtures };
  