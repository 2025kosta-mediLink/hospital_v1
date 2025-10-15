// 처방전 조회 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializePrescriptionList();
});

function initializePrescriptionList() {
    // 체크박스 상태에 따른 약국 찾기 버튼 활성화
    const checkboxes = document.querySelectorAll('input[name="selectedPrescriptions"]');
    const findPharmacyBtn = document.getElementById('findPharmacyBtn');
    
    if (checkboxes.length > 0 && findPharmacyBtn) {
        function updateButtonState() {
            const checkedBoxes = document.querySelectorAll('input[name="selectedPrescriptions"]:checked');
            findPharmacyBtn.disabled = checkedBoxes.length === 0;
        }
        
        checkboxes.forEach(checkbox => {
            checkbox.addEventListener('change', updateButtonState);
        });
        
        // 폼 제출 시 선택된 처방전이 있는지 확인
        const prescriptionForm = document.getElementById('prescriptionForm');
        if (prescriptionForm) {
            prescriptionForm.addEventListener('submit', function(e) {
                const checkedBoxes = document.querySelectorAll('input[name="selectedPrescriptions"]:checked');
                if (checkedBoxes.length === 0) {
                    e.preventDefault();
                    showAlert('처방전을 선택해주세요.');
                }
            });
        }
    }
    
    // 처방전 아이템 클릭 시 체크박스 토글
    const prescriptionItems = document.querySelectorAll('.prescription-item.selectable');
    prescriptionItems.forEach(item => {
        item.addEventListener('click', function(e) {
            // 체크박스나 버튼 클릭이 아닌 경우에만 처리
            if (!e.target.closest('.checkbox-container') && 
                !e.target.closest('.prescription-actions')) {
                const checkbox = item.querySelector('input[type="checkbox"]');
                if (checkbox) {
                    checkbox.checked = !checkbox.checked;
                    checkbox.dispatchEvent(new Event('change'));
                }
            }
        });
    });
}

function viewPrescription(prescriptionId) {
    // 처방전 상세 보기 기능 (향후 구현)
    showAlert('처방전 상세 보기 기능은 추후 구현 예정입니다.');
}

function checkDispensingStatus(prescriptionId) {
    // 조제 상황 확인 기능 (향후 구현)
    showAlert('조제 상황 확인 기능은 추후 구현 예정입니다.');
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
