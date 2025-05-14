export function cellToAxiosParamsDelete(cell) {
  if (
    !cell ||
    !cell.row ||
    !cell.row.values ||
    typeof cell.row.values.id === "undefined"
  ) {
    throw new Error("Invalid cell object for cellToAxiosParamsDelete");
  }
  return {
    url: "/api/requesttypes",
    method: "DELETE",
    params: {
      id: cell.row.values.id,
    },
  };
}
