(function () {
    "use strict";
    const base = document.body.dataset.ctx || '';

    const agreeAll = document.getElementById('agreeAll');
    const agreePrivacy = document.getElementById('agreePrivacy');
    const agreeService = document.getElementById('agreeService');
    const agreeTele = document.getElementById('agreeTele');
    const agreeMarketing = document.getElementById('agreeMarketing');
    const btn = document.getElementById('btnConsentConfirm');

    if (!(agreeAll && agreePrivacy && agreeService && agreeTele && btn)) return;

    const allBoxes = [agreePrivacy, agreeService, agreeTele, agreeMarketing].filter(Boolean);

    // 초기 상태: 모두 해제(뒤로가기 복원 방지)
    const resetAll = () => { [agreeAll, ...allBoxes].forEach(cb => cb && (cb.checked = false)); };
    resetAll();
    window.addEventListener('pageshow', e => { if (e.persisted) resetAll(); });

    const syncAll = () => { agreeAll.checked = allBoxes.every(cb => cb.checked); };
    const syncReq = () => { const v = agreeAll.checked; allBoxes.forEach(cb => cb.checked = v); };

    agreeAll.addEventListener('change', syncReq);
    allBoxes.forEach(cb => cb.addEventListener('change', syncAll));

    btn.addEventListener('click', () => {
        if (!agreePrivacy.checked || !agreeService.checked || !agreeTele.checked) {
            alert('필수 약관에 모두 동의해 주세요.');
            return;
        }
        window.location.href = base + '/v1/auth/sign-up';
    });
})();
