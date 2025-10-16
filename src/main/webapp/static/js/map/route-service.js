/**
 * 카카오맵 길찾기 서비스
 */
class RouteService {
    constructor() {
        this.map = null;
        this.routeDisplay = null;
        this.isLoaded = false;
        this.routeType = 'walking'; // 기본값: 도보
    }

    // 카카오맵 API 로드
    async loadKakaoMapScript() {
        return new Promise((resolve, reject) => {
            console.log('카카오 지도 API 로드 시작...');
            
            if (window.kakao && window.kakao.maps) {
                console.log('카카오 지도 API 이미 로드됨');
                this.isLoaded = true;
                resolve();
                return;
            }

            // JSP에서 이미 로드했으므로 대기만 함
            const checkKakao = () => {
                if (window.kakao && window.kakao.maps) {
                    console.log('카카오 지도 API 로드 완료');
                    this.isLoaded = true;
                    resolve();
                } else {
                    console.log('카카오 지도 API 로드 대기 중...');
                    setTimeout(checkKakao, 100);
                }
            };
            
            // 약간의 지연 후 확인
            setTimeout(checkKakao, 100);
        });
    }

    // 지도 초기화
    initializeMap(containerId, startLat, startLng, endLat, endLng) {
        return new Promise((resolve, reject) => {
            try {
                console.log('지도 초기화 시작:', containerId, startLat, startLng, endLat, endLng);
                
                if (!this.isLoaded) {
                    console.error('카카오 지도 API가 로드되지 않았습니다.');
                    reject(new Error('카카오 지도 API가 로드되지 않았습니다.'));
                    return;
                }

                // 지도 컨테이너 확인
                const mapContainer = document.getElementById(containerId);
                if (!mapContainer) {
                    console.error('지도 컨테이너를 찾을 수 없습니다:', containerId);
                    reject(new Error('지도 컨테이너를 찾을 수 없습니다: ' + containerId));
                    return;
                }

                // 출발지와 도착지 중간점 계산
                const centerLat = (startLat + endLat) / 2;
                const centerLng = (startLng + endLng) / 2;
                console.log('지도 중심점:', centerLat, centerLng);

                // 지도 생성 (경로 중심으로)
                const mapOption = {
                    center: new kakao.maps.LatLng(centerLat, centerLng),
                    level: 3 // 더 가까운 줌 레벨로 변경
                };

                this.map = new kakao.maps.Map(mapContainer, mapOption);
                console.log('지도 생성 완료');

                // 출발지 마커
                const startPosition = new kakao.maps.LatLng(startLat, startLng);
                const startMarker = new kakao.maps.Marker({
                    position: startPosition,
                    title: '출발지 (강북삼성병원)'
                });
                startMarker.setMap(this.map);
                console.log('출발지 마커 추가 완료');

                // 도착지 마커
                const endPosition = new kakao.maps.LatLng(endLat, endLng);
                const endMarker = new kakao.maps.Marker({
                    position: endPosition,
                    title: '도착지 (약국)'
                });
                endMarker.setMap(this.map);
                console.log('도착지 마커 추가 완료');

                // 실제 도보 경로 표시 (네비게이션처럼)
                this.showWalkingRoute(startLat, startLng, endLat, endLng)
                    .then(() => {
                        console.log('도보 경로 표시 완료');
                        // 지도를 경로에 맞게 자동 조정
                        this.fitMapToRoute(startLat, startLng, endLat, endLng);
                        resolve();
                    })
                    .catch(error => {
                        console.error('도보 경로 표시 실패, 개선된 경로로 대체:', error);
                        // 실패 시 개선된 경로로 대체
                        this.showImprovedRoute(startLat, startLng, endLat, endLng);
                        this.fitMapToRoute(startLat, startLng, endLat, endLng);
                        resolve();
                    });

            } catch (error) {
                console.error('지도 초기화 실패:', error);
                reject(error);
            }
        });
    }

