package com.techcourse.api.util;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 응답 DTO", name = "ApiResponse")
public record ApiResponse<T>(
    @Schema(description = "서비스 정의 코드", example = "2000")
    String code,
    @Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
    String message,
    @Schema(description = "응답 데이터")
    T data
) {

    public static <T> ApiResponse<T> show(T data) {
        return new ApiResponse<>(ResponseCode.OK.code(), ResponseCode.OK.message(), data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ResponseCode.OK.code(), message, null);
    }
}
