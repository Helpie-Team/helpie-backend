package com.helpie.backend.domain.survey;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 도시 선택 Enum (계층적 구조: 국가 > 도시)
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum Country {
    // 즐겨찾는 도시 5개
    SEOUL("서울"),
    TOKYO("도쿄"), 
    SHANGHAI("상하이"),
    LOS_ANGELES("로스앤젤레스"),
    LONDON("런던"),
    
    // 그 외 도시들
    // 미국
    USA_LOS_ANGELES("미국 - 로스앤젤레스"),
    USA_NEW_YORK("미국 - 뉴욕"),
    USA_BOSTON("미국 - 보스턴"),
    USA_SAN_FRANCISCO("미국 - 샌프란시스코"),
    USA_CHICAGO("미국 - 시카고"),
    USA_WASHINGTON("미국 - 워싱턴"),
    USA_AUSTIN("미국 - 어스틴"),
    
    // 한국
    KOREA_SEOUL("한국 - 서울"),
    KOREA_BUSAN("한국 - 부산"),
    KOREA_JEJU("한국 - 제주"),
    KOREA_INCHEON("한국 - 인천"),
    KOREA_GANGNEUNG("한국 - 강릉"),
    KOREA_GWANGJU("한국 - 광주"),
    
    // 중국
    CHINA_SHANGHAI("중국 - 상하이"),
    CHINA_BEIJING("중국 - 베이징"),
    CHINA_HANGZHOU("중국 - 항저우"),
    CHINA_HONGKONG("중국 - 홍콩"),
    
    // 싱가포르
    SINGAPORE("싱가포르"),
    
    // 일본
    JAPAN_TOKYO("일본 - 도쿄"),
    JAPAN_OSAKA("일본 - 오사카"),
    JAPAN_KYOTO("일본 - 교토"),
    
    // 영국
    UK_LONDON("영국 - 런던"),
    UK_MANCHESTER("영국 - 멘체스터"),
    UK_OXFORD("영국 - 옥스퍼드"),
    UK_CAMBRIDGE("영국 - 캠브리지"),
    
    // 호주
    AUSTRALIA_SYDNEY("호주 - 시드니"),
    AUSTRALIA_MELBOURNE("호주 - 멜버른"),
    AUSTRALIA_BRISBANE("호주 - 브리즈번"),
    AUSTRALIA_PERTH("호주 - 펄스"),
    
    // 캐나다
    CANADA_TORONTO("캐나다 - 토론토"),
    CANADA_VANCOUVER("캐나다 - 벤쿠버"),
    CANADA_MONTREAL("캐나다 - 몬트리올"),
    
    // 독일
    GERMANY_BERLIN("독일 - 베를린"),
    GERMANY_MUNICH("독일 - 뮈헨"),
    GERMANY_HEIDELBERG("독일 - 하이델베르크"),
    GERMANY_HAMBURG("독일 - 함부르크"),
    
    // 프랑스
    FRANCE_PARIS("프랑스 - 파리"),
    FRANCE_LYON("프랑스 - 리옹"),
    
    // 이탈리아
    ITALY_ROME("이탈리아 - 로마"),
    ITALY_MILAN("이탈리아 - 밀라노"),
    ITALY_BOLOGNA("이탈리아 - 볼로냐"),
    
    // 네덜란드
    NETHERLANDS_AMSTERDAM("네덜란드 - 암스테르담"),
    NETHERLANDS_ROTTERDAM("네덜란드 - 로테르담"),
    
    // 스페인
    SPAIN_BARCELONA("스페인 - 바르셀로나"),
    SPAIN_MADRID("스페인 - 마드리드"),
    SPAIN_VALENCIA("스페인 - 발렌시아"),
    
    // 노르웨이
    NORWAY_OSLO("노르웨이 - 오슬로"),
    NORWAY_TRONDHEIM("노르웨이 - 트론드하임"),
    
    // 스위스
    SWITZERLAND_ZURICH("스위스 - 취리히"),
    
    // 아랍에미리트
    UAE_DUBAI("아랍에미리트 - 두바이");

    private final String description;

    Country(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    /**
     * 즐겨찾는 도시 5개를 반환합니다.
     */
    public static List<Country> getFavoriteCities() {
        return List.of(SEOUL, TOKYO, SHANGHAI, LOS_ANGELES, LONDON);
    }
    
    /**
     * 국가별로 그룹핑된 그 외 도시들을 반환합니다.
     */
    public static Map<String, List<Country>> getOtherCitiesByCountry() {
        Map<String, List<Country>> grouped = new LinkedHashMap<>();
        
        // 미국
        grouped.put("미국", List.of(
            LOS_ANGELES, USA_LOS_ANGELES, USA_NEW_YORK, USA_BOSTON, USA_SAN_FRANCISCO, 
            USA_CHICAGO, USA_WASHINGTON, USA_AUSTIN
        ));
        
        // 한국  
        grouped.put("한국", List.of(
            SEOUL, KOREA_SEOUL, KOREA_BUSAN, KOREA_JEJU, 
            KOREA_INCHEON, KOREA_GANGNEUNG, KOREA_GWANGJU
        ));
        
        // 중국
        grouped.put("중국", List.of(
            SHANGHAI, CHINA_SHANGHAI, CHINA_BEIJING, CHINA_HANGZHOU, CHINA_HONGKONG
        ));
        
        // 싱가포르
        grouped.put("싱가포르", List.of(SINGAPORE));
        
        // 일본
        grouped.put("일본", List.of(
            TOKYO, JAPAN_TOKYO, JAPAN_OSAKA, JAPAN_KYOTO
        ));
        
        // 영국
        grouped.put("영국", List.of(
            LONDON, UK_LONDON, UK_MANCHESTER, UK_OXFORD, UK_CAMBRIDGE
        ));
        
        // 호주
        grouped.put("호주", List.of(
            AUSTRALIA_SYDNEY, AUSTRALIA_MELBOURNE, 
            AUSTRALIA_BRISBANE, AUSTRALIA_PERTH
        ));
        
        // 캐나다
        grouped.put("캐나다", List.of(
            CANADA_TORONTO, CANADA_VANCOUVER, CANADA_MONTREAL
        ));
        
        // 독일
        grouped.put("독일", List.of(
            GERMANY_BERLIN, GERMANY_MUNICH, 
            GERMANY_HEIDELBERG, GERMANY_HAMBURG
        ));
        
        // 프랑스
        grouped.put("프랑스", List.of(
            FRANCE_PARIS, FRANCE_LYON
        ));
        
        // 이탈리아
        grouped.put("이탈리아", List.of(
            ITALY_ROME, ITALY_MILAN, ITALY_BOLOGNA
        ));
        
        // 네덜란드
        grouped.put("네덜란드", List.of(
            NETHERLANDS_AMSTERDAM, NETHERLANDS_ROTTERDAM
        ));
        
        // 스페인
        grouped.put("스페인", List.of(
            SPAIN_BARCELONA, SPAIN_MADRID, SPAIN_VALENCIA
        ));
        
        // 노르웨이
        grouped.put("노르웨이", List.of(
            NORWAY_OSLO, NORWAY_TRONDHEIM
        ));
        
        // 스위스
        grouped.put("스위스", List.of(SWITZERLAND_ZURICH));
        
        // 아랍에미리트
        grouped.put("아랍에미리트", List.of(UAE_DUBAI));
        
        return grouped;
    }
    
    /**
     * 즐겨찾는 도시인지 확인합니다.
     */
    public boolean isFavorite() {
        return getFavoriteCities().contains(this);
    }
}