    // 개선된 대체 경로 표시 (중간 경유점 포함)
    showImprovedRoute(startLat, startLng, endLat, endLng) {
        try {
            console.log('개선된 경로 표시 시작:', startLat, startLng, '→', endLat, endLng);
            
            // 기존 경로 제거
            if (this.routeDisplay) {
                this.routeDisplay.setMap(null);
            }
            
            // 출발지와 도착지 좌표
            const start = new kakao.maps.LatLng(startLat, startLng);
            const end = new kakao.maps.LatLng(endLat, endLng);
            
            // 중간 경유점 계산 (더 현실적인 경로)
            const waypoints = this.calculateWaypoints(startLat, startLng, endLat, endLng);
            
            // 경로 배열 생성
            const path = [start, ...waypoints, end];
            
            // 개선된 경로 표시 (점선으로 실제 도로가 아님을 표시)
            const polyline = new kakao.maps.Polyline({
                path: path,
                strokeWeight: 6,
                strokeColor: '#f59e0b', // 주황색으로 직선 경로임을 표시
                strokeOpacity: 0.8,
                strokeStyle: 'shortdash' // 점선으로 실제 도로가 아님을 표시
            });
            
            polyline.setMap(this.map);
            this.routeDisplay = polyline;
            
            console.log('개선된 경로 표시 완료 (경유점 수:', waypoints.length, ')');
            
        } catch (error) {
            console.error('개선된 경로 표시 오류:', error);
            // 최후의 수단으로 직선 경로
            this.showSimpleRoute(startLat, startLng, endLat, endLng);
        }
    }

    // 간단한 직선 경로 표시 (최후의 수단)
    showSimpleRoute(startLat, startLng, endLat, endLng) {
        try {
            console.log('직선 경로 표시 시작:', startLat, startLng, '→', endLat, endLng);
            
            // 기존 경로 제거
            if (this.routeDisplay) {
                this.routeDisplay.setMap(null);
            }
            
            // 출발지와 도착지 좌표
            const start = new kakao.maps.LatLng(startLat, startLng);
            const end = new kakao.maps.LatLng(endLat, endLng);
            
            // 직선 경로 표시
            const polyline = new kakao.maps.Polyline({
                path: [start, end],
                strokeWeight: 4,
                strokeColor: '#ef4444', // 빨간색으로 긴급 경로임을 표시
                strokeOpacity: 0.7,
                strokeStyle: 'shortdashdot' // 점선으로 실제 도로가 아님을 표시
            });
            
            polyline.setMap(this.map);
            this.routeDisplay = polyline;
            
            console.log('직선 경로 표시 완료');
            
        } catch (error) {
            console.error('직선 경로 표시 오류:', error);
        }
    }

    // 중간 경유점 계산 (더 현실적인 경로 생성)
    calculateWaypoints(startLat, startLng, endLat, endLng) {
        const waypoints = [];
        
        // 거리 계산
        const distance = this.calculateDistance(startLat, startLng, endLat, endLng);
        
        // 거리가 500m 이상일 때만 중간 경유점 추가
        if (distance > 500) {
            // 중간점 계산
            const midLat = (startLat + endLat) / 2;
            const midLng = (startLng + endLng) / 2;
            
            // 중간점을 약간 이동시켜서 더 현실적인 경로 생성
            const latOffset = (endLat - startLat) * 0.1; // 10% 오프셋
            const lngOffset = (endLng - startLng) * 0.1;
            
            // 2개의 경유점 추가
            waypoints.push(new kakao.maps.LatLng(
                midLat - latOffset, 
                midLng - lngOffset
            ));
            waypoints.push(new kakao.maps.LatLng(
                midLat + latOffset, 
                midLng + lngOffset
            ));
        }
        
        return waypoints;
    }

