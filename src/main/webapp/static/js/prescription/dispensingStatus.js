// 조제 현황 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로드 시 실행할 코드
    console.log('Dispensing status page loaded');
    
    // 경로 옵션 이벤트 리스너 추가
    setupRouteOptions();
    
    // 길찾기 지도 초기화
    initializeRouteMap();
    
    // 조제 완료 상태 확인 및 알림 표시
    checkCompletionStatus();
});

// 경로 옵션 설정
function setupRouteOptions() {
    const routeButtons = document.querySelectorAll('.route-option-btn');
    
    routeButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 모든 버튼에서 active 클래스 제거
            routeButtons.forEach(btn => btn.classList.remove('active'));
            
            // 클릭된 버튼에 active 클래스 추가
            this.classList.add('active');
            
            // 경로 타입 설정
            const routeType = this.getAttribute('data-route-type');
            routeService.setRouteType(routeType);
            
            // 경로 다시 그리기
            redrawRoute();
        });
    });
}

// 경로 다시 그리기
async function redrawRoute() {
    try {
        const pharmacyInfo = getPharmacyInfo();
        const startLat = 37.5685;
        const startLng = 126.9672;
        const endLat = pharmacyInfo.lat;
        const endLng = pharmacyInfo.lng;
        
        // 경로 다시 표시
        await routeService.showRoute(startLat, startLng, endLat, endLng);
        
        // 경로 정보 다시 계산
        const routeInfo = await routeService.calculateRouteInfo(startLat, startLng, endLat, endLng);
        displayRouteInfo(routeInfo);
        
    } catch (error) {
        console.error('경로 다시 그리기 실패:', error);
    }
}

// 조제 완료 상태 확인
function checkCompletionStatus() {
    // 2단계가 현재 진행 중인지 확인
    const currentStep = document.querySelector('.step.current .step-title');
    if (currentStep && currentStep.textContent.includes('약을 조제 중입니다')) {
        // 5초 후 2단계를 완료하고 3단계를 활성화
        setTimeout(() => {
            // 2단계를 완료 상태로 변경
            const secondStep = document.getElementById('secondStep');
            const secondStepCircle = secondStep.querySelector('.step-circle');
            secondStepCircle.classList.remove('active');
            secondStepCircle.classList.add('completed');
            secondStep.classList.remove('current');
            secondStep.classList.add('completed');
            
            // 3단계를 활성화
            const completionStep = document.getElementById('completionStep');
            const stepTitle = completionStep.querySelector('.step-title');
            const stepCircle = completionStep.querySelector('.step-circle');
            
            stepTitle.textContent = '조제가 완료되었습니다. (14:20)';
            stepCircle.classList.add('completed');
            completionStep.classList.add('current');
            
            // 알림 모달 표시
            showCompletionNotification();
        }, 5000); // 5초 후 상태 변경 및 알림 표시
    }
}

// 길찾기 지도 초기화
async function initializeRouteMap() {
    try {
        console.log('길찾기 지도 초기화 시작...');
        
        // 약국 정보 가져오기 (세션에서 또는 하드코딩된 테스트 데이터)
        const pharmacyInfo = getPharmacyInfo();
        console.log('약국 정보:', pharmacyInfo);
        
        // 약국명 표시
        document.getElementById('pharmacyName').textContent = pharmacyInfo.name;
        
        // 강북삼성병원 좌표 (출발지)
        const startLat = 37.5685;
        const startLng = 126.9672;
        
        // 약국 좌표 (도착지)
        const endLat = pharmacyInfo.lat;
        const endLng = pharmacyInfo.lng;
        
        console.log('출발지:', startLat, startLng);
        console.log('도착지:', endLat, endLng);
        
        // 카카오 지도 API 로드
        console.log('카카오 지도 API 로드 중...');
        await routeService.loadKakaoMapScript();
        console.log('카카오 지도 API 로드 완료');
        
        // 지도 초기화 및 길찾기
        console.log('지도 초기화 중...');
        await routeService.initializeMap('routeMap', startLat, startLng, endLat, endLng);
        console.log('지도 초기화 완료');
        
        // 경로 정보 계산 (실제 도보 경로 또는 직선거리)
        try {
            // 먼저 실제 경로 정보를 시도
            const routeInfo = await routeService.calculateRouteInfo(startLat, startLng, endLat, endLng);
            displayRouteInfo(routeInfo);
            console.log('실제 경로 정보 표시 완료');
        } catch (routeInfoError) {
            console.error('실제 경로 정보 계산 실패, 직선거리로 대체:', routeInfoError);
            // 실패 시 간단한 직선거리 계산
            const distance = calculateDistance(startLat, startLng, endLat, endLng);
            const estimatedTime = Math.round(distance / 80); // 대략적인 도보 시간 (분당 80m)
            
            displaySimpleRouteInfo(distance, estimatedTime);
            console.log('간단한 경로 정보 표시 완료 (폴백)');
        }
        
        console.log('길찾기 지도 초기화 완료');
        
    } catch (error) {
        console.error('길찾기 지도 초기화 실패:', error);
        showRouteError();
    }
}

