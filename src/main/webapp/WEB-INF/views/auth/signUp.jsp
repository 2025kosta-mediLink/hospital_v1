<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <title>회원가입</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/auth/signUp.css">
    <!-- 박스형 입력 스타일 -->
    <script defer src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script defer src="${pageContext.request.contextPath}/static/js/auth/signUp.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<% request.setAttribute("pageTitle", "회원가입"); %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<h2 class="page-title">회원가입</h2>

<div class="form-section">
    <form id="signUpForm" method="post" action="${pageContext.request.contextPath}/v1/auth/sign-up"
          novalidate>

        <!-- 아이디 + 중복 확인 -->
        <div class="field">
            <label class="label" for="loginId">아이디</label>
            <div class="row">
                <input
                        class="input"
                        id="loginId"
                        name="loginId"
                        type="text"
                        autocomplete="username"
                        placeholder="아이디"
                        maxlength="20"
                        pattern="[A-Za-z0-9]{1,20}"
                        inputmode="latin"
                >
                <button class="btn-idcheck" type="button" id="btnCheckId">중복 확인</button>
            </div>
        </div>

        <!-- 비밀번호 -->
        <div class="field">
            <label class="label" for="password">비밀번호</label>
            <input class="input" id="password" name="password" type="password"
                   autocomplete="new-password" placeholder="비밀번호">
            <div class="helper">사용 가능한 문자는 영문, 숫자, 특수문자이며, 8~16자 이내여야 합니다.<br></div>
        </div>

        <!-- 비밀번호 확인 -->
        <div class="field">
            <label class="label" for="password2">비밀번호 확인</label>
            <input class="input" id="password2" type="password" autocomplete="new-password"
                   placeholder="비밀번호 확인">
        </div>

        <!-- 이름 -->
        <div class="field">
            <label class="label" for="name">이름</label>
            <input class="input" id="name" name="name" type="text" autocomplete="name"
                   placeholder="이름">
        </div>

        <!-- 주민등록번호 -->
        <div class="field">
            <label class="label" for="rrn">주민등록번호</label>
            <input class="input" id="rrn" name="rrn" type="text" inputmode="numeric"
                   autocomplete="off"
                   placeholder="주민등록번호 (예: 901201-1234567)">
        </div>

        <!-- 성별(세그먼트 버튼) -->
        <div class="field">
            <label class="label">성별</label>
            <div class="gender-row" role="radiogroup" aria-label="성별">
                <label class="gender-item">
                    <input type="radio" name="gender" value="F" checked>
                    <span>여자</span>
                </label>
                <label class="gender-item">
                    <input type="radio" name="gender" value="M">
                    <span>남자</span>
                </label>
            </div>
        </div>

        <!-- 휴대폰 -->
        <div class="field">
            <label class="label" for="phone">휴대폰 번호</label>
            <input class="input" id="phone" name="phone" type="text" inputmode="numeric"
                   autocomplete="tel"
                   placeholder="휴대폰 번호">
        </div>

        <!-- 주소 -->
        <div class="field">
            <label class="label" for="address">주소</label>
            <input class="input" id="address" name="address" type="text"
                   autocomplete="street-address"
                   placeholder="주소">
        </div>

        <!-- 제출 -->
        <div class="actions">
            <button class="btn btn-primary" type="submit">회원 가입</button>
        </div>
    </form>
</div>

<% request.setAttribute("activeKey", "home"); %>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body>
</html>