    // 경로 타입에 따른 우선순위 설정
    getRoutePriority() {
        switch (this.routeType) {
            case 'walking':
                return kakao.maps.services.Directions.Priority.SHORTEST_PATH;
            case 'bicycle':
                return kakao.maps.services.Directions.Priority.SHORTEST_PATH;
            case 'public_transport':
                return kakao.maps.services.Directions.Priority.MINIMUM_TIME;
            default:
                return kakao.maps.services.Directions.Priority.SHORTEST_PATH;
        }
    }

    // 경로 타입 설정
    setRouteType(type) {
        this.routeType = type;
        console.log('경로 타입 변경:', type);
    }

    // 경로 타입에 따른 스타일 반환
    getRouteStyle() {
        switch (this.routeType) {
            case 'walking':
                return {
                    weight: 8,
                    color: '#2563eb', // 파란색 - 도보
                    opacity: 0.9,
                    style: 'solid'
                };
            case 'bicycle':
                return {
                    weight: 6,
                    color: '#10b981', // 초록색 - 자전거
                    opacity: 0.8,
                    style: 'solid'
                };
            case 'public_transport':
                return {
                    weight: 5,
                    color: '#8b5cf6', // 보라색 - 대중교통
                    opacity: 0.8,
                    style: 'solid'
                };
            default:
                return {
                    weight: 8,
                    color: '#2563eb',
                    opacity: 0.9,
                    style: 'solid'
                };
        }
    }

    // 실제 도보 경로 표시 (네비게이션처럼)
    async showWalkingRoute(startLat, startLng, endLat, endLng) {
        return new Promise((resolve, reject) => {
            try {
                console.log('도보 경로 검색 시작:', startLat, startLng, '→', endLat, endLng);
                
                // 기존 경로 제거
                if (this.routeDisplay) {
                    this.routeDisplay.setMap(null);
                }
                
                // 출발지와 도착지 좌표
                const start = new kakao.maps.LatLng(startLat, startLng);
                const end = new kakao.maps.LatLng(endLat, endLng);
                
                // Directions 서비스 사용 가능 여부 확인
                if (!window.kakao || !window.kakao.maps || !window.kakao.maps.services || !window.kakao.maps.services.Directions) {
                    console.warn('Directions 서비스가 사용 불가능합니다. 개선된 경로로 대체합니다.');
                    this.showImprovedRoute(startLat, startLng, endLat, endLng);
                    this.fitMapToRoute(startLat, startLng, endLat, endLng);
                    resolve();
                    return;
                }

                // Directions 서비스 생성 (안전한 방법)
                let directionsService;
                try {
                    directionsService = new kakao.maps.services.Directions();
                } catch (error) {
                    console.error('Directions 서비스 생성 실패:', error);
                    this.showImprovedRoute(startLat, startLng, endLat, endLng);
                    this.fitMapToRoute(startLat, startLng, endLat, endLng);
                    resolve();
                    return;
                }
                
                // 경로 검색 (타입에 따른 우선순위 적용)
                directionsService.route({
                    origin: start,
                    destination: end,
                    priority: this.getRoutePriority()
                }, (result, status) => {
                    console.log('도보 경로 검색 결과:', status, result);
                    
                    if (status === kakao.maps.services.Directions.Status.OK) {
                        try {
                            const route = result.routes[0];
                            console.log('도보 경로 데이터:', route);
                            
                            // 실제 도로 경로를 Polyline으로 표시
                            const path = [];
                            const bounds = new kakao.maps.LatLngBounds();
                            
                            route.sections.forEach((section, sectionIndex) => {
                                console.log(`섹션 ${sectionIndex} 처리:`, section);
                                
                                section.roads.forEach((road, roadIndex) => {
                                    console.log(`도로 ${roadIndex} 처리:`, road);
                                    
                                    // vertexes 배열에서 좌표 추출
                                    if (road.vertexes && road.vertexes.length > 0) {
                                        for (let i = 0; i < road.vertexes.length; i += 2) {
                                            if (i + 1 < road.vertexes.length) {
                                                const lng = road.vertexes[i];     // x 좌표 (경도)
                                                const lat = road.vertexes[i + 1]; // y 좌표 (위도)
                                                const point = new kakao.maps.LatLng(lat, lng);
                                                path.push(point);
                                                bounds.extend(point);
                                            }
                                        }
                                    }
                                });
                            });
                            
                            console.log('추출된 도보 경로 포인트 수:', path.length);
                            
                            if (path.length > 0) {
                                // 실제 경로 Polyline 표시 (타입에 따른 스타일 적용)
                                const routeStyle = this.getRouteStyle();
                                const polyline = new kakao.maps.Polyline({
                                    path: path,
                                    strokeWeight: routeStyle.weight,
                                    strokeColor: routeStyle.color,
                                    strokeOpacity: routeStyle.opacity,
                                    strokeStyle: routeStyle.style
                                });
                                
                                polyline.setMap(this.map);
                                this.routeDisplay = polyline;
                                
                                // 지도를 경로에 맞게 자동 조정
                                this.map.setBounds(bounds, 50);
                                
                                console.log('실제 도보 경로 표시 완료');
                                resolve();
                            } else {
                                console.warn('도보 경로 포인트가 없습니다.');
                                reject(new Error('도보 경로 포인트가 없습니다.'));
                            }
                            
                        } catch (routeError) {
                            console.error('도보 경로 표시 오류:', routeError);
                            reject(routeError);
                        }
                    } else {
                        console.error('도보 경로 검색 실패:', status);
                        reject(new Error('도보 경로 검색 실패: ' + status));
                    }
                });
                
            } catch (error) {
                console.error('도보 경로 표시 오류:', error);
                reject(error);
            }
        });
    }