// 약국 정보 가져오기 (세션에서 또는 테스트 데이터)
function getPharmacyInfo() {
    // JSP에서 전달된 약국 정보 확인
    const pharmacyInfoElement = document.querySelector('[data-pharmacy-info]');
    
    console.log('약국 정보 엘리먼트:', pharmacyInfoElement);
    console.log('모든 data-pharmacy-info 엘리먼트:', document.querySelectorAll('[data-pharmacy-info]'));
    
    if (pharmacyInfoElement) {
        const rawData = pharmacyInfoElement.getAttribute('data-pharmacy-info');
        console.log('세션에서 가져온 원본 데이터:', rawData);
        console.log('원본 데이터 타입:', typeof rawData);
        console.log('원본 데이터 길이:', rawData ? rawData.length : 'null');
        
        try {
            const pharmacyInfo = JSON.parse(rawData);
            console.log('세션에서 가져온 약국 정보:', pharmacyInfo);
            return pharmacyInfo;
        } catch (e) {
            console.error('약국 정보 파싱 오류:', e);
            console.error('파싱 실패한 데이터:', rawData);
        }
    }
    
    // 세션 정보가 없으면 에러
    console.error('세션에서 약국 정보를 찾을 수 없습니다.');
    throw new Error('약국 정보를 찾을 수 없습니다. 약국을 다시 선택해주세요.');
}

// 간단한 거리 계산 (직선거리)
function calculateDistance(lat1, lng1, lat2, lng2) {
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

// 간단한 경로 정보 표시
function displaySimpleRouteInfo(distance, estimatedTime) {
    const distanceElement = document.getElementById('routeDistance');
    const durationElement = document.getElementById('routeDuration');
    
    if (distanceElement && durationElement) {
        // 거리 표시 (미터를 적절한 단위로 변환)
        if (distance < 1000) {
            distanceElement.textContent = `${Math.round(distance)}m`;
        } else {
            distanceElement.textContent = `${(distance / 1000).toFixed(1)}km`;
        }
        
        // 시간 표시
        durationElement.textContent = `${estimatedTime}분`;
    }
}

// 기존 함수 (호환성 유지)
function displayRouteInfo(routeInfo) {
    const distanceElement = document.getElementById('routeDistance');
    const durationElement = document.getElementById('routeDuration');
    
    if (distanceElement && durationElement) {
        // 거리 표시 (미터를 킬로미터로 변환)
        const distanceKm = (routeInfo.distance / 1000).toFixed(1);
        distanceElement.textContent = `${distanceKm}km`;
        
        // 시간 표시 (초를 분으로 변환)
        const durationMin = Math.round(routeInfo.duration / 60);
        durationElement.textContent = `${durationMin}분`;
    }
}

// 길찾기 오류 시 표시
function showRouteError() {
    const routeMap = document.getElementById('routeMap');
    if (routeMap) {
        routeMap.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: #f5f5f5; color: #666;">
                <div style="text-align: center;">
                    <div style="font-size: 24px; margin-bottom: 8px;">🗺️</div>
                    <div>길찾기 지도를 불러올 수 없습니다.</div>
                </div>
            </div>
        `;
    }
    
    // 경로 정보도 오류 표시
    document.getElementById('routeDistance').textContent = '오류';
    document.getElementById('routeDuration').textContent = '오류';
}

// 팝업 관련 함수 제거됨

// 수령 완료 처리 - 모달창 사용
// let currentDispensingId = null; // 중복 선언 방지

function completeReceipt(dispensingId) {
    currentDispensingId = dispensingId;
    showReceiptConfirmModal();
}

function showReceiptConfirmModal() {
    const modal = document.getElementById('receiptConfirmModal');
    if (modal) {
        modal.style.display = 'flex';
        
        // 애니메이션
        setTimeout(() => {
            modal.style.opacity = '1';
        }, 10);
    }
}

function closeReceiptConfirmModal() {
    const modal = document.getElementById('receiptConfirmModal');
    if (modal) {
        modal.style.opacity = '0';
        
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
}

function confirmReceiptComplete() {
    if (!currentDispensingId) return;
    
    fetch(window.location.origin + '/v1/dispensing/complete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'dispensingId=' + currentDispensingId
    })
    .then(response => {
        if (response.ok) {
            window.location.href = window.location.origin + '/v1/prescription?completed=true&dispensingId=' + currentDispensingId;
        } else {
            alert('수령 완료 처리 중 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('수령 완료 처리 중 오류가 발생했습니다.');
    });
}

// 조제 완료 알림 모달 표시
function showCompletionNotification() {
    const modal = document.getElementById('completionNotificationModal');
    if (modal) {
        modal.style.display = 'flex';
        
        // 애니메이션
        setTimeout(() => {
            modal.style.opacity = '1';
        }, 10);
    }
}

// 조제 완료 알림 모달 닫기
function closeCompletionNotification() {
    const modal = document.getElementById('completionNotificationModal');
    if (modal) {
        modal.style.opacity = '0';
        
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
}
