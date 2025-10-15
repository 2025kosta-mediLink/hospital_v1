// 약국 검색 페이지 JavaScript
console.log('=== 약국 검색 JavaScript 로드됨 ===');

// 전역 변수
let mapService = null;
let currentPharmacies = [];

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== DOM 로드 완료 ===');
    initializeServices();
    initializeMap();
});

// 서비스 초기화
function initializeServices() {
    mapService = new KakaoMapService();
    console.log('서비스 초기화 완료');
}

// 지도 초기화 (카카오 API 사용)
function initializeMap() {
    console.log('카카오 지도 초기화 시작...');
    console.log('JavaScript 키:', '371a027cd1dac68dce2424d2ac0fd3ca');
    
    // 카카오 지도 API 로드 확인
    if (window.kakao && window.kakao.maps) {
        console.log('카카오 지도 API 이미 로드됨');
        createKakaoMap();
    } else {
        console.log('카카오 지도 API 로드 대기 중...');
        
        // 카카오 지도 API 로드 대기
        const checkInterval = setInterval(() => {
            if (window.kakao && window.kakao.maps) {
                clearInterval(checkInterval);
                console.log('카카오 지도 API 로드 완료!');
                createKakaoMap();
            } else {
                console.log('카카오 지도 API 대기 중...');
            }
        }, 100);
        
        // 5초 후 타임아웃
        setTimeout(() => {
            clearInterval(checkInterval);
            if (!window.kakao || !window.kakao.maps) {
                console.log('카카오 지도 API 로드 타임아웃, 대체 화면 표시');
                showPharmacySearchInterface();
            }
        }, 5000);
    }
}

// 카카오 지도 생성 (성공 시)
function createKakaoMap() {
    try {
        console.log('카카오 지도 생성 시작...');
        
        // 강북삼성병원 외래동을 기본 중심으로 설정 (정확한 좌표)
        const center = new kakao.maps.LatLng(37.5685, 126.9672); // 강북삼성병원 외래동 (스타벅스와 구분)
        
        // 지도 생성
        const mapContainer = document.getElementById('map');
        const mapOption = {
            center: center,
            level: 4 // 3km 반경에 적합한 줌 레벨
        };
        
        const map = new kakao.maps.Map(mapContainer, mapOption);
        
        // mapService 초기화
        mapService.map = map;
        
        // 강북삼성병원에 내 현위치 마커 추가
        addHospitalLocationMarker();
        
        // 지도 클릭 이벤트 추가
        addMapClickEvent();
        
        // 현재 위치 버튼 추가
        addCurrentLocationButton();
        
        console.log('카카오 지도 생성 완료!');
        
        // 약국 검색 시작
        setTimeout(() => {
            loadPharmacies();
        }, 1000);
        
    } catch (error) {
        console.error('카카오 지도 생성 실패:', error);
        showPharmacySearchInterface();
    }
}

// 약국 데이터 로드 (카카오 REST API 사용)
async function loadPharmacies() {
    try {
        console.log('카카오 REST API로 약국 검색 시작...');
        
        if (mapService && mapService.map) {
            // 현재 지도 중심 좌표 가져오기
            const center = mapService.map.getCenter();
            const lat = center.getLat();
            const lng = center.getLng();
            
                console.log('강북삼성병원 주변 약국 검색 (정확한 위치):', lat, lng);
            
            // 카카오 REST API로 약국 검색
            const pharmacies = await mapService.searchPharmaciesWithKakao('약국', lat, lng);
            
            if (pharmacies && pharmacies.length > 0) {
                currentPharmacies = pharmacies;
                
                // 지도에 마커 표시
                mapService.addPharmacyMarkers(pharmacies);
                
                // 약국 리스트 업데이트
                updatePharmacyList(pharmacies);
                
                console.log('카카오 API 약국 검색 완료:', pharmacies.length + '개');
            } else {
                console.log('카카오 API 약국 검색 결과가 없습니다.');
                showNoPharmaciesMessage();
            }
        } else {
            console.log('지도가 초기화되지 않았습니다.');
            showNoPharmaciesMessage();
        }
    } catch (error) {
        console.error('약국 검색 오류:', error);
        showNoPharmaciesMessage();
    }
}

