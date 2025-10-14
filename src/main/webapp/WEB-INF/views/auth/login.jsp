<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <title>로그인</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webapp/static/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/auth/login.css">
    <script defer src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script defer src="${pageContext.request.contextPath}/static/js/auth/login.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card auth">
    <div class="card-title">로그인</div>
    <!-- 로그인 폼 -->
    <form id="loginForm" method="post" action="${pageContext.request.contextPath}/v1/auth/login" novalidate>
        <div class="form-group">
            <label class="label" for="loginId">아이디</label>
            <input class="input" id="loginId" name="loginId" type="text" autocomplete="username" required>
        </div>
        <div class="form-group">
            <label class="label" for="password">비밀번호</label>
            <input class="input" id="password" name="password" type="password" autocomplete="current-password" required>
        </div>
        <button class="btn btn-primary stack-24" type="submit">로그인</button>
    </form>

    <div class="center stack-16">
        <a id="goSignUp" class="link" href="${pageContext.request.contextPath}/v1/auth/consent">회원가입</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>

</body>
</html>
