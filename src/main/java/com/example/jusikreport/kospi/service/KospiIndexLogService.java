package com.example.jusikreport.kospi.service;

import com.example.jusikreport.kospi.dto.KospiIndexResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KospiIndexLogService {

    private static final Logger log = LoggerFactory.getLogger(KospiIndexLogService.class);
    private final ObjectMapper objectMapper;

    public KospiIndexLogService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KospiIndexLogService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * JSON 문자열을 DTO로 파싱하고 주요 항목을 로그로 출력합니다.
     *
     * @param jsonString 파싱할 JSON 문자열
     * @return 파싱된 ApiResponse DTO 객체
     */
    public KospiIndexResponseDto.ApiResponse parseAndLog(String jsonString) {
        log.info("================ [수신 데이터 원본] ================");
        log.info("{}", jsonString);

        try {
            KospiIndexResponseDto.ApiResponse apiResponse =
                    objectMapper.readValue(jsonString, KospiIndexResponseDto.ApiResponse.class);

            if (apiResponse == null || apiResponse.response() == null) {
                log.warn("응답 데이터(response)가 null입니다.");
                return null;
            }

            KospiIndexResponseDto.Header header = apiResponse.response().header();
            KospiIndexResponseDto.Body body = apiResponse.response().body();

            if (header != null) {
                log.info("================ [지수 정보 응답 결과] ================");
                log.info("결과 코드: {}, 결과 메시지: {}", header.resultCode(), header.resultMsg());
            }

            if (body != null) {
                log.info("페이지 번호: {}, 한 페이지 결과 수: {}, 전체 개수: {}",
                        body.pageNo(), body.numOfRows(), body.totalCount());

                if (body.items() != null && body.items().item() != null) {
                    log.info("================ [지수 항목 목록 (총 {}건)] ================", body.items().item().size());

                    for (KospiIndexResponseDto.KospiIndexItem item : body.items().item()) {
                        log.info("[{}] 지수명: {}, 분류: {}, 종가: {}, 대비: {}, 등락률: {}%, 시가: {}, 고가: {}, 저가: {}, 거래량: {}, 거래대금: {}",
                                item.basDt(),
                                item.idxNm(),
                                item.idxCsf(),
                                item.clpr(),
                                item.vs(),
                                item.fltRt(),
                                item.mkp(),
                                item.hipr(),
                                item.lopr(),
                                item.trqu(),
                                item.trPrc());
                    }
                }
            }

            return apiResponse;
        } catch (Exception e) {
            log.error("JSON 파싱 및 로그 출력 중 오류 발생: {}", e.getMessage(), e);
            throw new IllegalArgumentException("올바르지 않은 JSON 데이터입니다.", e);
        }
    }
}
