import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import RecommendationRequestForm from "main/components/RecommendationRequest/RecommendationRequestForm";
import { Navigate } from "react-router-dom";
import { useBackend, useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function StudentProfileEditPage({ storybook = false }) {
  let { id } = useParams();

  const {
    data: recommendationrequests,
    _error,
    _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    [`/api/recommendationrequest?id=${id}`],
    {
      // Stryker disable next-line all : GET is the default, so mutating this to "" doesn't introduce a bug
      method: "GET",
      url: `/api/recommendationrequest`,
      params: {
        id,
      },
    },
  );

  const objectToAxiosPutParams = (recommendationrequest) => ({
    url: "/api/recommendationrequest",
    method: "PUT",
    params: {
      id: recommendationrequest.id,
    },
    data: {
      professorId: recommendationrequest.professor_id,
      recommendationType: recommendationrequest.recommendationType,
      details: recommendationrequest.details,
      dueDate: recommendationrequest.dueDate,
    },
  });

  const onSuccess = (recommendationrequest) => {
    toast(`Recommendation Request Updated - id: ${recommendationrequest.id}`);
  };

  const mutation = useBackendMutation(
    objectToAxiosPutParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    [`/api/recommendationrequest?id=${id}`],
  );

  const { isSuccess } = mutation;

  const onSubmit = async (data) => {
    mutation.mutate(data);
  };

  if (isSuccess && !storybook) {
    return <Navigate to="/requests" />;
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Edit Recommendation Request</h1>
        {recommendationrequests && (
          <RecommendationRequestForm
            submitAction={onSubmit}
            buttonLabel={"Update"}
            initialContents={{
              ...recommendationrequests,
              professor_id: recommendationrequests.professor.id,
              dueDate: recommendationrequests.dueDate.substring(0, 10),
            }}
          />
        )}
      </div>
    </BasicLayout>
  );
}
