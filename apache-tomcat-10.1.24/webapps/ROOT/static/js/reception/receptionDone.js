function goBackOr(url) {
    if (document.referrer) {
        history.back();
    } else {
        location.href = url;
    }
}
