import {
  onDeleteSuccess,
  cellToAxiosParamsDelete,
  formattedDate,
} from "main/utils/RecommendationRequestUtils";
import mockConsole from "jest-mock-console";

const mockToast = jest.fn();
jest.mock("react-toastify", () => {
  const originalModule = jest.requireActual("react-toastify");
  return {
    __esModule: true,
    ...originalModule,
    toast: (x) => mockToast(x),
  };
});

describe("RecommendationRequestUtils", () => {
  describe("onDeleteSuccess", () => {
    test("It puts the message on console.log and in a toast", () => {
      // arrange
      const restoreConsole = mockConsole();

      // act
      onDeleteSuccess("abc");
      // asserts
      expect(mockToast).toHaveBeenCalledWith("abc");
      expect(console.log).toHaveBeenCalled();
      const message = console.log.mock.calls[0][0];
      expect(message).toMatch("abc");

      restoreConsole();
    });
  });
  describe("cellToAxiosParamsDelete", () => {
    test("It returns the correct params", () => {
      // arrange
      const cell = { row: { values: { id: 17 } } };

      // act
      const result = cellToAxiosParamsDelete(cell);

      // assert
      expect(result).toEqual({
        url: "/api/recommendationrequest",
        method: "DELETE",
        params: { id: 17 },
      });
    });
  });
  describe("formattedDate", () => {
    test("It returns the correct date", () => {
      // arrange
      const val = "2023-10-01T12:34:56";

      // act
      const result = formattedDate(val);

      // assert
      expect(result).toEqual("10/01/2023 12:34");
    });
  });
});
