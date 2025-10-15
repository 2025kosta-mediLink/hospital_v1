// 공공데이터 API 서비스
class PublicDataService {
    constructor() {
        this.baseUrl = 'http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService';
        this.apiKey = API_CONFIG.PUBLIC_DATA_KEY;
    }

    // 약국 목록 조회 (공공데이터 API)
    async getPharmacyList(params = {}) {
        try {
            const queryParams = new URLSearchParams({
                serviceKey: this.apiKey,
                pageNo: params.pageNo || 1,
                numOfRows: params.numOfRows || 20,
                sidoCd: params.sidoCd || '', // 시도 코드
                sgguCd: params.sgguCd || '', // 시군구 코드
                emdongNm: params.emdongNm || '', // 읍면동명
                dutyName: params.dutyName || '', // 약국명
                ...params
            });

            console.log('공공데이터 API 호출:', this.baseUrl + '/getParmacyListInfoInqire?' + queryParams.toString());

            const response = await fetch(this.baseUrl + '/getParmacyListInfoInqire?' + queryParams.toString(), {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('공공데이터 응답:', data);
            
            return this.transformPharmacyData(data);
        } catch (error) {
            console.error('공공데이터 API 호출 실패:', error);
            throw error;
        }
    }

    // 공공데이터 응답을 프론트엔드 형식으로 변환
    transformPharmacyData(apiResponse) {
        if (!apiResponse || !apiResponse.response || !apiResponse.response.body || !apiResponse.response.body.items) {
            console.warn('공공데이터 응답 구조가 예상과 다릅니다:', apiResponse);
            return [];
        }

        const items = apiResponse.response.body.items.item;
        if (!Array.isArray(items)) {
            return [this.transformSinglePharmacy(items)];
        }

        return items.map(item => this.transformSinglePharmacy(item));
    }

    // 단일 약국 데이터 변환
    transformSinglePharmacy(item) {
        return {
            pharmacyId: item.hpid || item.dutyNo,
            name: item.dutyName || '약국명 없음',
            address: item.dutyAddr || '주소 없음',
            phone: item.dutyTel1 || '전화번호 없음',
            latitude: parseFloat(item.wgs84Lat) || parseFloat(item.lat) || 0,
            longitude: parseFloat(item.wgs84Lon) || parseFloat(item.lng) || 0,
            isOpen: this.checkIsOpen(item),
            operatingHours: this.formatOperatingHours(item),
            distance: null // 거리는 별도 계산 필요
        };
    }

    // 약국 영업시간 확인
    checkIsOpen(pharmacy) {
        const now = new Date();
        const currentTime = now.getHours() * 100 + now.getMinutes();
        const currentDay = now.getDay(); // 0=일요일, 1=월요일, ..., 6=토요일

        // 평일 영업시간 체크
        if (currentDay >= 1 && currentDay <= 5) { // 월~금
            if (pharmacy.dutyTime1s && pharmacy.dutyTime1c) {
                const openTime = parseInt(pharmacy.dutyTime1s.replace(':', ''));
                const closeTime = parseInt(pharmacy.dutyTime1c.replace(':', ''));
                return currentTime >= openTime && currentTime <= closeTime;
            }
        }
        // 토요일 영업시간 체크
        else if (currentDay === 6) { // 토요일
            if (pharmacy.dutyTime2s && pharmacy.dutyTime2c) {
                const openTime = parseInt(pharmacy.dutyTime2s.replace(':', ''));
                const closeTime = parseInt(pharmacy.dutyTime2c.replace(':', ''));
                return currentTime >= openTime && currentTime <= closeTime;
            }
        }
        // 일요일 영업시간 체크
        else if (currentDay === 0) { // 일요일
            if (pharmacy.dutyTime3s && pharmacy.dutyTime3c) {
                const openTime = parseInt(pharmacy.dutyTime3s.replace(':', ''));
                const closeTime = parseInt(pharmacy.dutyTime3c.replace(':', ''));
                return currentTime >= openTime && currentTime <= closeTime;
            }
        }

        // 영업시간 정보가 없으면 기본값 (영업중으로 가정)
        return true;
    }

    // 영업시간 포맷팅
    formatOperatingHours(pharmacy) {
        const hours = [];
        
        if (pharmacy.dutyTime1s && pharmacy.dutyTime1c) {
            hours.push(`평일: ${pharmacy.dutyTime1s} - ${pharmacy.dutyTime1c}`);
        }
        if (pharmacy.dutyTime2s && pharmacy.dutyTime2c) {
            hours.push(`토요일: ${pharmacy.dutyTime2s} - ${pharmacy.dutyTime2c}`);
        }
        if (pharmacy.dutyTime3s && pharmacy.dutyTime3c) {
            hours.push(`일요일: ${pharmacy.dutyTime3s} - ${pharmacy.dutyTime3c}`);
        }
        
        return hours.length > 0 ? hours.join('\n') : '영업시간 정보 없음';
    }

    // 거리 계산 (하버사인 공식)
    calculateDistance(lat1, lon1, lat2, lon2) {
        const R = 6371; // 지구 반지름 (km)
        const dLat = this.deg2rad(lat2 - lat1);
        const dLon = this.deg2rad(lon2 - lon1);
        const a = 
            Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) * 
            Math.sin(dLon/2) * Math.sin(dLon/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        const distance = R * c;
        return distance;
    }

    deg2rad(deg) {
        return deg * (Math.PI/180);
    }
}

// 전역에서 사용할 수 있도록 설정
window.PublicDataService = PublicDataService;
