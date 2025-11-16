package com.techcourse.api.controller.locktestcontroller.javalock;

import com.techcourse.api.service.lock.javalock.ApplyServiceReentrantLock;
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
@Tag(name = "(ReentrantLock 적용) 수강신청", description = "ReentrantLock 적용 컨트롤러")
public class ReentrantLockApplyController {

    private final ApplyServiceReentrantLock applyServiceReentrantLock;

    @Operation(description = "(성능 테스트용) ReentrantLock를 사용한 수강신청")
    @PostMapping("/apply/reentrantlock/{studentId}/{courseId}")
    public ResponseEntity<ApiResponse<Void>> apply(
        @PathVariable Long studentId,
        @PathVariable Long courseId
    ) {
        applyServiceReentrantLock.apply(studentId, courseId);

        return ResponseEntity.ok(ApiResponse.success("수강신청이 완료되었습니다."));
    }
}
