// 약국 검색 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializePharmacySearch();
});

function initializePharmacySearch() {
    // 약국 아이템 클릭 이벤트
    const pharmacyItems = document.querySelectorAll('.pharmacy-item');
    pharmacyItems.forEach(item => {
        item.addEventListener('click', function(e) {
            // 버튼 클릭이 아닌 경우에만 상세 정보 표시
            if (!e.target.closest('.pharmacy-actions')) {
                showPharmacyDetail(this);
            }
        });
    });
    
    // 현재 위치 기반 약국 검색 (실제 구현에서는 Geolocation API 사용)
    requestLocationPermission();
}

function showPharmacyDetail(element) {
    const pharmacyId = element.dataset.pharmacyId;
    const pharmacyName = element.querySelector('.pharmacy-name').textContent;
    const address = element.querySelector('.address').textContent;
    const phone = element.querySelector('.phone').textContent;
    const hours = element.querySelector('.hours').textContent;
    const distance = element.querySelector('.distance').textContent;
    
    // 모달에 정보 설정
    document.getElementById('modalPharmacyName').textContent = pharmacyName;
    document.getElementById('modalAddress').textContent = address;
    document.getElementById('modalPhone').textContent = phone;
    document.getElementById('modalHours').textContent = hours;
    document.getElementById('modalDistance').textContent = distance;
    document.getElementById('modalPharmacyId').value = pharmacyId;
    
    // 모달 표시
    const modal = document.getElementById('pharmacyDetailModal');
    modal.style.display = 'flex';
    
    // 모달 외부 클릭 시 닫기
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            closePharmacyDetail();
        }
    });
}

function closePharmacyDetail() {
    document.getElementById('pharmacyDetailModal').style.display = 'none';
}

function callPharmacy(phoneNumber) {
    // 전화 걸기 기능
    window.location.href = 'tel:' + phoneNumber;
}

function showMap(latitude, longitude) {
    // 지도 앱 연동 (향후 구현)
    showAlert('지도 앱 연동 기능은 추후 구현 예정입니다.');
    
    // 실제 구현 예시:
    // - 네이버 지도: 'nmap://route/car?dlat=' + latitude + '&dlng=' + longitude
    // - 카카오맵: 'kakaomap://route?ep=' + latitude + ',' + longitude
    // - 구글맵: 'https://maps.google.com/maps?daddr=' + latitude + ',' + longitude
}

function requestLocationPermission() {
    // 위치 권한 요청 (실제 구현에서는 사용)
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                // 위치 정보를 받았을 때의 처리
                console.log('현재 위치:', position.coords.latitude, position.coords.longitude);
                // 실제로는 이 위치 정보를 서버에 전송하여 약국 검색
            },
            function(error) {
                console.log('위치 정보를 가져올 수 없습니다:', error.message);
                // 기본 위치 사용 (서울시청)
            }
        );
    }
}

function showAlert(message) {
    // 간단한 알림 표시
    if (window.confirm) {
        alert(message);
    } else {
        // 모바일 환경을 위한 대체 알림
        const alertDiv = document.createElement('div');
        alertDiv.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(0, 0, 0, 0.8);
            color: white;
            padding: 16px 24px;
            border-radius: 8px;
            z-index: 10000;
            font-size: 14px;
        `;
        alertDiv.textContent = message;
        document.body.appendChild(alertDiv);
        
        setTimeout(() => {
            document.body.removeChild(alertDiv);
        }, 2000);
    }
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closePharmacyDetail();
    }
});
