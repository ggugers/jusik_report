package com.example.jusikreport.kospi.controller;

import com.example.jusikreport.kospi.service.KospiIndexLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KospiIndexController.class)
class KospiIndexControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private KospiIndexLogService kospiIndexLogService;

  @Test
  @DisplayName("외부 HTTP POST JSON 수신 컨트롤러 테스트")
  void testReceiveKospiIndexJson() throws Exception {
    String jsonPayload = """
        {
          "response": {
            "header": {
              "resultCode": "00",
              "resultMsg": "NORMAL SERVICE."
            },
            "body": {
              "numOfRows": 10,
              "pageNo": 1,
              "totalCount": 1,
              "items": {
                "item": [
                  {
                    "basDt": "20260723",
                    "idxNm": "IT 서비스",
                    "clpr": "623.63"
                  }
                ]
              }
            }
          }
        }
        """;

    mockMvc.perform(post("/api/kospi/index")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonPayload))
        .andExpect(status().isOk());

    verify(kospiIndexLogService).parseAndLog(anyString());
  }
}
