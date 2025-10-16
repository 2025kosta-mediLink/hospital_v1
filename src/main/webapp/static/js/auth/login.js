(function () {
    "use strict";
    const base = document.body.dataset.ctx || '';
    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
        const id = form.loginId.value.trim();
        const pw = form.password.value.trim();
        if (!id || !pw) { e.preventDefault(); alert('아이디와 비밀번호를 입력해주세요.'); }
    });

    const go = document.getElementById('goSignUp');
    if (go) go.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = base + '/v1/auth/consent';
    });
})();
