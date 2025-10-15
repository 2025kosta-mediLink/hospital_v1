// 카카오맵 장소 검색 서비스
class KakaoPlaceService {
    constructor() {
        this.apiKey = API_CONFIG.KAKAO_MAP_KEY;
        this.baseUrl = 'https://dapi.kakao.com/v2/local';
    }

    // 약국 검색 (카카오맵 장소 API)
    async searchPharmacies(keyword = '약국', x = null, y = null, radius = 2000) {
        try {
            console.log('=== 카카오맵 장소 검색 시작 ===');
            console.log('검색 키워드:', keyword);
            console.log('위치:', x, y);
            console.log('반경:', radius, 'm');

            const queryParams = new URLSearchParams({
                query: keyword,
                size: 15 // 최대 15개 결과
            });

            // 현재 위치가 있으면 반경 검색
            if (x && y) {
                queryParams.append('x', x);
                queryParams.append('y', y);
                queryParams.append('radius', radius);
            }

            const response = await fetch(`${this.baseUrl}/search/keyword.json?${queryParams}`, {
                method: 'GET',
                headers: {
                    'Authorization': `KakaoAK ${this.apiKey}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('카카오맵 API 응답:', data);

            return this.transformPlaceData(data);
        } catch (error) {
            console.error('카카오맵 장소 검색 실패:', error);
            throw error;
        }
    }

    // 카카오맵 응답을 프론트엔드 형식으로 변환
    transformPlaceData(apiResponse) {
        if (!apiResponse || !apiResponse.documents) {
            console.warn('카카오맵 응답 구조가 예상과 다릅니다:', apiResponse);
            return [];
        }

        return apiResponse.documents.map(place => this.transformSinglePlace(place));
    }

    // 단일 장소 데이터 변환
    transformSinglePlace(place) {
        return {
            pharmacyId: place.id,
            name: place.place_name,
            address: place.address_name,
            roadAddress: place.road_address_name,
            phone: place.phone,
            latitude: parseFloat(place.y),
            longitude: parseFloat(place.x),
            distance: place.distance ? `${(place.distance / 1000).toFixed(1)}km` : null,
            category: place.category_name,
            isOpen: this.checkIsOpen(place),
            operatingHours: this.getOperatingHours(place),
            rating: null, // 카카오맵 API에서는 평점 정보 없음
            reviewCount: null
        };
    }

    // 영업시간 확인 (간단한 추정)
    checkIsOpen(place) {
        const now = new Date();
        const currentHour = now.getHours();
        
        // 약국은 보통 9시~21시 영업
        // 더 정확한 정보는 별도 API 필요
        return currentHour >= 9 && currentHour <= 21;
    }

    // 영업시간 정보 (카카오맵에서는 제공하지 않음)
    getOperatingHours(place) {
        return '영업시간 정보 없음';
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

    // 현재 위치 기반 약국 검색
    async searchNearbyPharmacies(latitude, longitude, radius = 2000) {
        try {
            // 카카오맵 좌표계로 변환 (경도, 위도 순서)
            const x = longitude;
            const y = latitude;

            const pharmacies = await this.searchPharmacies('약국', x, y, radius);
            
            // 거리 순으로 정렬
            return pharmacies.sort((a, b) => {
                const distA = a.distance ? parseFloat(a.distance) : 999;
                const distB = b.distance ? parseFloat(b.distance) : 999;
                return distA - distB;
            });
        } catch (error) {
            console.error('주변 약국 검색 실패:', error);
            throw error;
        }
    }

    // 특정 약국명으로 검색
    async searchPharmacyByName(pharmacyName, latitude = null, longitude = null) {
        try {
            let x, y;
            if (latitude && longitude) {
                x = longitude;
                y = latitude;
            }

            return await this.searchPharmacies(pharmacyName, x, y);
        } catch (error) {
            console.error('약국명 검색 실패:', error);
            throw error;
        }
    }
}

// 전역에서 사용할 수 있도록 설정
window.KakaoPlaceService = KakaoPlaceService;
