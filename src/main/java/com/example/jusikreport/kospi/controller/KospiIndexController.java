package com.example.jusikreport.kospi.controller;

import com.example.jusikreport.kospi.dto.KospiIndexResponseDto;
import com.example.jusikreport.kospi.service.KospiIndexLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kospi")
public class KospiIndexController {

    private static final Logger log = LoggerFactory.getLogger(KospiIndexController.class);
    private final KospiIndexLogService kospiIndexLogService;

    public KospiIndexController(KospiIndexLogService kospiIndexLogService) {
        this.kospiIndexLogService = kospiIndexLogService;
    }

    /**
     * 외부 시스템에서 JSON을 HTTP POST 방식으로 송신할 때 받아주는 REST 엔드포인트입니다.
     *
     * URL: POST http://localhost:8080/api/kospi/index
     * Content-Type: application/json
     *
     * @param jsonPayload 외부에서 전송한 JSON 문자열
     * @return 수신 성공 응답 메시지
     */
    @PostMapping("/index")
    public ResponseEntity<String> receiveKospiIndexJson(@RequestBody String jsonPayload) {
        log.info("외부 시스템으로부터 KOSPI 지수 JSON 데이터를 수신했습니다.");

        // JSON 파싱 및 로그 출력 실행
        kospiIndexLogService.parseAndLog(jsonPayload);

        return ResponseEntity.ok("성공적으로 JSON을 수신하고 로그에 기록했습니다.");
    }

    /**
     * Spring이 JSON을 자동으로 DTO로 변환하여 받아주는 엔드포인트 (선택적으로 사용 가능)
     *
     * URL: POST http://localhost:8080/api/kospi/index-dto
     * Content-Type: application/json
     */
    @PostMapping("/index-dto")
    public ResponseEntity<KospiIndexResponseDto.ApiResponse> receiveKospiIndexDto(
            @RequestBody KospiIndexResponseDto.ApiResponse apiResponse) {
        log.info("DTO 형태로 KOSPI 지수 데이터를 수신했습니다.");

        if (apiResponse != null && apiResponse.response() != null && apiResponse.response().header() != null) {
            log.info("결과 코드: {}, 메시지: {}",
                    apiResponse.response().header().resultCode(),
                    apiResponse.response().header().resultMsg());
        }

        return ResponseEntity.ok(apiResponse);
    }
}
