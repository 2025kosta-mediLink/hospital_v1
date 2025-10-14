// 진료과 검색 처리
function searchDepartments() {
    const searchTerm = document.getElementById('searchTerm').value.trim();
    if (searchTerm) {
        window.location.href = "/v1/reservation/departments?searchTerm=" + encodeURIComponent(searchTerm);
    } else {
        window.location.href = "/v1/reservation/departments"; // 검색어가 없으면 그냥 모든 진료과로 리다이렉트
    }
}

// Enter 키로 검색 처리
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchTerm');

    // Enter 키 입력 시 검색
    searchInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            searchDepartments();
        }
    });

    // 선택된 진료과가 없다면 '다음' 버튼 비활성화
    const radios = document.querySelectorAll('input[name="departmentId"]');
    radios.forEach(radio => {
        radio.addEventListener('change', function () {
            nextBtn.disabled = false; // 진료과 선택 시 버튼 활성화
        });
    });
});

// 진료과 선택 시, 해당 진료과 ID를 의료진 선택 페이지로 전달
function submitForm() {
    const selectedRadio = document.querySelector('input[name="departmentId"]:checked');
    if (selectedRadio) {
        const departmentId = selectedRadio.value;
        window.location.href = "/v1/reservation/doctors?departmentId=" + departmentId;
    } else {
        alert("진료과를 선택해주세요.");
    }
}
