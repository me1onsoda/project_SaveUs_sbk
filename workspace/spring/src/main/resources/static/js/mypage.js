// userId == null (비로그인 마이페이지 접근시)
function requireLogin() {
    location.href = "/login";
}

// 회원 탈퇴 버튼 클릭 처리
function deleteAccount() {
    if (confirm("정말로 탈퇴하시겠습니까? 모든 정보가 삭제됩니다.")) {
        const form = document.createElement("form");
        form.method = "POST";
        form.action = "/user/delete";
        document.body.appendChild(form);
        form.submit();
    }
}

