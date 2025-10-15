// 약국 API 서비스 클래스
class PharmacyApiService {
    constructor() {
        this.baseUrl = '/api/pharmacy'; // 백엔드 API 엔드포인트
    }

    // 약국 목록 조회 (공공데이터 API 연동)
    async getPharmacyList(params = {}) {
        try {
            const queryParams = new URLSearchParams({
                pageNo: params.pageNo || 1,
                numOfRows: params.numOfRows || 20,
                ...params
            });

            const response = await fetch(`${this.baseUrl}/list?${queryParams}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return this.transformPharmacyData(data);
        } catch (error) {
            console.error('약국 목록 조회 실패:', error);
            throw error;
        }
    }

    // 위치 기반 약국 검색
    async searchPharmaciesByLocation(latitude, longitude, radius = 2000) {
        try {
            const response = await fetch(`${this.baseUrl}/search`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    latitude,
                    longitude,
                    radius
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return this.transformPharmacyData(data);
        } catch (error) {
            console.error('위치 기반 약국 검색 실패:', error);
            throw error;
        }
    }

    // 약국 상세 정보 조회
    async getPharmacyDetail(pharmacyId) {
        try {
            const response = await fetch(`${this.baseUrl}/detail/${pharmacyId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('약국 상세 정보 조회 실패:', error);
            throw error;
        }
    }

    // 공공데이터 API 응답을 프론트엔드 형식으로 변환
    transformPharmacyData(apiResponse) {
        if (!apiResponse || !apiResponse.body || !apiResponse.body.items) {
            return [];
        }

        return apiResponse.body.items.map(item => ({
            pharmacyId: item.dutyNo || item.hpid,
            name: item.dutyName || item.dutyAddr,
            address: item.dutyAddr,
            phone: item.dutyTel1,
            latitude: parseFloat(item.wgs84Lat) || parseFloat(item.lat),
            longitude: parseFloat(item.wgs84Lon) || parseFloat(item.lng),
            isOpen: this.checkIsOpen(item.dutyTime1c, item.dutyTime2c, item.dutyTime3c),
            operatingHours: this.formatOperatingHours(item),
            distance: item.distance || null
        }));
    }

    // 약국 영업시간 확인
    checkIsOpen(time1c, time2c, time3c) {
        const now = new Date();
        const currentTime = now.getHours() * 100 + now.getMinutes();
        
        // 간단한 영업시간 체크 (실제로는 더 복잡한 로직 필요)
        return currentTime >= 900 && currentTime <= 2100; // 9시~21시
    }

    // 영업시간 포맷팅
    formatOperatingHours(pharmacy) {
        const hours = [];
        
        if (pharmacy.dutyTime1c && pharmacy.dutyTime1s) {
            hours.push(`평일: ${pharmacy.dutyTime1s} - ${pharmacy.dutyTime1c}`);
        }
        if (pharmacy.dutyTime2c && pharmacy.dutyTime2s) {
            hours.push(`토요일: ${pharmacy.dutyTime2s} - ${pharmacy.dutyTime2c}`);
        }
        if (pharmacy.dutyTime3c && pharmacy.dutyTime3s) {
            hours.push(`일요일: ${pharmacy.dutyTime3s} - ${pharmacy.dutyTime3c}`);
        }
        
        return hours.join('\n');
    }

    // 에러 처리
    handleError(error, context = '') {
        console.error(`약국 API 에러 ${context}:`, error);
        
        // 사용자에게 친화적인 에러 메시지 표시
        const errorMessages = {
            'NetworkError': '네트워크 연결을 확인해주세요.',
            'TimeoutError': '요청 시간이 초과되었습니다.',
            'NotFound': '약국 정보를 찾을 수 없습니다.',
            'ServerError': '서버 오류가 발생했습니다.'
        };

        const message = errorMessages[error.name] || '알 수 없는 오류가 발생했습니다.';
        this.showErrorMessage(message);
    }

    // 에러 메시지 표시
    showErrorMessage(message) {
        // 토스트 메시지나 모달로 에러 표시
        if (window.showToast) {
            window.showToast(message, 'error');
        } else {
            alert(message);
        }
    }
}

// 전역에서 사용할 수 있도록 설정
window.PharmacyApiService = PharmacyApiService;
