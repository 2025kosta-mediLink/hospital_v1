(function () {
    "use strict";
    const base = document.body.dataset.ctx || '';
    document.addEventListener('click', (e) => {
        if (e.target.closest('.btn-back')) history.back();
        if (e.target.closest('.btn-close')) window.location.href = base + '/v1/home';
    });
})();
