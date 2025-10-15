(function () {
    "use strict";

    const base = document.body.dataset.ctx || '';
    const qs = (s, el = document) => el.querySelector(s);

    const form = document.getElementById('signUpForm');
    if (!form) return;

    // ---------- elements ----------
    const iptId      = qs('#loginId');
    const iptPw      = qs('#password');
    const iptPw2     = qs('#password2');
    const iptName    = qs('#name');
    const iptRrn     = qs('#rrn');
    const iptPhone   = qs('#phone');
    const iptAddress = qs('#address');
    const btnCheckId = qs('#btnCheckId');

    // ---------- utils ----------
    const onlyDigits = (s) => s.replace(/[^\d]/g, '');

    // 010-1234-5678 형태
    const phoneHyphen = (v) => {
        const d = onlyDigits(v).slice(0, 11); // 최대 11자리
        if (d.length < 4) return d;
        if (d.length < 7) return d.replace(/(\d{3})(\d+)/, '$1-$2');
        return d.replace(/(\d{3})(\d{3,4})(\d{0,4}).*/, (_, a, b, c) => (c ? `${a}-${b}-${c}` : `${a}-${b}`));
    };

    // 주민등록번호: 앞 6자리 + '-' + 뒤 7자리 (총 13자리, 자동 하이픈)
    const rrnMask = (v) => {
        const d = onlyDigits(v).slice(0, 13); // 6 + 7 = 13
        if (d.length <= 6) return d;
        return d.slice(0, 6) + '-' + d.slice(6);
    };

    // ---------- behaviors ----------

    // 1) 아이디: 영문/숫자만 + 최대 20자 (실시간 필터)
    if (iptId) {
        const filterId = () => {
            const raw = iptId.value;
            const filtered = raw.replace(/[^A-Za-z0-9]/g, '').slice(0, 20);
            if (filtered !== raw) iptId.value = filtered;
        };
        iptId.addEventListener('input', filterId);
        iptId.addEventListener('paste', (e) => {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            iptId.value = (text || '').replace(/[^A-Za-z0-9]/g, '').slice(0, 20);
        });
    }

    // 2) 휴대폰: 숫자만 + 자동 하이픈
    if (iptPhone) {
        const onPhone = () => { iptPhone.value = phoneHyphen(iptPhone.value); };
        iptPhone.addEventListener('input', onPhone);
        iptPhone.addEventListener('paste', (e) => {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            iptPhone.value = phoneHyphen(text || '');
        });
    }

    // 3) 주민등록번호: 숫자만 + 6자리 뒤 자동 '-' + 총 13자리 제한
    if (iptRrn) {
        const onRrn = () => { iptRrn.value = rrnMask(iptRrn.value); };
        iptRrn.addEventListener('input', onRrn);
        iptRrn.addEventListener('paste', (e) => {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            iptRrn.value = rrnMask(text || '');
        });
    }

    // 4) 아이디 중복 확인
    if (btnCheckId) {
        btnCheckId.addEventListener('click', async () => {
            const v = iptId.value.trim();
            if (!v) return alert('아이디를 입력하세요.');
            try {
                const r = await fetch(`${base}/v1/auth/check-id?loginId=${encodeURIComponent(v)}`, { cache: 'no-store' });
                const t = await r.text();
                if (/available=true/.test(t)) {
                    alert('사용 가능한 아이디입니다.');
                    iptId.dataset.available = 'true';
                } else {
                    alert('이미 사용 중인 아이디입니다.');
                    iptId.dataset.available = 'false';
                }
            } catch (err) {
                console.error(err);
                alert('중복 확인 중 오류가 발생했습니다.');
            }
        });
    }

    // 5) 제출 검증
    form.addEventListener('submit', (e) => {
        if (iptId?.dataset.available !== 'true') { e.preventDefault(); return alert('아이디 중복확인을 해주세요.'); }
        if (!iptPw.value || iptPw.value.length < 8) { e.preventDefault(); return alert('비밀번호는 8자 이상입니다.'); }
        if (iptPw.value !== iptPw2.value) { e.preventDefault(); return alert('비밀번호 확인이 일치하지 않습니다.'); }
        if (!iptName.value.trim()) { e.preventDefault(); return alert('이름을 입력하세요.'); }

        const rrn = iptRrn.value.trim();
        if (!/^\d{6}-\d{7}$/.test(rrn)) { e.preventDefault(); return alert('주민등록번호 형식을 확인하세요.'); }

        const phone = iptPhone.value.trim();
        if (!/^\d{3}-\d{3,4}-\d{4}$/.test(phone)) { e.preventDefault(); return alert('휴대폰 번호 형식을 확인하세요.'); }

        if (!iptAddress.value.trim()) { e.preventDefault(); return alert('주소를 입력하세요.'); }
    });

})();
