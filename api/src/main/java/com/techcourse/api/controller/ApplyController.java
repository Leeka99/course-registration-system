package com.techcourse.api.controller;

import com.techcourse.api.util.ApiResponse;
import com.techcourse.api.domain.dto.ShowRegistrationResponse;
import com.techcourse.api.service.lock.ApplyService;
import com.techcourse.api.service.showregistration.ShowRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;
    private final ShowRegistrationService showRegistrationService;

    @Operation(summary = "등록 목록 전체조회", description = "전체 등록 상황을 조회합니다.")
    @GetMapping("/show")
    public ResponseEntity<ApiResponse<List<ShowRegistrationResponse>>> showRegistration() {
        List<ShowRegistrationResponse> response = showRegistrationService.showRegistration();
        return ResponseEntity.ok(ApiResponse.show(response));
    }

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
