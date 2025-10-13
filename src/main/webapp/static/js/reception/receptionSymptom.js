document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('symptomForm');
    const note = document.getElementById('noteToDoctor');
    const counter = document.getElementById('noteCounter');
    const consent = document.getElementById('consentNotice');
    const submitBtn = document.getElementById('nextBtn');
    const symCheckboxes = document.querySelectorAll('input[name="symptomIds"]');
    const symCountEl = document.getElementById('symCount');
    const maxLen = 500;

    if (!form || !note || !counter || !consent || !submitBtn || !symCountEl) return;

    function updateCount() {
        const len = note.value.length;
        counter.textContent = len + ' / ' + maxLen;
        counter.classList.toggle('error', len > maxLen);
        submitBtn.disabled = !(consent.checked && len <= maxLen);
    }

    function updateSymCount() {
        let c = 0;
        symCheckboxes.forEach((cb) => { if (cb.checked) c++; });
        symCountEl.textContent = c;
    }

    note.addEventListener('input', updateCount);
    consent.addEventListener('change', updateCount);
    symCheckboxes.forEach((cb) => cb.addEventListener('change', updateSymCount));

    // 초기 상태
    updateCount();
    updateSymCount();

    form.addEventListener('submit', function (e) {
        if (note.value.length > maxLen) {
            e.preventDefault();
            alert('전달사항은 최대 ' + maxLen + '자까지 입력 가능합니다.');
        }
    });
});

// 뒤로가기 (referrer 없으면 폴백 이동)
function goBackOr(url) {
    if (document.referrer) {
        history.back();
    } else {
        location.href = url;
    }
}
