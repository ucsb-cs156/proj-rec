import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useCurrentUser } from "main/utils/currentUser";

export default function HomePage() {
  const { data: currentUser } = useCurrentUser();

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Welcome!</h1>
        <p>
          This is a webapp that allows students to submit different types of
          requests to professors at the University of California, Santa Barbara.
          {currentUser && currentUser.loggedIn
            ? " Navigate to the requests tab to get started."
            : " Please log in to get started."}
        </p>
      </div>
    </BasicLayout>
  );
}
