// API 설정 파일
const API_CONFIG = {
    // 카카오맵 API 키
    KAKAO_MAP_KEY: '371a027cd1dac68dce2424d2ac0fd3ca',
    
    // 공공데이터 API 키 (공공데이터포털에서 발급받은 키로 교체)
    PUBLIC_DATA_KEY: 'YOUR_PUBLIC_DATA_API_KEY',
    
    // 카카오 REST API 키 (약국 검색용)
    KAKAO_REST_API_KEY: 'af11d187f48df0ed2268e7e2afbbfc45',
    
    // API 엔드포인트
    ENDPOINTS: {
        // 공공데이터 약국 정보 API
        PHARMACY_INFO: 'http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire',
        
        // 카카오맵 API
        KAKAO_MAP: 'https://dapi.kakao.com/v2/maps/sdk.js'
    },
    
    // 기본 설정
    DEFAULT_LOCATION: {
        latitude: 37.5665,  // 서울시청 위도
        longitude: 126.9780 // 서울시청 경도
    },
    
    // 검색 반경 (미터)
    SEARCH_RADIUS: 2000
};

// 전역에서 사용할 수 있도록 설정
window.API_CONFIG = API_CONFIG;