    // 지도를 경로에 맞게 자동 조정
    fitMapToRoute(startLat, startLng, endLat, endLng) {
        try {
            console.log('지도 자동 조정 시작:', startLat, startLng, '→', endLat, endLng);
            
            // 출발지와 도착지 좌표
            const start = new kakao.maps.LatLng(startLat, startLng);
            const end = new kakao.maps.LatLng(endLat, endLng);
            
            // LatLngBounds 객체 생성
            const bounds = new kakao.maps.LatLngBounds();
            bounds.extend(start);
            bounds.extend(end);
            
            // 지도를 경로에 맞게 조정 (적절한 여백 포함)
            this.map.setBounds(bounds, 50); // 50px 여백 추가
            
            console.log('지도 자동 조정 완료');
            
        } catch (error) {
            console.error('지도 자동 조정 오류:', error);
        }
    }

    // 길찾기 경로 표시
    async showRoute(startLat, startLng, endLat, endLng) {
        return new Promise((resolve, reject) => {
            try {
                console.log('길찾기 시작:', startLat, startLng, '→', endLat, endLng);
                
                // 기존 경로 제거
                if (this.routeDisplay) {
                    this.routeDisplay.setMap(null);
                }

                // 출발지와 도착지 좌표
                const start = new kakao.maps.LatLng(startLat, startLng);
                const end = new kakao.maps.LatLng(endLat, endLng);

                // 길찾기 서비스
                if (!window.kakao || !window.kakao.maps || !window.kakao.maps.services) {
                    console.error('카카오 지도 서비스가 로드되지 않았습니다.');
                    resolve(); // 서비스가 없어도 계속 진행
                    return;
                }
                
                // Directions 서비스 사용 가능 여부 확인
                if (!window.kakao || !window.kakao.maps || !window.kakao.maps.services || !window.kakao.maps.services.Directions) {
                    console.warn('Directions 서비스가 사용 불가능합니다. 개선된 경로로 대체합니다.');
                    this.showImprovedRoute(startLat, startLng, endLat, endLng);
                    this.fitMapToRoute(startLat, startLng, endLat, endLng);
                    resolve();
                    return;
                }

                let directionsService;
                try {
                    directionsService = new kakao.maps.services.Directions();
                    console.log('길찾기 서비스 생성 완료');
                } catch (error) {
                    console.error('길찾기 서비스 생성 실패:', error);
                    this.showImprovedRoute(startLat, startLng, endLat, endLng);
                    this.fitMapToRoute(startLat, startLng, endLat, endLng);
                    resolve();
                    return;
                }

                // 길찾기 요청 (타입에 따른 우선순위 적용)
                directionsService.route({
                    origin: start,
                    destination: end,
                    priority: this.getRoutePriority()
                }, (result, status) => {
                    console.log('길찾기 응답:', status, result);
                    
                    if (status === kakao.maps.services.Directions.Status.OK) {
                        try {
                            console.log('길찾기 성공, 경로 표시 시작');
                            const route = result.routes[0];
                            
                            // 기존 경로 제거
                            if (this.routeDisplay) {
                                this.routeDisplay.setMap(null);
                            }
                            
                            // 실제 도로 경로를 Polyline으로 표시
                            const path = [];
                            const bounds = new kakao.maps.LatLngBounds();
                            
                            console.log('경로 데이터 파싱 시작:', route.sections);
                            
                            // 각 섹션의 도로들을 처리
                            route.sections.forEach((section, sectionIndex) => {
                                console.log(`섹션 ${sectionIndex} 처리:`, section);
                                
                                section.roads.forEach((road, roadIndex) => {
                                    console.log(`도로 ${roadIndex} 처리:`, road);
                                    
                                    // vertexes 배열에서 좌표 추출
                                    if (road.vertexes && road.vertexes.length > 0) {
                                        for (let i = 0; i < road.vertexes.length; i += 2) {
                                            if (i + 1 < road.vertexes.length) {
                                                const lng = road.vertexes[i];     // x 좌표 (경도)
                                                const lat = road.vertexes[i + 1]; // y 좌표 (위도)
                                                const point = new kakao.maps.LatLng(lat, lng);
                                                path.push(point);
                                                bounds.extend(point);
                                            }
                                        }
                                    }
                                });
                            });
                            
                            console.log('추출된 경로 포인트 수:', path.length);
                            
                            // 경로가 있으면 Polyline으로 표시 (타입에 따른 스타일 적용)
                            if (path.length > 0) {
                                const routeStyle = this.getRouteStyle();
                                const polyline = new kakao.maps.Polyline({
                                    path: path,
                                    strokeWeight: routeStyle.weight,
                                    strokeColor: routeStyle.color,
                                    strokeOpacity: routeStyle.opacity,
                                    strokeStyle: routeStyle.style
                                });
                                
                                polyline.setMap(this.map);
                                this.routeDisplay = polyline;
                                console.log('실제 도로 경로 Polyline 표시 완료');
                            } else {
                                console.warn('경로 포인트가 없습니다. 직선으로 연결합니다.');
                                
                                // 경로 포인트가 없으면 출발지와 도착지를 직선으로 연결
                                const startPoint = new kakao.maps.LatLng(startLat, startLng);
                                const endPoint = new kakao.maps.LatLng(endLat, endLng);
                                
                                const directLine = new kakao.maps.Polyline({
                                    path: [startPoint, endPoint],
                                    strokeWeight: 6,
                                    strokeColor: '#ff6b6b',
                                    strokeOpacity: 0.8,
                                    strokeStyle: 'dashed'
                                });
                                
                                directLine.setMap(this.map);
                                this.routeDisplay = directLine;
                                console.log('직선 경로 표시 완료');
                            }

                            // 지도 영역을 경로에 맞게 조정
                            this.map.setBounds(bounds);
                            console.log('지도 영역 조정 완료');
                            resolve();
                        } catch (routeError) {
                            console.error('경로 표시 오류:', routeError);
                            resolve(); // 경로 표시 실패해도 계속 진행
                        }
                    } else {
                        console.error('길찾기 실패:', status);
                        resolve(); // 길찾기 실패해도 계속 진행
                    }
                });

            } catch (error) {
                console.error('길찾기 오류:', error);
                resolve(); // 오류가 발생해도 계속 진행
            }
        });
    }