// 약국 리스트 업데이트
function updatePharmacyList(pharmacies) {
    const pharmacyList = document.querySelector('.pharmacy-list');
    if (!pharmacyList) return;

    pharmacyList.innerHTML = '';
    
    if (pharmacies && pharmacies.length > 0) {
        pharmacies.forEach(pharmacy => {
            const pharmacyItem = createPharmacyItem(pharmacy);
            pharmacyList.appendChild(pharmacyItem);
        });
    } else {
        showNoPharmaciesMessage();
    }
}

// 약국 아이템 생성
function createPharmacyItem(pharmacy) {
    const item = document.createElement('div');
    item.className = 'pharmacy-list-item';
    item.setAttribute('data-pharmacy-id', pharmacy.id);
    item.onclick = () => selectPharmacy(pharmacy.id);
    
    const statusClass = pharmacy.isOpen ? 'open' : 'closed';
    const statusText = pharmacy.isOpen ? '영업중' : '영업마감';
    
    item.innerHTML = `
        <div class="pharmacy-info">
            <div class="pharmacy-name">${pharmacy.name}</div>
            <div class="pharmacy-distance">${Math.round(pharmacy.distance)}m</div>
            <div class="pharmacy-address">${pharmacy.address}</div>
        </div>
        <div class="pharmacy-status">
            <span class="status-badge ${statusClass}">${statusText}</span>
        </div>
    `;
    
    return item;
}

