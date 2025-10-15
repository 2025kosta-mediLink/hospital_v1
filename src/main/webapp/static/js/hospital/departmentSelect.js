function searchDepartments() {
    const searchTerm = document.getElementById('searchTerm').value.trim();
    const currentPath = window.location.pathname;

    if (searchTerm) {
        if (currentPath.includes('/reservation/')) {
            window.location.href = "/v1/reservation/departments?searchTerm=" + encodeURIComponent(searchTerm);
        } else if (currentPath.includes('/reception/')) {
            window.location.href = "/v1/reception/departments?searchTerm=" + encodeURIComponent(searchTerm);
        }
    } else {
        window.location.href = currentPath; // 검색어가 없으면 그냥 현재 페이지로 리다이렉트
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchTerm');

    // Enter 키 입력 시 검색
    searchInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            searchDepartments();
        }
    });

    // '다음' 버튼 비활성화/활성화 처리
    const nextBtn = document.getElementById('nextBtn');
    const radios = document.querySelectorAll('input[name="departmentId"]');
    radios.forEach(radio => {
        radio.addEventListener('change', function () {
            nextBtn.disabled = false; // 진료과 선택 시 버튼 활성화
        });
    });

    // '다음' 버튼 클릭 시 리다이렉트 처리
    document.getElementById('deptForm').onsubmit = function (event) {
        event.preventDefault(); // 폼 제출 막기 (리다이렉트만 처리하도록)

        const selectedRadio = document.querySelector('input[name="departmentId"]:checked');
        const deptId = selectedRadio ? selectedRadio.value : null;
        if (!deptId) {
            alert("진료과를 선택해주세요.");
            return; // 선택되지 않은 경우 리다이렉트하지 않음
        }

        // 페이지 분기
        const currentPath = window.location.pathname;
        if (currentPath.includes('/reservation/')) {
            window.location.href = "/v1/reservation/doctors?departmentId=" + deptId;
        } else if (currentPath.includes('/reception/')) {
            window.location.href = "/v1/reception/doctors?departmentId=" + deptId;
        } else {
            alert("잘못된 요청입니다.");
        }
    };
});
