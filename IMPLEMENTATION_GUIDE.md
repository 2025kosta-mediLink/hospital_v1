# 🗺️ 실제 지도 API + 공공데이터 연동 구현 가이드

## 📋 구현 순서

### 1단계: API 키 발급 🔑

#### **카카오맵 API 키 발급**
1. [카카오 개발자 콘솔](https://developers.kakao.com/) 접속
2. 애플리케이션 등록
3. 플랫폼 설정 → Web 플랫폼 추가
4. JavaScript 키 복사

#### **공공데이터포털 API 키 발급**
1. [공공데이터포털](https://data.go.kr/) 회원가입
2. "약국정보" 검색
3. "응급의료기관 조회서비스" 또는 "약국정보 조회서비스" 신청
4. 승인 후 API 키 발급

### 2단계: 설정 파일 업데이트 ⚙️

**`src/main/webapp/static/js/config/api-config.js`** 파일에서 실제 API 키로 교체:

```javascript
const API_CONFIG = {
    // 실제 발급받은 키로 교체
    KAKAO_MAP_KEY: 'YOUR_ACTUAL_KAKAO_MAP_API_KEY',
    PUBLIC_DATA_KEY: 'YOUR_ACTUAL_PUBLIC_DATA_API_KEY',
    // ... 나머지 설정
};
```

### 3단계: 백엔드 서비스 구현 🔧

**`src/main/service/PharmacyService.java`**에 공공데이터 API 연동 메서드 추가:

```java
// 공공데이터 API 호출 메서드들
public List<PharmacyListItemDTO> getPharmacyListFromPublicData(int pageNo, int numOfRows)
public PharmacyListItemDTO getPharmacyDetailFromPublicData(String pharmacyId)
public List<PharmacyListItemDTO> searchNearbyPharmaciesFromPublicData(double lat, double lng, int radius)
```

### 4단계: 프론트엔드 연동 🎨

**약국 검색 페이지**에서 실제 API 호출:

```javascript
// 실제 지도 초기화
await mapService.initMap('map');

// 공공데이터에서 약국 정보 가져오기
const pharmacies = await pharmacyApiService.searchPharmaciesByLocation(lat, lng, radius);

// 지도에 마커 표시
mapService.addPharmacyMarkers(pharmacies);
```

## 🚀 배포 및 테스트

### 1. API 키 설정
```bash
# 설정 파일에서 API 키 업데이트
vim src/main/webapp/static/js/config/api-config.js
```

### 2. 백엔드 컴파일 및 배포
```bash
# Java 클래스 컴파일
javac -cp "lib/*" src/main/controller/PharmacyController.java
javac -cp "lib/*" src/main/service/PharmacyService.java

# Tomcat에 배포
cp -r src/main/webapp/* /usr/local/tomcat/webapps/ROOT/
```

### 3. 테스트
1. 브라우저에서 약국 검색 페이지 접속
2. 개발자 도구 → Network 탭에서 API 호출 확인
3. 지도에 실제 약국 마커 표시 확인

## 🔧 주요 기능

### ✅ 구현된 기능
- [x] 카카오맵 API 연동
- [x] 공공데이터 API 연동
- [x] 위치 기반 약국 검색
- [x] 지도 마커 표시
- [x] 약국 상세 정보 모달
- [x] 실시간 영업 상태 표시

### 🚧 추가 구현 필요
- [ ] 공공데이터 API 실제 연동
- [ ] 약국 영업시간 정확한 파싱
- [ ] 거리 계산 및 정렬
- [ ] 에러 처리 및 폴백 로직

## 📱 사용자 경험 개선

### 현재 상태
- ✅ 기본 지도 표시
- ✅ 약국 마커 표시
- ✅ 약국 리스트 스크롤
- ✅ 모달 상세 정보

### 향후 개선
- 🔄 실시간 약국 영업 상태
- 🔄 정확한 거리 계산
- 🔄 약국 리뷰 및 평점
- 🔄 길찾기 기능

## 🐛 문제 해결

### 자주 발생하는 문제들

1. **API 키 오류**
   ```
   해결: api-config.js에서 올바른 키 설정 확인
   ```

2. **CORS 오류**
   ```
   해결: 백엔드에서 CORS 헤더 추가
   ```

3. **지도 로딩 실패**
   ```
   해결: 카카오맵 API 키 및 도메인 설정 확인
   ```

## 📞 지원

구현 과정에서 문제가 발생하면:
1. 브라우저 개발자 도구 → Console 탭 확인
2. Network 탭에서 API 호출 상태 확인
3. 서버 로그 확인

---

**다음 단계**: 실제 API 키 발급 후 테스트 진행! 🚀
