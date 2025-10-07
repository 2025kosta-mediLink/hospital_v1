<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>의료진 선택</title></head>
<body>
<h2>의료진 선택</h2>

<!--
  [화면 설명]
  이전 단계에서 선택한 진료과(departmentId)를 기준으로
  해당 진료과 소속 의사 목록(doctors)을 출력하고,
  사용자가 한 명의 의사를 선택하면 다음 단계(/v1/reception/symptom)로 이동한다.
-->
<form method="get" action="${pageContext.request.contextPath}/v1/reception/symptom">

    <!--
      departmentId는 모든 의사가 동일한 값을 가지므로 한 번만 전송해도 충분하다.
      doctors[0]은 리스트의 첫 번째 의사 객체에서 departmentId 값을 가져옴.
      이렇게 하면 불필요한 hidden 필드 중복을 줄일 수 있다.
    -->
    <input type="hidden" name="departmentId" value="${doctors[0].departmentId}" />

    <!--
      의사 목록 반복 출력 (Controller에서 setAttribute("doctors", list))
      각 의사마다 라디오 버튼을 생성하고, 사용자가 한 명만 선택할 수 있다.
      선택된 doctorId는 GET 파라미터로 전송됨.
      ex) /v1/reception/symptom?departmentId=1&doctorId=21
    -->
    <c:forEach var="doc" items="${doctors}">
        <label>
            <!-- 의사 선택용 라디오 버튼 -->
            <input type="radio" name="doctorId" value="${doc.doctorId}" required />
            <!-- 의사 이름과 소속 진료과명 표시 -->
                ${doc.name} (${doc.departmentName})
        </label><br/>
    </c:forEach>

    <!--
      “다음” 버튼 클릭 시 form이 전송되어
      departmentId와 선택된 doctorId가 /v1/reception/symptom 으로 전달됨.
    -->
    <button type="submit">다음</button>
</form>

</body>
</html>
