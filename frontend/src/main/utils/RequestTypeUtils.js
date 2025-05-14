export const API_REQUEST_TYPES_URL = "/api/requesttypes";
export const HTTP_METHOD_DELETE = "DELETE";

export function cellToAxiosParamsDelete(cell) {
  // Input validation: This is key for robustness and killing mutants
  if (!cell || typeof cell !== 'object') {
    throw new TypeError("cellToAxiosParamsDelete: 'cell' must be an object.");
  }
  if (!cell.row || typeof cell.row !== 'object') {
    throw new TypeError("cellToAxiosParamsDelete: 'cell.row' must be an object.");
  }
  if (!cell.row.values || typeof cell.row.values !== 'object') {
    throw new TypeError("cellToAxiosParamsDelete: 'cell.row.values' must be an object.");
  }
  // Allow 0 as a valid ID, but not undefined or null
  if (cell.row.values.id === undefined || cell.row.values.id === null) {
    throw new TypeError("cellToAxiosParamsDelete: 'cell.row.values.id' is missing or invalid.");
  }

  return {
    url: API_REQUEST_TYPES_URL,
    method: HTTP_METHOD_DELETE,
    params: {
      id: cell.row.values.id,
    },
  };
}
