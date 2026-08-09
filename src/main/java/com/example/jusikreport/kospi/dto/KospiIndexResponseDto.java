package com.example.jusikreport.kospi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

public class KospiIndexResponseDto {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiResponse(Response response) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Header header, Body body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
        String resultCode,
        String resultMsg
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
        int numOfRows,
        int pageNo,
        int totalCount,
        Items items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
        List<KospiIndexItem> item
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KospiIndexItem(
        String basDt,          // 기준일자
        String idxNm,          // 지수명
        String idxCsf,         // 지수분류
        String epyItmsCnt,     // 채용종목수
        String clpr,           // 종가
        String vs,             // 대비
        String fltRt,          // 등락률
        String mkp,            // 시가
        String hipr,           // 고가
        String lopr,           // 저가
        String trqu,           // 거래량
        String trPrc,          // 거래대금
        String lstgMrktTotAmt, // 상장시가총액
        String lsYrEdVsFltRg,  // 전년말대비등락폭
        String lsYrEdVsFltRt,  // 전년말대비등락율
        String yrWRcrdHgst,    // 연중최고가
        String yrWRcrdHgstDt,  // 연중최고가일자
        String yrWRcrdLwst,    // 연중최저가
        String yrWRcrdLwstDt,  // 연중최저가일자
        String basPntm,        // 기준시점
        String basIdx          // 기준지수
    ) {}
}
