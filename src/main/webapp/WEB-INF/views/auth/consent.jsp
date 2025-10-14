<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html lang="ko"><head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>동의</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/auth/consent.css">
<script defer src="${pageContext.request.contextPath}/static/js/common.js"></script>
<script defer src="${pageContext.request.contextPath}/static/js/auth/consent.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<% request.setAttribute("pageTitle", ""); %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="consent">
    <h2 class="consent-page-title">회원가입 약관 동의</h2>

    <!-- 전체동의 -->
    <label class="consent-all">
    <span class="checkbox">
      <input type="checkbox" id="agreeAll">
      <i></i>
        <!-- 흰 체크 -->
      <svg viewBox="0 0 14 14" aria-hidden="true"><path d="M3 7l3 3 5-6"/></svg>
    </span>
        <strong>전체동의</strong>
    </label>

    <!-- 개인정보 처리방침 -->
    <section class="consent-section">
        <div class="consent-head">
            <div class="consent-title">개인정보 처리방침 동의</div>
        </div>

        <label class="consent-item">
      <span class="checkbox">
        <input type="checkbox" id="agreePrivacy">
        <i></i><svg viewBox="0 0 14 14"><path d="M3 7l3 3 5-6"/></svg>
      </span>
            <span>(필수) 개인정보 처리방침 동의<br>
            <small class="consent-desc">개인정보 수집·이용 목적/기간, 제3자 제공, 처리 위탁
            </small>
            </span>
        </label>

        <label class="consent-item">
      <span class="checkbox">
        <input type="checkbox" id="agreeMarketing">
        <i></i><svg viewBox="0 0 14 14"><path d="M3 7l3 3 5-6"/></svg>
      </span>
            <span>(선택) 진료/예약 알림 및 마케팅 정보 수신<br>
            <small class="consent-desc">문자·알림 수신 동의, 언제든 해제 가능
            </small>
            </span>
        </label>
    </section>

    <!-- 서비스 이용 약관 -->
    <section class="consent-section">
        <div class="consent-head">
            <div class="consent-title">서비스 이용 약관 동의</div>
        </div>

        <label class="consent-item">
      <span class="checkbox">
        <input type="checkbox" id="agreeService">
        <i></i><svg viewBox="0 0 14 14"><path d="M3 7l3 3 5-6"/></svg>
      </span>
            <span>(필수) 서비스 이용 약관 동의<br>
            <small class="consent-desc">회원가입, 이용자 의무, 서비스 제한, 분쟁 해결
            </small>
            </span>
        </label>

        <label class="consent-item">
      <span class="checkbox">
        <input type="checkbox" id="agreeTele">
        <i></i><svg viewBox="0 0 14 14"><path d="M3 7l3 3 5-6"/></svg>
      </span>
            <span>(필수) 비대면진료·처방 및 본인확인 동의<br>
            <small class="consent-desc">진료 녹취/기록 처리, 본인확인, 전자처방전 전송
            </small>
            </span>
        </label>
    </section>

    <div class="consent-action">
        <button class="btn btn-primary" id="btnConsentConfirm">확인</button>
    </div>
</div>


<% request.setAttribute("activeKey", "home"); %>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body></html>
