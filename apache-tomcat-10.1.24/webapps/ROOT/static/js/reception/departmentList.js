// 선택해야 '다음' 버튼 활성화 (널 가드 포함)
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('deptForm');
    const nextBtn = document.getElementById('nextBtn');
    if (!form || !nextBtn) return;

    form.addEventListener('change', function (e) {
        if (e.target && e.target.name === 'departmentId') nextBtn.disabled = false;
    });
});

// 뒤로가기 (referrer 없으면 폴백으로 이동)
function goBackOr(url) {
    if (document.referrer) {
        history.back();
    } else {
        location.href = url;
    }
}
