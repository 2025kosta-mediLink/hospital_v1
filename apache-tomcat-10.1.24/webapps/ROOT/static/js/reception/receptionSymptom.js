document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('symptomForm');
    const note = document.getElementById('noteToDoctor');
    const counter = document.getElementById('noteCounter');
    const submitBtn = document.getElementById('nextBtn');
    const symCheckboxes = document.querySelectorAll('input[name="symptomIds"]');
    const symCountEl = document.getElementById('symCount');
    const maxLen = 500;

    if (!form || !note || !counter || !submitBtn || !symCountEl) return;

    function updateCount() {
        const len = note.value.length;
        counter.textContent = len + ' / ' + maxLen;
        counter.classList.toggle('error', len > maxLen);
        // 동의 체크 제거에 따라 길이 초과만 막음
        submitBtn.disabled = len > maxLen;
    }

    function updateSymCount() {
        let c = 0;
        symCheckboxes.forEach((cb) => { if (cb.checked) c++; });
        symCountEl.textContent = c;
    }

    note.addEventListener('input', updateCount);
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
