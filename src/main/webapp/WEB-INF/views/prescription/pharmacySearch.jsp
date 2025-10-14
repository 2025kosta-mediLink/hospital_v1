<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>주변 약국 찾기</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/prescription/pharmacySearch.css?v=20250115_002">
    
    <!-- API 설정 및 지도 서비스 -->
    <script src="${pageContext.request.contextPath}/static/js/config/api-config.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/map/kakao-map-service.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/api/pharmacy-api-service.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/api/public-data-service.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/api/kakao-place-service.js?v=20250115_001"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:set var="headerTitle" value="약국 찾기" scope="request"/>

<div class="wrap">
    <jsp:include page="../common/header.jsp"/>

    <!-- 필터 바 -->
    <div class="filter-bar">
        <div class="filter-dropdown">추천순 ∨</div>
        <div class="filter-tag-dropdown">거리순</div>
        <div class="filter-tag active">영업중</div>
    </div>

    <!-- 전체 화면 지도 -->
    <div class="map-container">
        <div id="map" class="map-fullscreen">
            <!-- 카카오맵 API가 들어갈 영역 -->
        </div>
    </div>

    <!-- 약국 리스트 오버레이 -->
    <div class="pharmacy-list-overlay">
        <div class="list-header">
            <div class="location-selector">현재 지도 중심 ∨</div>
            <div class="sort-selector">관련도순 ∨</div>
            </div>

        <div class="pharmacy-list">
        <c:choose>
            <c:when test="${not empty pharmacies}">
                <c:forEach var="pharmacy" items="${pharmacies}">
                        <div class="pharmacy-list-item" data-pharmacy-id="${pharmacy.pharmacyId}" onclick="selectPharmacy('${pharmacy.pharmacyId}')">
                        <div class="pharmacy-info">
                                <div class="pharmacy-name"><c:out value="${pharmacy.pharmacyName}"/></div>
                                <div class="pharmacy-distance">
                                    <fmt:formatNumber value="${pharmacy.distance * 1000}" pattern="0"/>m
                                </div>
                                <div class="pharmacy-address">
                                    <c:out value="${pharmacy.address}"/>
                                </div>
                            </div>
                                <div class="pharmacy-status">
                                    <c:choose>
                                        <c:when test="${pharmacy.isOpen}">
                                        <span class="status-badge open">영업중</span>
                                        </c:when>
                                        <c:otherwise>
                                        <span class="status-badge closed">영업마감</span>
                                        </c:otherwise>
                                    </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">🏥</div>
                    <div class="empty-text">주변에 약국이 없습니다.</div>
                </div>
            </c:otherwise>
        </c:choose>
        </div>
    </div>

    <!-- 약국 상세 정보 모달 -->
    <div class="pharmacy-detail-modal" id="pharmacyDetailModal" style="display: none;">
        <div class="modal-handle"></div>
        <div class="modal-content">
            <div class="modal-header">
                <div class="pharmacy-title-section">
                    <h3 class="pharmacy-name" id="modalPharmacyName"></h3>
                    <span class="status-badge open" id="modalStatus">영업중</span>
                </div>
                <div class="pharmacy-hours">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                        <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    <span id="modalHours">목 08:30-21:00</span>
                </div>
                <div class="pharmacy-location">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2"/>
                        <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    <span id="modalDistance"></span>
                    <span id="modalAddress"></span>
                </div>
            </div>
            
            <div class="modal-actions">
                <button class="btn-phone" onclick="callPharmacy()">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" stroke="currentColor" stroke-width="2"/>
                    </svg>
                </button>
                <button class="btn-copy" onclick="copyAddress()">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" stroke="currentColor" stroke-width="2"/>
                    </svg>
                </button>
            </div>
            
            <div class="modal-main-action">
                <form id="sendPrescriptionForm" method="post" action="${ctx}/v1/pharmacy/send">
                    <input type="hidden" name="pharmacyId" id="modalPharmacyId">
                    <button type="submit" class="btn-send-prescription">
                        처방전 전달하기
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- 확인 모달 -->
    <div class="confirm-modal" id="confirmModal" style="display: none;">
        <div class="confirm-overlay" onclick="closeConfirmModal()"></div>
        <div class="confirm-content">
            <div class="confirm-header">
                <h3>처방전 전달 확인</h3>
            </div>
            <div class="confirm-body">
                <p id="confirmMessage"></p>
            </div>
            <div class="confirm-actions">
                <button class="btn-cancel" onclick="closeConfirmModal()">취소</button>
                <button class="btn-confirm" onclick="confirmSendPrescription()">전달하기</button>
            </div>
        </div>
    </div>


    <!-- 공통 하단 네비게이션 -->
    <jsp:include page="../common/navigation.jsp"/>
