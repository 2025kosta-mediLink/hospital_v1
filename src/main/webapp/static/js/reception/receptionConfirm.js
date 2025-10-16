// 뒤로가기 (referrer 없으면 폴백)
function goBackOr(url) {
    if (document.referrer) {
        history.back();
    } else {
        location.href = url;
    }
}

// 동의 체크 시 버튼 활성화
document.addEventListener('DOMContentLoaded', function () {
    const agree = document.getElementById('consentNotice');
    const submit = document.getElementById('submitBtn');
    if (!agree || !submit) return;

    const sync = () => { submit.disabled = !agree.checked; };
    agree.addEventListener('change', sync);
    sync();
});
