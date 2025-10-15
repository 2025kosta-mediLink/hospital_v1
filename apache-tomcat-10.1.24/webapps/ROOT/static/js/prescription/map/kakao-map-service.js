// 카카오맵 서비스 클래스
class KakaoMapService {
    constructor() {
        this.map = null;
        this.markers = [];
        this.infoWindow = null;
        this.isLoaded = false;
    }

    // 카카오맵 초기화
    async initMap(containerId, options = {}) {
        return new Promise((resolve, reject) => {
            if (window.kakao && window.kakao.maps) {
                this.createMap(containerId, options);
                resolve(this.map);
            } else {
                // 카카오맵 스크립트 로드
                this.loadKakaoMapScript()
                    .then(() => {
                        this.createMap(containerId, options);
                        resolve(this.map);
                    })
                    .catch(reject);
            }
        });
    }

    // 카카오맵 스크립트 로드
    loadKakaoMapScript() {
        return new Promise((resolve) => {
            if (window.kakao && window.kakao.maps) {
                this.isLoaded = true;
                resolve();
                return;
            }

            // JSP에서 이미 스크립트를 로드했으므로 대기만 함
            const checkInterval = setInterval(() => {
                if (window.kakao && window.kakao.maps) {
                    clearInterval(checkInterval);
                    this.isLoaded = true;
                    resolve();
                }
            }, 100);

            // 3초 후 타임아웃
            setTimeout(() => {
                clearInterval(checkInterval);
                resolve();
            }, 3000);
        });
    }

    // 지도 생성
    createMap(containerId, options) {
        const defaultOptions = {
            center: new kakao.maps.LatLng(
                API_CONFIG.DEFAULT_LOCATION.latitude,
                API_CONFIG.DEFAULT_LOCATION.longitude
            ),
            level: 3
        };

        const mapOptions = { ...defaultOptions, ...options };
        this.map = new kakao.maps.Map(document.getElementById(containerId), mapOptions);
    }

    // 약국 마커 추가
    addPharmacyMarkers(pharmacies) {
        // 기존 마커 제거
        this.clearMarkers();

        pharmacies.forEach(pharmacy => {
            const marker = this.createMarker(pharmacy);
            this.markers.push(marker);
        });
    }

    // 마커 생성
    createMarker(pharmacy) {
        // 좌표 필드명 통일 (lat/lng 사용)
        const lat = pharmacy.lat || pharmacy.latitude;
        const lng = pharmacy.lng || pharmacy.longitude;
        const position = new kakao.maps.LatLng(lat, lng);
        
        // 기본 카카오마커 사용 (이미지 없이)
        const marker = new kakao.maps.Marker({
            position: position,
            title: pharmacy.name
        });

        // 마커에 약국 정보 저장
        marker.pharmacy = pharmacy;

        // 마커 클릭 이벤트 - 바로 상세모달창 표시
        kakao.maps.event.addListener(marker, 'click', () => {
            console.log('지도 마커 클릭:', pharmacy.name);
            // 바로 상세모달창 표시
            if (typeof showPharmacyDetailModal === 'function') {
                showPharmacyDetailModal(pharmacy);
            } else if (typeof selectPharmacy === 'function') {
                selectPharmacy(pharmacy.id);
            }
        });

        marker.setMap(this.map);
        return marker;
    }

    // 정보창 표시
    showInfoWindow(marker, pharmacy) {
        if (this.infoWindow) {
            this.infoWindow.close();
        }

        const content = `
            <div class="pharmacy-info-window">
                <div class="pharmacy-name">${pharmacy.name}</div>
                <div class="pharmacy-address">${pharmacy.address}</div>
                <div class="pharmacy-phone">${pharmacy.phone}</div>
                <div class="pharmacy-status ${pharmacy.isOpen ? 'open' : 'closed'}">
                    ${pharmacy.isOpen ? '영업중' : '영업마감'}
                </div>
            </div>
        `;

        this.infoWindow = new kakao.maps.InfoWindow({
            content: content,
            removable: true
        });

        this.infoWindow.open(this.map, marker);
    }

    // 마커 제거
    clearMarkers() {
        this.markers.forEach(marker => marker.setMap(null));
        this.markers = [];
        if (this.infoWindow) {
            this.infoWindow.close();
        }
    }

    // 지도 중심 이동
    setCenter(latitude, longitude) {
        if (this.map) {
            const moveLatLon = new kakao.maps.LatLng(latitude, longitude);
            this.map.setCenter(moveLatLon);
        }
    }