    // 거리 및 예상 시간 계산
    calculateRouteInfo(startLat, startLng, endLat, endLng) {
        return new Promise((resolve, reject) => {
            try {
                console.log('경로 정보 계산 시작:', startLat, startLng, '→', endLat, endLng);
                
                // Directions 서비스가 사용 가능한지 확인
                if (!window.kakao || !window.kakao.maps || !window.kakao.maps.services) {
                    console.error('카카오 지도 서비스가 로드되지 않았습니다.');
                    reject(new Error('카카오 지도 서비스가 로드되지 않았습니다.'));
                    return;
                }

                // Directions 서비스 사용 가능 여부 확인
                if (!window.kakao || !window.kakao.maps || !window.kakao.maps.services || !window.kakao.maps.services.Directions) {
                    console.warn('Directions 서비스가 사용 불가능합니다. 직선거리로 대체합니다.');
                    const distance = this.calculateDistance(startLat, startLng, endLat, endLng);
                    const estimatedTime = Math.round(distance / 80); // 대략적인 도보 시간 (분당 80m)
                    resolve({
                        distance: distance,
                        duration: estimatedTime * 60, // 초 단위로 변환
                        summary: { totalDistance: distance, totalTime: estimatedTime * 60 }
                    });
                    return;
                }

                // Directions 서비스 생성 (더 안전한 방법)
                let directionsService;
                try {
                    directionsService = new kakao.maps.services.Directions();
                    console.log('Directions 서비스 생성 완료');
                } catch (error) {
                    console.error('Directions 서비스 생성 실패:', error);
                    // 실패 시 직선거리로 대체
                    const distance = this.calculateDistance(startLat, startLng, endLat, endLng);
                    const estimatedTime = Math.round(distance / 80); // 대략적인 도보 시간 (분당 80m)
                    resolve({
                        distance: distance,
                        duration: estimatedTime * 60, // 초 단위로 변환
                        summary: { totalDistance: distance, totalTime: estimatedTime * 60 }
                    });
                    return;
                }
                
                const start = new kakao.maps.LatLng(startLat, startLng);
                const end = new kakao.maps.LatLng(endLat, endLng);

                directionsService.route({
                    origin: start,
                    destination: end,
                    priority: this.getRoutePriority()
                }, (result, status) => {
                    console.log('경로 정보 응답:', status, result);
                    
                    if (status === kakao.maps.services.Directions.Status.OK) {
                        const route = result.routes[0];
                        const section = route.sections[0];
                        
                        console.log('경로 정보 계산 완료:', {
                            distance: section.distance,
                            duration: section.duration
                        });
                        
                        resolve({
                            distance: section.distance,
                            duration: section.duration,
                            summary: route.summary
                        });
                    } else {
                        console.error('경로 정보 계산 실패:', status);
                        reject(new Error('경로 정보 계산 실패: ' + status));
                    }
                });
            } catch (error) {
                console.error('경로 정보 계산 오류:', error);
                reject(error);
            }
        });
    }

    // 지도 크기 조정
    resizeMap() {
        if (this.map) {
            kakao.maps.event.trigger(this.map, 'resize');
        }
    }

    // 지도 제거
    destroy() {
        if (this.routeDisplay) {
            this.routeDisplay.setMap(null);
            this.routeDisplay = null;
        }
        if (this.map) {
            this.map = null;
        }
    }

    // 간단한 거리 계산 (직선거리)
    calculateDistance(lat1, lng1, lat2, lng2) {
        const R = 6371e3; // 지구 반지름 (미터)
        const φ1 = lat1 * Math.PI / 180;
        const φ2 = lat2 * Math.PI / 180;
        const Δφ = (lat2 - lat1) * Math.PI / 180;
        const Δλ = (lng2 - lng1) * Math.PI / 180;

        const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                  Math.cos(φ1) * Math.cos(φ2) *
                  Math.sin(Δλ/2) * Math.sin(Δλ/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c; // 미터 단위
    }
}

// 전역 인스턴스
window.routeService = new RouteService();
