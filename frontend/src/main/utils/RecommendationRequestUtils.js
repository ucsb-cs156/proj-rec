// frontend/src/utils/RecommendationRequestUtils.js
import { toast } from "react-toastify";

export function onDeleteSuccess(message) {
  console.log(message);
  toast(message);
}

export function cellToAxiosParamsDeleteAdmin(cell) {
  return {
    url: "/api/recommendationrequest/admin",
    method: "DELETE",
    params: {
      id: cell.row.values.id,
    },
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