    // 현재 위치로 지도 이동
    moveToCurrentLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    this.setCenter(lat, lng);
                },
                (error) => {
                    console.error('위치 정보를 가져올 수 없습니다:', error);
                    // 기본 위치로 설정
                    this.setCenter(
                        API_CONFIG.DEFAULT_LOCATION.latitude,
                        API_CONFIG.DEFAULT_LOCATION.longitude
                    );
                }
            );
        }
    }

    // 카카오 로컬 API를 사용한 약국 검색 (REST API 키 사용)
    async searchPharmaciesWithKakao(keyword = '약국', lat, lng) {
        try {
            console.log('카카오 로컬 API 검색 시작:', keyword, lat, lng);
            
            // REST API 키를 사용한 직접 API 호출
            const response = await fetch(`https://dapi.kakao.com/v2/local/search/keyword.json?query=${encodeURIComponent(keyword)}&x=${lng}&y=${lat}&radius=3000`, {
                method: 'GET',
                headers: {
                    'Authorization': `KakaoAK ${API_CONFIG.KAKAO_REST_API_KEY}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('카카오 API 응답:', data);
            
            if (data.documents && data.documents.length > 0) {
                const pharmacies = data.documents
                    .filter(place => {
                        // 약국만 필터링 (더 엄격하게)
                        const isPharmacy = place.category_group_code === 'PM9' || 
                                          place.place_name.includes('약국') || 
                                          place.category_name.includes('약국');
                        
                        // 약국이 아닌 것들 제외 (카페, 편의점, 식당 등)
                        const isNotOtherBusiness = !place.place_name.includes('스타벅스') &&
                                                  !place.place_name.includes('카페') &&
                                                  !place.place_name.includes('편의점') &&
                                                  !place.place_name.includes('GS25') &&
                                                  !place.place_name.includes('CU') &&
                                                  !place.place_name.includes('식당') &&
                                                  !place.place_name.includes('레스토랑');
                        
                        return isPharmacy && isNotOtherBusiness;
                    })
                    .map(place => ({
                        id: place.id,
                        name: place.place_name,
                        address: place.road_address_name || place.address_name,
                        phone: place.phone || '전화번호 없음',
                        lat: parseFloat(place.y),
                        lng: parseFloat(place.x),
                        distance: this.calculateDistance(lat, lng, parseFloat(place.y), parseFloat(place.x)),
                        isOpen: this.checkIsOpen(place)
                    }));
                
                // 거리순으로 정렬
                pharmacies.sort((a, b) => a.distance - b.distance);
                console.log('검색된 약국:', pharmacies);
                return pharmacies;
            } else {
                console.log('검색된 약국이 없습니다.');
                return [];
            }
        } catch (error) {
            console.error('카카오 로컬 API 오류:', error);
            return [];
        }
    }

    // 약국 영업시간 체크 (간단한 로직)
    checkIsOpen(place) {
        const now = new Date();
        const currentHour = now.getHours();
        // 간단한 영업시간 체크 (9시~21시)
        return currentHour >= 9 && currentHour <= 21;
    }

    // 반경 내 약국 검색
    async searchPharmaciesInRadius(centerLat, centerLng, radius = API_CONFIG.SEARCH_RADIUS) {
        try {
            // 카카오 로컬 API로 약국 검색
            const pharmacies = await this.searchPharmaciesWithKakao('약국', centerLat, centerLng);
            
            // 거리 계산 및 필터링
            const nearbyPharmacies = pharmacies.filter(pharmacy => {
                return pharmacy.distance <= radius;
            });
            
            console.log('반경 내 약국:', nearbyPharmacies);
            
            // 마커 표시
            if (this.map) {
                this.addPharmacyMarkers(nearbyPharmacies);
            }
            
            return nearbyPharmacies;
        } catch (error) {
            console.error('반경 내 약국 검색 오류:', error);
            return [];
        }
    }

    // 두 지점 간 거리 계산 (미터)
    calculateDistance(lat1, lng1, lat2, lng2) {
        const R = 6371e3; // 지구 반지름 (미터)
        const φ1 = lat1 * Math.PI/180;
        const φ2 = lat2 * Math.PI/180;
        const Δφ = (lat2-lat1) * Math.PI/180;
        const Δλ = (lng2-lng1) * Math.PI/180;

        const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ/2) * Math.sin(Δλ/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
}

// 전역에서 사용할 수 있도록 설정
window.KakaoMapService = KakaoMapService;
