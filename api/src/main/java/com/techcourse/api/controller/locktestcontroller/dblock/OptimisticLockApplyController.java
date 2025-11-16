package com.techcourse.api.controller.locktestcontroller.dblock;

import com.techcourse.api.service.lock.dblock.ApplyServiceOptimisticLock;
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
@Tag(name = "(OptimisticLock 적용) 수강신청", description = "OptimisticLock 적용 컨트롤러")
public class OptimisticLockApplyController {

    private final ApplyServiceOptimisticLock applyServiceOptimisticLock;

    @Operation(description = "(성능 테스트용) OptimisticLock를 사용한 수강신청")
    @PostMapping("/apply/optimisticlock/{studentId}/{courseId}")
    public ResponseEntity<ApiResponse<Void>> apply(
        @PathVariable Long studentId,
        @PathVariable Long courseId
    ) {
        applyServiceOptimisticLock.apply(studentId, courseId);

        return ResponseEntity.ok(ApiResponse.success("수강신청이 완료되었습니다."));
    }
}
