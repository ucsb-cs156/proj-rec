import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import RecommendationRequestForm from "main/components/RecommendationRequest/RecommendationRequestForm";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";

const EditRecommendationRequestPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [initialData, setInitialData] = useState(null);

  useEffect(() => {
    // Fetch the request by id
    fetch(`/api/recommendationrequest?id=${id}`)
      .then((res) => res.json())
      .then((data) => setInitialData(data));
  }, [id]);

  const handleSubmit = async (formData) => {
    // PUT to backend
    await fetch(`/api/recommendationrequest?id=${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formData),
    });
    navigate("/profile");
  };

  if (!initialData) return <div>Loading...</div>;

  return (
    <BasicLayout>
      <h2>Edit Recommendation Request</h2>
      <RecommendationRequestForm mode="edit" initialData={initialData} onSubmit={handleSubmit} disableStatus={true} />
    </BasicLayout>
  );
};

export default EditRecommendationRequestPage; 