package com.techcourse.api.controller;

import com.techcourse.api.domain.ApiResponse;
import com.techcourse.api.service.ApplyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    @Operation(summary = "수강신청", description = "수강신청을 진행합니다.")
    @PostMapping("/apply/{studentId}/{courseId}")

    public ResponseEntity<ApiResponse<Void>> apply(
        @PathVariable Long studentId,
        @PathVariable Long courseId
    ) {
        applyService.apply(studentId, courseId);

        return ResponseEntity.ok(ApiResponse.success("수강신청이 완료되었습니다."));
    }
}