// 약국 검색 실패 시 처리
function showNoPharmaciesMessage() {
    const pharmacyList = document.querySelector('.pharmacy-list');
    if (pharmacyList) {
        pharmacyList.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🏥</div>
                <div class="empty-text">주변에 약국이 없습니다.</div>
                <div class="empty-subtext">다른 위치를 선택해보세요.</div>
            </div>
        `;
    }
}

// 약국 찾기 인터페이스 표시 (지도 로드 실패 시)
function showPharmacySearchInterface() {
    const mapContainer = document.getElementById('map');
    if (mapContainer) {
        mapContainer.innerHTML = `
            <div style="width: 100%; height: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; flex-direction: column; color: white; position: relative;">
                <div style="font-size: 64px; margin-bottom: 20px;">🏥</div>
                <div style="font-size: 24px; font-weight: 700; margin-bottom: 15px;">약국 찾기</div>
                <div style="font-size: 16px; text-align: center; opacity: 0.9; line-height: 1.5; margin-bottom: 30px;">
                    강북삼성병원 주변 약국을 검색합니다<br>
                    아래 버튼을 클릭하여 약국을 찾아보세요
                </div>
            </div>
        `;
    }
}

// 지도 클릭 이벤트 추가
function addMapClickEvent() {
    if (mapService && mapService.map) {
        kakao.maps.event.addListener(mapService.map, 'click', function(mouseEvent) {
            const latlng = mouseEvent.latLng;
            const lat = latlng.getLat();
            const lng = latlng.getLng();
            
            console.log('지도 클릭:', lat, lng);
            
            // 클릭한 위치 주변 약국 검색
            searchPharmaciesAtLocation(lat, lng);
        });
    }
}

// 특정 위치에서 약국 검색 (카카오 API 사용)
async function searchPharmaciesAtLocation(lat, lng) {
    try {
        if (mapService) {
            console.log('클릭한 위치에서 약국 검색:', lat, lng);
            
            // 카카오 REST API로 약국 검색
            const pharmacies = await mapService.searchPharmaciesWithKakao('약국', lat, lng);
            
            if (pharmacies && pharmacies.length > 0) {
                currentPharmacies = pharmacies;
                
                // 지도에 마커 표시
                mapService.addPharmacyMarkers(pharmacies);
                
                // 약국 리스트 업데이트
                updatePharmacyList(pharmacies);
                
                console.log('클릭 위치 약국 검색 완료:', pharmacies.length + '개');
            } else {
                console.log('해당 위치에서 약국을 찾을 수 없습니다.');
                showNoPharmaciesMessage();
            }
        }
    } catch (error) {
        console.error('위치 기반 약국 검색 실패:', error);
    }
}

// 현재 위치 버튼 클릭 이벤트
function addCurrentLocationButton() {
    const currentLocationBtn = document.createElement('button');
    currentLocationBtn.innerHTML = '📍';
    currentLocationBtn.className = 'current-location-btn';
    currentLocationBtn.onclick = async () => {
        try {
            // 현재 위치 가져오기
            const userLocation = await getCurrentLocation();
            if (userLocation) {
                // 지도 중심을 현재 위치로 이동
                mapService.setCenter(userLocation.getLat(), userLocation.getLng());
                
                // 현재 위치에서 약국 검색
                searchPharmaciesAtLocation(userLocation.getLat(), userLocation.getLng());
                
                console.log('현재 위치로 이동 및 약국 검색 완료');
            } else {
                alert('현재 위치를 가져올 수 없습니다. 위치 권한을 허용해주세요.');
            }
        } catch (error) {
            console.error('현재 위치 버튼 클릭 오류:', error);
            alert('위치 정보를 가져오는 중 오류가 발생했습니다.');
        }
    };
    
    // 지도 컨테이너에 버튼 추가
    const mapContainer = document.querySelector('.map-container');
    if (mapContainer) {
        mapContainer.appendChild(currentLocationBtn);
    }
}

// 현재 위치 가져오기
function getCurrentLocation() {
    return new Promise((resolve, reject) => {
        if (!navigator.geolocation) {
            console.warn('Geolocation이 지원되지 않습니다.');
            resolve(null);
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                console.log('현재 위치:', lat, lng);
                resolve(new kakao.maps.LatLng(lat, lng));
            },
            (error) => {
                console.warn('위치 정보를 가져올 수 없습니다:', error.message);
                resolve(null); // 에러 시 null 반환 (기본 위치 사용)
            },
            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 300000 // 5분
            }
        );
    });
}

// 강북삼성병원 위치 마커 추가
function addHospitalLocationMarker() {
    if (mapService && mapService.map) {
        // 강북삼성병원 외래동 좌표 (정확한 위치)
        const hospitalPosition = new kakao.maps.LatLng(37.5685, 126.9672);
        
        // 내 현위치 마커 이미지 생성
        const markerImage = new kakao.maps.MarkerImage(
            'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAiIGhlaWdodD0iMzAiIHZpZXdCb3g9IjAgMCAzMCAzMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTUiIGN5PSIxNSIgcj0iMTUiIGZpbGw9IiM0Q0FGNTIiLz4KPGNpcmNsZSBjeD0iMTUiIGN5PSIxNSIgcj0iOCIgZmlsbD0id2hpdGUiLz4KPGNpcmNsZSBjeD0iMTUiIGN5PSIxNSIgcj0iNCIgZmlsbD0iIzRDQUY1MiIvPgo8L3N2Zz4K',
            new kakao.maps.Size(30, 30)
        );
        
        // 마커 생성
        const hospitalMarker = new kakao.maps.Marker({
            position: hospitalPosition,
            image: markerImage,
            title: '강북삼성병원 (내 현위치)'
        });
        
        // 마커를 지도에 표시
        hospitalMarker.setMap(mapService.map);
        
        // 인포윈도우 생성
        const infowindow = new kakao.maps.InfoWindow({
            content: '<div style="padding:5px; font-size:12px; text-align:center;"><strong>강북삼성병원</strong><br/>내 현위치</div>'
        });
        
        // 마커 클릭 시 인포윈도우 표시
        kakao.maps.event.addListener(hospitalMarker, 'click', function() {
            infowindow.open(mapService.map, hospitalMarker);
        });
        
        console.log('강북삼성병원 위치 마커 추가 완료');
    }
}

// 약국 선택 함수 (리스트와 지도 연동)
function selectPharmacy(pharmacyId) {
    console.log('약국 선택:', pharmacyId);
    
    // 선택된 약국 찾기
    const pharmacy = currentPharmacies.find(p => p.id === pharmacyId);
    if (!pharmacy) {
        console.error('선택된 약국을 찾을 수 없습니다:', pharmacyId);
        return;
    }
    
    // 리스트에서 해당 약국 하이라이트
    highlightPharmacyInList(pharmacyId);
    
    // 지도에서 해당 약국으로 이동 및 줌
    focusPharmacyOnMap(pharmacy);
    
    // 약국 상세 정보 모달 표시
    showPharmacyDetailModal(pharmacy);
}

// 리스트에서 약국 하이라이트
function highlightPharmacyInList(pharmacyId) {
    // 모든 약국 아이템에서 하이라이트 제거
    const allItems = document.querySelectorAll('.pharmacy-list-item');
    allItems.forEach(item => {
        item.classList.remove('selected');
    });
    
    // 선택된 약국 아이템에 하이라이트 추가
    const selectedItem = document.querySelector(`[data-pharmacy-id="${pharmacyId}"]`);
    if (selectedItem) {
        selectedItem.classList.add('selected');
        selectedItem.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

// 지도에서 약국으로 포커스
function focusPharmacyOnMap(pharmacy) {
    if (mapService && mapService.map) {
        const pharmacyPosition = new kakao.maps.LatLng(pharmacy.lat, pharmacy.lng);
        
        // 지도 중심을 약국 위치로 이동
        mapService.map.setCenter(pharmacyPosition);
        
        // 줌 레벨을 높여서 약국을 더 크게 보여주기
        mapService.map.setLevel(2);
        
        // 마커 강조 기능 제거 - 간단하게 지도만 이동
        
        console.log('지도에서 약국으로 포커스:', pharmacy.name);
    }
}

// 약국 마커 강조 표시
function highlightPharmacyMarker(selectedPharmacy) {
    if (mapService && mapService.markers) {
        console.log('약국 마커 강조 시작:', selectedPharmacy.name);
        console.log('전체 마커 수:', mapService.markers.length);
        
        // 모든 마커를 기본 상태로 되돌리기
        mapService.markers.forEach(marker => {
            if (marker.pharmacy) {
                const defaultImageSize = new kakao.maps.Size(30, 30);
                const defaultImage = new kakao.maps.MarkerImage(
                    marker.pharmacy.isOpen ? 
                        '/static/images/icons/pharmacy_open.png' : 
                        '/static/images/icons/pharmacy_closed.png',
                    defaultImageSize
                );
                marker.setImage(defaultImage);
            }
        });
        
        // 선택된 약국 마커 강조
        const selectedMarker = mapService.markers.find(marker => 
            marker.pharmacy && marker.pharmacy.id === selectedPharmacy.id
        );
        
        if (selectedMarker) {
            // 선택된 마커 크기 증가 + 색상 변경
            const highlightedImageSize = new kakao.maps.Size(45, 45);
            
            // 선택된 마커용 특별한 이미지 (빨간색 테두리)
            const highlightedImageData = selectedPharmacy.isOpen ? 
                'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDUiIGhlaWdodD0iNDUiIHZpZXdCb3g9IjAgMCA0NSA0NSIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjIuNSIgY3k9IjIyLjUiIHI9IjIyLjUiIGZpbGw9IiNFRjQ0NDQiLz4KPGNpcmNsZSBjeD0iMjIuNSIgY3k9IjIyLjUiIHI9IjE4LjUiIGZpbGw9IndoaXRlIi8+CjxjaXJjbGUgY3g9IjIyLjUiIGN5PSIyMi41IiByPSIxNCIgZmlsbD0iIzRDQUY1MiIvPgo8L3N2Zz4K' :
                'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDUiIGhlaWdodD0iNDUiIHZpZXdCb3g9IjAgMCA0NSA0NSIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjIuNSIgY3k9IjIyLjUiIHI9IjIyLjUiIGZpbGw9IiNFRjQ0NDQiLz4KPGNpcmNsZSBjeD0iMjIuNSIgY3k9IjIyLjUiIHI9IjE4LjUiIGZpbGw9IndoaXRlIi8+CjxjaXJjbGUgY3g9IjIyLjUiIGN5PSIyMi41IiByPSIxNCIgZmlsbD0iIzZCNzI4MCIvPgo8L3N2Zz4K';
            
            const highlightedImage = new kakao.maps.MarkerImage(
                highlightedImageData,
                highlightedImageSize
            );
            selectedMarker.setImage(highlightedImage);
            
            console.log('약국 마커 강조 표시 완료:', selectedPharmacy.name);
        } else {
            console.log('선택된 약국 마커를 찾을 수 없습니다:', selectedPharmacy.name);
        }
    }
}

// 약국 상세 정보 모달 표시
function showPharmacyDetailModal(pharmacy) {
    const modal = document.getElementById('pharmacyDetailModal');
    if (!modal) return;
    
    // 약국 리스트 모달창 숨기기
    const pharmacyListOverlay = document.querySelector('.pharmacy-list-overlay');
    if (pharmacyListOverlay) {
        pharmacyListOverlay.style.display = 'none';
    }
    
    // 모달 내용 업데이트
    document.getElementById('modalPharmacyName').textContent = pharmacy.name;
    document.getElementById('modalAddress').textContent = pharmacy.address;
    document.getElementById('modalDistance').textContent = Math.round(pharmacy.distance) + 'm';
    document.getElementById('modalPharmacyId').value = pharmacy.id;
    
    // 영업 상태 업데이트
    const statusElement = document.getElementById('modalStatus');
    if (pharmacy.isOpen) {
        statusElement.textContent = '영업중';
        statusElement.className = 'status-badge open';
    } else {
        statusElement.textContent = '영업마감';
        statusElement.className = 'status-badge closed';
    }
    
    // 모달 표시
    modal.style.display = 'block';
    
    // 드래그 바 클릭 이벤트 추가
    addModalCloseFunctionality();
    
    // 약국 정보를 세션에 저장 (길찾기용)
    savePharmacyInfoToSession(pharmacy);
    
    // 선택한 약국만 지도에 표시
    showOnlySelectedPharmacy(pharmacy);
}

// 약국 상세 정보 모달 닫기
function closePharmacyDetailModal() {
    const modal = document.getElementById('pharmacyDetailModal');
    if (modal) {
        modal.style.display = 'none';
    }
    
    // 약국 리스트 모달창 다시 표시
    const pharmacyListOverlay = document.querySelector('.pharmacy-list-overlay');
    if (pharmacyListOverlay) {
        pharmacyListOverlay.style.display = 'block';
    }
    
    // 모든 약국 마커 다시 표시
    showAllPharmacies();
}

// 모달 닫기 기능 추가 (드래그 바 클릭)
function addModalCloseFunctionality() {
    const modalHandle = document.querySelector('.modal-handle');
    
    if (modalHandle) {
        // 기존 이벤트 리스너 제거
        modalHandle.replaceWith(modalHandle.cloneNode(true));
        const newModalHandle = document.querySelector('.modal-handle');
        
        // 클릭 이벤트 추가
        newModalHandle.addEventListener('click', function() {
            console.log('드래그 바 클릭 - 모달 닫기');
            closePharmacyDetailModal();
        });
        
        console.log('모달 닫기 기능 추가 완료');
    }
}

// 약국에 전화하기
function callPharmacy() {
    // 전화 기능은 모바일에서만 작동
    alert('전화 기능은 모바일에서만 사용할 수 있습니다.');
}

// 주소 복사하기
function copyAddress() {
    const addressElement = document.getElementById('modalAddress');
    if (addressElement) {
        const address = addressElement.textContent;
        navigator.clipboard.writeText(address).then(() => {
            alert('주소가 클립보드에 복사되었습니다.');
        }).catch(() => {
            alert('주소 복사에 실패했습니다.');
        });
    }
}

// 확인 모달 닫기
function closeConfirmModal() {
    const modal = document.getElementById('confirmModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// 처방전 전달 확인
function confirmSendPrescription() {
    // 폼을 직접 제출하여 서버에서 리다이렉트 처리
    const form = document.getElementById('sendPrescriptionForm');
    closeConfirmModal();
    form.submit();
}

// 약국 정보를 세션에 저장
function savePharmacyInfoToSession(pharmacy) {
    const pharmacyInfo = {
        id: pharmacy.id,
        name: pharmacy.name,
        address: pharmacy.address,
        lat: pharmacy.lat,
        lng: pharmacy.lng,
        distance: pharmacy.distance,
        isOpen: pharmacy.isOpen
    };
    
    // 서버에 약국 정보 저장 요청
    console.log('약국 정보 세션 저장 요청:', pharmacyInfo);
    
    fetch('/v1/pharmacy/save-info', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(pharmacyInfo)
    })
    .then(response => {
        console.log('약국 정보 저장 응답 상태:', response.status);
        if (response.ok) {
            console.log('약국 정보가 세션에 저장되었습니다:', pharmacy.name);
            return response.json();
        } else {
            console.error('약국 정보 저장 실패:', response.status);
            throw new Error('약국 정보 저장 실패');
        }
    })
    .then(data => {
        console.log('약국 정보 저장 응답 데이터:', data);
    })
    .catch(error => {
        console.error('약국 정보 저장 오류:', error);
    });
}

// 선택한 약국만 지도에 표시
function showOnlySelectedPharmacy(selectedPharmacy) {
    if (mapService && mapService.markers) {
        console.log('선택한 약국만 표시:', selectedPharmacy.name);
        
        // 모든 마커 숨기기
        mapService.markers.forEach(marker => {
            marker.setMap(null);
        });
        
        // 선택한 약국 마커만 표시
        const selectedMarker = mapService.markers.find(marker => 
            marker.pharmacy && marker.pharmacy.id === selectedPharmacy.id
        );
        
        if (selectedMarker) {
            // 선택한 약국 위치로 지도 중심 이동
            const pharmacyPosition = new kakao.maps.LatLng(selectedPharmacy.lat, selectedPharmacy.lng);
            mapService.map.setCenter(pharmacyPosition);
            mapService.map.setLevel(3); // 줌 레벨 높이기 (현위치와 약국이 둘 다 보이도록)
            
            // 선택한 약국 마커만 지도에 표시 (원래 마커 그대로)
            selectedMarker.setMap(mapService.map);
            
            console.log('선택한 약국만 표시 완료:', selectedPharmacy.name);
        } else {
            console.log('선택한 약국 마커를 찾을 수 없습니다:', selectedPharmacy.name);
        }
    }
}

// 모든 약국 마커 다시 표시
function showAllPharmacies() {
    if (mapService && mapService.markers) {
        console.log('모든 약국 마커 다시 표시');
        
        // 모든 마커를 지도에 다시 표시 (원래 상태 그대로)
        mapService.markers.forEach(marker => {
            marker.setMap(mapService.map);
        });
        
        // 지도 중심을 원래 위치로 되돌리기 (강북삼성병원)
        const center = new kakao.maps.LatLng(37.5685, 126.9672);
        mapService.map.setCenter(center);
        mapService.map.setLevel(4); // 원래 줌 레벨로 되돌리기
        
        console.log('모든 약국 마커 표시 완료');
    }
}