// 조제 현황 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로드 시 실행할 코드
    console.log('Dispensing status page loaded');
});

// 팝업 관련 함수 제거됨

// 수령 완료 처리 - 모달창 사용
let currentDispensingId = null;

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
