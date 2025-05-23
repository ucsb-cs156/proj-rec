// frontend/src/utils/RecommendationRequestUtils.js
import { toast } from "react-toastify";

export function onDeleteSuccess(message) {
  console.log(message);
  toast(message);
}

/**
 * Build axios params for DELETE.
 * @param {object} cell     – table-cell object
 * @param {boolean} isAdmin – if true, hits the admin endpoint
 */
export function cellToAxiosParamsDelete(cell, isAdmin = false) {
  return {
    url: isAdmin
      ? "/api/recommendationrequest/admin"
      : "/api/recommendationrequest",
    method: "DELETE",
    params: { id: cell.row.values.id },
  };
}

export function onUpdateStatusSuccess(message) {
  console.log(message);
  toast(message);
}

export function cellToAxiosParamsUpdateStatus(cell, newStatus) {
  return {
    url: "/api/recommendationrequest/professor",
    method: "PUT",
    params: { id: cell.row.values.id },
    data: { status: newStatus },
  };
}
