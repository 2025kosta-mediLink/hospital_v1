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
        return new Promise((resolve, reject) => {
            if (window.kakao && window.kakao.maps) {
                resolve();
                return;
            }

            const script = document.createElement('script');
            script.src = `${API_CONFIG.ENDPOINTS.KAKAO_MAP}?appkey=${API_CONFIG.KAKAO_MAP_KEY}&autoload=false`;
            script.onload = () => {
                window.kakao.maps.load(() => {
                    this.isLoaded = true;
                    resolve();
                });
            };
            script.onerror = reject;
            document.head.appendChild(script);
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
        const position = new kakao.maps.LatLng(pharmacy.latitude, pharmacy.longitude);
        
        // 마커 이미지 설정
        const imageSrc = pharmacy.isOpen ? 
            '/static/images/icons/pharmacy_open.png' : 
            '/static/images/icons/pharmacy_closed.png';
        const imageSize = new kakao.maps.Size(30, 30);
        const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

        const marker = new kakao.maps.Marker({
            position: position,
            image: markerImage,
            title: pharmacy.name
        });

        // 마커 클릭 이벤트
        kakao.maps.event.addListener(marker, 'click', () => {
            this.showInfoWindow(marker, pharmacy);
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

    // 반경 내 약국 검색
    searchPharmaciesInRadius(centerLat, centerLng, radius = API_CONFIG.SEARCH_RADIUS) {
        // 실제 구현에서는 서버 API를 호출하여 반경 내 약국을 검색
        console.log(`반경 ${radius}m 내 약국 검색: (${centerLat}, ${centerLng})`);
    }
}

// 전역에서 사용할 수 있도록 설정
window.KakaoMapService = KakaoMapService;