</div>

<script>
    console.log('=== JavaScript 파일 로드됨 ===');
    
    // 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        console.log('=== DOM 로드 완료 ===');
        
        // 카카오맵 초기화
        initKakaoMap();
        
        // 모달 핸들 드래그 이벤트
        const modalHandle = document.querySelector('.modal-handle');
        if (modalHandle) {
            modalHandle.addEventListener('click', function() {
                closePharmacyDetail();
            });
        }
        
        // 처방전 전달 폼 이벤트
        const prescriptionForm = document.getElementById('sendPrescriptionForm');
        if (prescriptionForm) {
            prescriptionForm.addEventListener('submit', function(e) {
                e.preventDefault();
                sendPrescription();
            });
        }
    });
    
    // 카카오맵 초기화 함수
    function initKakaoMap() {
        console.log('=== 카카오맵 초기화 시작 ===');
        console.log('API_CONFIG:', API_CONFIG);
        console.log('카카오맵 API 키:', API_CONFIG.KAKAO_MAP_KEY);
        
        // 지도 컨테이너 확인
        const mapContainer = document.getElementById('map');
        console.log('지도 컨테이너:', mapContainer);
        if (!mapContainer) {
            console.error('지도 컨테이너를 찾을 수 없습니다!');
            return;
        }
        
        // 카카오맵 API가 로드되었는지 확인
        if (typeof kakao !== 'undefined' && kakao.maps) {
            console.log('카카오맵 API가 이미 로드되어 있음');
            createMap();
        } else {
            console.log('카카오맵 API 로드 시작...');
            // 카카오맵 API 스크립트 로드
            const script = document.createElement('script');
            script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${API_CONFIG.KAKAO_MAP_KEY}&autoload=false`;
            script.onload = function() {
                console.log('=== 카카오맵 API 스크립트 로드 완료 ===');
                console.log('kakao 객체:', typeof kakao);
                console.log('kakao.maps:', typeof kakao.maps);
                
                kakao.maps.load(function() {
                    console.log('=== kakao.maps.load 콜백 실행 ===');
                    createMap();
                });
            };
            script.onerror = function(error) {
                console.error('카카오맵 API 스크립트 로드 실패:', error);
            };
            document.head.appendChild(script);
            console.log('카카오맵 스크립트 태그 추가됨');
        }
    }
    
    // 지도 생성 함수
    function createMap() {
        const container = document.getElementById('map');
        if (!container) {
            console.error('지도 컨테이너를 찾을 수 없습니다');
            return;
        }
        
        const options = {
            center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울시청
            level: 3
        };
        
        const map = new kakao.maps.Map(container, options);
        console.log('=== 카카오맵 생성 완료 ===');
        
        // 현재 위치로 지도 이동
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                const moveLatLon = new kakao.maps.LatLng(position.coords.latitude, position.coords.longitude);
                map.setCenter(moveLatLon);
                map.setLevel(3);
                console.log('=== 현재 위치로 지도 이동 완료 ===');
            }, function(error) {
                console.warn('현재 위치를 가져올 수 없습니다:', error);
            });
        }
        
        // 약국 마커 추가
        addPharmacyMarkers(map);
    }
    
    // 약국 마커 추가 함수 (카카오맵 장소 API 사용)
    async function addPharmacyMarkers(map) {
        try {
            console.log('=== 카카오맵 장소 API로 약국 검색 시작 ===');
            
            // 카카오맵 장소 서비스 초기화
            const kakaoPlaceService = new KakaoPlaceService();
            
            // 현재 위치 기반 약국 검색
            const currentPosition = await getCurrentPosition();
            
            // 카카오맵 장소 API에서 약국 정보 가져오기
            const pharmacies = await kakaoPlaceService.searchNearbyPharmacies(
                currentPosition.latitude, 
                currentPosition.longitude, 
                3000 // 3km 반경
            );
            
            console.log('=== 카카오맵에서 가져온 약국 수:', pharmacies.length);
            
            // 거리 정보가 이미 API에서 제공되므로 그대로 사용
            const pharmaciesWithDistance = pharmacies;
            
            // 지도에 마커 추가
            pharmaciesWithDistance.forEach(function(pharmacy, index) {
                if (pharmacy.latitude && pharmacy.longitude) {
                    const markerPosition = new kakao.maps.LatLng(pharmacy.latitude, pharmacy.longitude);
                    
                    // 마커 이미지 설정 (영업중/마감에 따라)
                    const imageSrc = pharmacy.isOpen ? 
                        '/static/images/icons/pharmacy_open.png' : 
                        '/static/images/icons/pharmacy_closed.png';
                    const imageSize = new kakao.maps.Size(30, 30);
                    const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
                    
                    const marker = new kakao.maps.Marker({
                        position: markerPosition,
                        image: markerImage,
                        title: pharmacy.name
                    });
                    
                    marker.setMap(map);
                    
                    // 마커 클릭 이벤트
                    kakao.maps.event.addListener(marker, 'click', function() {
                        selectPharmacy(pharmacy.pharmacyId);
                    });
                }
            });
            
            // 약국 리스트 업데이트
            updatePharmacyList(pharmaciesWithDistance);
            
            console.log('=== 실제 약국 마커 추가 완료 ===');
            
        } catch (error) {
            console.error('카카오맵 장소 API 호출 실패:', error);
            console.log('=== 더미 데이터로 폴백 ===');
            
            // 에러 발생 시 더미 데이터 사용
            const dummyPharmacies = [
                { name: '건강약국', lat: 37.5665, lng: 126.9780, isOpen: true, pharmacyId: 'pharmacy_001', address: '서울시 강남구 테헤란로 123', phone: '02-1234-5678', distance: '0.3km' },
                { name: '메디팜약국', lat: 37.5655, lng: 126.9790, isOpen: true, pharmacyId: 'pharmacy_002', address: '서울시 강남구 역삼동 456', phone: '02-2345-6789', distance: '0.5km' },
                { name: '웰니스약국', lat: 37.5675, lng: 126.9770, isOpen: false, pharmacyId: 'pharmacy_003', address: '서울시 강남구 논현동 789', phone: '02-3456-7890', distance: '0.8km' }
            ];
            
            addDummyMarkers(map, dummyPharmacies);
        }
    }
    
    // 더미 마커 추가 함수 (폴백용)
    function addDummyMarkers(map, pharmacies) {
        pharmacies.forEach(function(pharmacy, index) {
            const markerPosition = new kakao.maps.LatLng(pharmacy.lat, pharmacy.lng);
            
            const marker = new kakao.maps.Marker({
                position: markerPosition,
                title: pharmacy.name
            });
            
            marker.setMap(map);
            
            kakao.maps.event.addListener(marker, 'click', function() {
                selectPharmacy(pharmacy.pharmacyId);
            });
        });
        
        updatePharmacyList(pharmacies);
        console.log('=== 더미 약국 마커 추가 완료 ===');
    }
    
    // 현재 위치 가져오기
    function getCurrentPosition() {
        return new Promise((resolve, reject) => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        resolve({
                            latitude: position.coords.latitude,
                            longitude: position.coords.longitude
                        });
                    },
                    (error) => {
                        console.warn('현재 위치를 가져올 수 없습니다:', error);
                        resolve({
                            latitude: 37.5665, // 서울시청 기본값
                            longitude: 126.9780
                        });
                    }
                );
            } else {
                resolve({
                    latitude: 37.5665,
                    longitude: 126.9780
                });
            }
        });
    }
    
    // 약국 리스트 업데이트
    function updatePharmacyList(pharmacies) {
        const pharmacyList = document.querySelector('.pharmacy-list');
        if (!pharmacyList) return;
        
        pharmacyList.innerHTML = '';
        
        pharmacies.slice(0, 10).forEach(pharmacy => { // 상위 10개만 표시
            const pharmacyItem = createPharmacyListItem(pharmacy);
            pharmacyList.appendChild(pharmacyItem);
        });
    }
    
    // 약국 리스트 아이템 생성
    function createPharmacyListItem(pharmacy) {
        const item = document.createElement('div');
        item.className = 'pharmacy-list-item';
        item.onclick = () => selectPharmacy(pharmacy.pharmacyId);
        
        item.innerHTML = `
            <div class="pharmacy-info">
                <div class="pharmacy-name">${pharmacy.name}</div>
                <div class="pharmacy-address">${pharmacy.address}</div>
                <div class="pharmacy-distance">${pharmacy.distance}</div>
            </div>
            <div class="pharmacy-status ${pharmacy.isOpen ? 'open' : 'closed'}">
                ${pharmacy.isOpen ? '영업중' : '영업마감'}
            </div>
        `;
        
        return item;
    }
    
    function selectPharmacy(pharmacyId) {
        console.log('=== 약국 선택 시작 ===');
        console.log('선택된 약국 ID:', pharmacyId);
        
        // 약국 데이터에서 해당 약국 찾기
        const pharmacies = [
            <c:forEach var="pharmacy" items="${pharmacies}" varStatus="status">
            {
                pharmacyId: "${pharmacy.pharmacyId}",
                pharmacyName: "${pharmacy.pharmacyName}",
                address: "${pharmacy.address}",
                distance: ${pharmacy.distance},
                phoneNumber: "${pharmacy.phoneNumber}",
                operatingHours: "${pharmacy.operatingHours}",
                isOpen: ${pharmacy.isOpen},
                rating: ${pharmacy.rating}
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        
        console.log('전체 약국 데이터:', pharmacies);
        
        const pharmacy = pharmacies.find(p => p.pharmacyId === pharmacyId);
        console.log('찾은 약국:', pharmacy);
        
        if (pharmacy) {
            console.log('약국 상세 정보 표시 시작');
            console.log('전달할 약국 객체:', pharmacy);
            showPharmacyDetail(pharmacy);
        } else {
            console.log('약국을 찾을 수 없습니다.');
        }
    }
    
    function showPharmacyDetail(pharmacy) {
        console.log('=== 상세 정보 표시 시작 ===');
        console.log('약국 데이터:', pharmacy);
        
        // pharmacy 객체 검증
        if (!pharmacy) {
            console.error('pharmacy 객체가 null 또는 undefined입니다!');
            return;
        }
        
        if (!pharmacy.pharmacyId) {
            console.error('pharmacy.pharmacyId가 없습니다!');
            return;
        }
        
        console.log('약국 ID 확인:', pharmacy.pharmacyId);
        
        // 모달 요소들 확인
        const modalName = document.getElementById('modalPharmacyName');
        const modalAddress = document.getElementById('modalAddress');
        const modalDistance = document.getElementById('modalDistance');
        const modalId = document.getElementById('modalPharmacyId');
        const modal = document.getElementById('pharmacyDetailModal');
        
        console.log('모달 요소들:', {
            modalName: modalName,
            modalAddress: modalAddress,
            modalDistance: modalDistance,
            modalId: modalId,
            modal: modal
        });
        
        if (!modalName || !modalAddress || !modalDistance || !modalId || !modal) {
            console.error('모달 요소를 찾을 수 없습니다!');
            return;
        }
        
        // 약국 이름
        modalName.textContent = pharmacy.pharmacyName;
        console.log('약국 이름 설정:', pharmacy.pharmacyName);
        
        // 영업 상태
        const modalStatus = document.getElementById('modalStatus');
        if (modalStatus) {
            modalStatus.textContent = pharmacy.isOpen ? '영업중' : '영업마감';
            modalStatus.className = pharmacy.isOpen ? 'status-badge open' : 'status-badge closed';
        }
        
        // 영업시간
        const modalHours = document.getElementById('modalHours');
        if (modalHours) {
            modalHours.textContent = pharmacy.operatingHours || '목 08:30-21:00';
        }
        
        // 주소
        modalAddress.textContent = pharmacy.address;
        console.log('약국 주소 설정:', pharmacy.address);
        
        // 거리
        modalDistance.textContent = Math.round(pharmacy.distance * 1000) + 'm';
        console.log('약국 거리 설정:', Math.round(pharmacy.distance * 1000) + 'm');
        
        // 약국 ID (처방전 전달용)
        modalId.value = pharmacy.pharmacyId;
        console.log('약국 ID 설정:', pharmacy.pharmacyId);
        
        // 약국 리스트 오버레이 숨기기
        const pharmacyListOverlay = document.querySelector('.pharmacy-list-overlay');
        if (pharmacyListOverlay) {
            pharmacyListOverlay.style.display = 'none';
        }
        
        // 모달 표시
        console.log('모달 표시 시작');
        modal.style.display = 'block';
        
        // 애니메이션 효과
        setTimeout(() => {
            console.log('모달 애니메이션 시작');
            modal.style.transform = 'translateX(-50%) translateY(0)';
        }, 10);
        
        console.log('=== 상세 정보 표시 완료 ===');
    }
    
    function closePharmacyDetail() {
        const modal = document.getElementById('pharmacyDetailModal');
        modal.style.transform = 'translateX(-50%) translateY(100%)';
        
        // 약국 리스트 오버레이 다시 보이기
        const pharmacyListOverlay = document.querySelector('.pharmacy-list-overlay');
        if (pharmacyListOverlay) {
            pharmacyListOverlay.style.display = 'block';
        }
        
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
    
    function callPharmacy() {
        // 전화 걸기 기능
        const phoneNumber = document.getElementById('modalPhone')?.textContent || '02-1234-5678';
        window.location.href = 'tel:' + phoneNumber;
    }
    
    function copyAddress() {
        // 주소 복사 기능
        const address = document.getElementById('modalAddress')?.textContent || '';
        if (navigator.clipboard) {
            navigator.clipboard.writeText(address).then(() => {
                alert('주소가 클립보드에 복사되었습니다.');
            });
        } else {
            alert('주소: ' + address);
        }
    }
    
    function showDirections(latitude, longitude) {
        // 지도 앱 연동 (향후 구현)
        alert('길찾기 기능은 추후 구현 예정입니다.');
    }
    
    function sharePharmacy() {
        if (navigator.share) {
            navigator.share({
                title: '약국 정보',
                text: '약국 정보를 공유합니다.',
                url: window.location.href
            });
        } else {
            // 클립보드에 복사
            navigator.clipboard.writeText(window.location.href).then(() => {
                alert('링크가 클립보드에 복사되었습니다.');
            });
        }
    }
    
    function sendPrescription() {
        const pharmacyId = document.getElementById('modalPharmacyId').value;
        const pharmacyName = document.getElementById('modalPharmacyName').textContent;
        
        console.log('약국 ID:', pharmacyId);
        console.log('약국 이름:', pharmacyName);
        
        if (!pharmacyId) {
            alert('약국 정보를 찾을 수 없습니다.');
            return;
        }
        
        if (!pharmacyName || pharmacyName.trim() === '') {
            console.error('약국 이름이 비어있습니다!');
            showConfirmModal('약국');
            return;
        }
        
        showConfirmModal(pharmacyName);
    }
    
    function showConfirmModal(pharmacyName) {
        console.log('showConfirmModal 호출됨, 약국 이름:', pharmacyName);
        
        const modal = document.getElementById('confirmModal');
        const message = document.getElementById('confirmMessage');
        
        console.log('모달 요소들:', { modal, message });
        
        if (!modal || !message) {
            console.error('모달 요소를 찾을 수 없습니다!');
            return;
        }
        
        const confirmText = `"${pharmacyName}"에 처방전을 전달하시겠습니까?`;
        console.log('설정할 텍스트:', confirmText);
        
        message.textContent = confirmText;
        modal.style.display = 'flex';
        
        // 애니메이션
        setTimeout(() => {
            modal.style.opacity = '1';
        }, 10);
    }
    
    function closeConfirmModal() {
        const modal = document.getElementById('confirmModal');
        modal.style.opacity = '0';
        
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
    
    function confirmSendPrescription() {
        // 폼을 직접 제출하여 서버에서 리다이렉트 처리
        const form = document.getElementById('sendPrescriptionForm');
        closeConfirmModal();
        form.submit();
    }
</script>
</body>
</html>

