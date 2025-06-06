import "../src/index.css";
import "bootstrap/dist/css/bootstrap.css";
import 'react-toastify/dist/ReactToastify.css';

import { initialize, mswLoader } from 'msw-storybook-addon'

import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { ToastContainer } from "react-toastify";

const currentUrl = window.location.href;
const isLocalhost = currentUrl.startsWith("http://localhost:6006/");
const mockServiceWorkerUrl = isLocalhost ? "mockServiceWorker.js" : "https://" + window.location.hostname + "/mockServiceWorker.js";

// Initialize MSW

initialize(
  {
    serviceWorker: {
      url: mockServiceWorkerUrl
    }
  }
);


// Per https://storybook.js.org/docs/react/writing-stories/decorators#context-for-mocking
// Here, we provide the context needed for some of the components,
// e.g. the ones that rely on currentUser

const queryClient = new QueryClient();

export const decorators = [
  (Story) => (
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <ToastContainer />
        <Story />
      </MemoryRouter>
    </QueryClientProvider>
  )
];

/** @type { import('@storybook/react').Preview } */
const preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },

  loaders: [mswLoader],
  tags: ["autodocs"]
};

export default preview;