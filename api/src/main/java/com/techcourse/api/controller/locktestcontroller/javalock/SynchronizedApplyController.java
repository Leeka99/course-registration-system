package com.techcourse.api.controller.locktestcontroller.javalock;

import com.techcourse.api.service.lock.javalock.ApplyServiceSynchronized;
import com.techcourse.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "(Synchronized 키워드) 수강신청", description = "Synchronized 키워드 적용 컨트롤러")
public class SynchronizedApplyController {

    private final ApplyServiceSynchronized applyServiceSynchronized;

    @Operation(description = "(성능 테스트용) Synchronized 키워드를 사용한 수강신청")
    @PostMapping("/apply/synchronized/{studentId}/{courseId}")
    public ResponseEntity<ApiResponse<Void>> apply(
        @PathVariable Long studentId,
        @PathVariable Long courseId
    ) {
        applyServiceSynchronized.apply(studentId, courseId);

        return ResponseEntity.ok(ApiResponse.success("수강신청이 완료되었습니다."));
    }
}