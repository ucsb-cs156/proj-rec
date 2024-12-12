import { toast } from "react-toastify";

/**
 * Function to handle success message after deleting a request type.
 * @param {string} message - The success message to display.
 */
export function onDeleteSuccess(message) {
  console.log(message);
  toast(message);
}

/**
 * Function to generate Axios parameters for deleting a request type.
 * @param {object} cell - The table cell containing the request type data.
 * @returns {object} Axios parameters for the DELETE request.
 */
export function cellToAxiosParamsDelete(cell) {
  return {
    url: "/api/request-types",
    method: "DELETE",
    params: {
      id: cell.row.values.id,
    },
  };
}
