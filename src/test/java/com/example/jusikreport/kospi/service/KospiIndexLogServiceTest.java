package com.example.jusikreport.kospi.service;

import com.example.jusikreport.kospi.dto.KospiIndexResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KospiIndexLogServiceTest {

    private final KospiIndexLogService logService = new KospiIndexLogService();

    @Test
    @DisplayName("Kospi 지수 JSON 파싱 및 로그 출력 테스트")
    void testParseAndLog() {
        String jsonStr = """
        {
          "response": {
            "header": {
              "resultCode": "00",
              "resultMsg": "NORMAL SERVICE."
            },
            "body": {
              "numOfRows": 10,
              "pageNo": 1,
              "totalCount": 248289,
              "items": {
                "item": [
                  {
                    "basDt": "20260723",
                    "idxNm": "IT 서비스",
                    "idxCsf": "KOSDAQ시리즈",
                    "epyItmsCnt": "227",
                    "clpr": "623.63",
                    "vs": "30.94",
                    "fltRt": "5.22",
                    "mkp": "599.92",
                    "hipr": "623.63",
                    "lopr": "598.99",
                    "trqu": "79029990",
                    "trPrc": "442791452022",
                    "lstgMrktTotAmt": "28443558404720",
                    "lsYrEdVsFltRg": "-264",
                    "lsYrEdVsFltRt": "-29.71",
                    "yrWRcrdHgst": "1045.53",
                    "yrWRcrdHgstDt": "20260129",
                    "yrWRcrdLwst": "0",
                    "yrWRcrdLwstDt": "20260724",
                    "basPntm": "20240701",
                    "basIdx": "1000"
                  }
                ]
              }
            }
          }
        }
        """;

        KospiIndexResponseDto.ApiResponse response = logService.parseAndLog(jsonStr);

        assertNotNull(response);
        assertNotNull(response.response());
        assertEquals("00", response.response().header().resultCode());
        assertEquals(1, response.response().body().items().item().size());

        KospiIndexResponseDto.KospiIndexItem item = response.response().body().items().item().get(0);
        assertEquals("20260723", item.basDt());
        assertEquals("IT 서비스", item.idxNm());
        assertEquals("623.63", item.clpr());
    }
}
