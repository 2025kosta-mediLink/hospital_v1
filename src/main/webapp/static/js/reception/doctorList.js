// 라디오 선택 시 칩/카드 동기화 + 버튼 활성화
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('doctorForm');
    const nextBtn = document.getElementById('nextBtn');
    const chipEls = document.querySelectorAll('.chip');
    const cardEls = document.querySelectorAll('.card-wrap');
    if (!form || !nextBtn) return;

    form.addEventListener('change', function (e) {
        if (e.target && e.target.name === 'doctorId') {
            nextBtn.disabled = false;
            syncSelection(e.target.value);
        }
    });

    // 뒤로가기 등으로 라디오 상태가 남아있는 경우 초기 동기화
    const checked = form.querySelector('input[name="doctorId"]:checked');
    if (checked) {
        nextBtn.disabled = false;
        syncSelection(checked.value);
    }

    function syncSelection(value) {
        chipEls.forEach((ch) => ch.classList.toggle('active', ch.dataset.doc === value));
        cardEls.forEach((cd) => cd.classList.toggle('active', cd.dataset.doc === value));
    }
});

// 뒤로가기 (referrer 없으면 폴백 이동)
function goBackOr(url) {
    if (document.referrer) {
        history.back();
    } else {
        location.href = url;
    }
}
