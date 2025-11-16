document.addEventListener("DOMContentLoaded", function () {

<<<<<<< HEAD
    const form = document.getElementById("userLoginForm");
=======
    const form = document.getElementById("loginForm");  // 수정됨
>>>>>>> home2
    if (!form) return;

    form.addEventListener("submit", function (e) {

        let isValid = true;

        const email = document.getElementById("email");
        const password = document.getElementById("password");

        const emailErrorSpan = document.getElementById("emailError");
        const passwordErrorSpan = document.getElementById("passwordError");

<<<<<<< HEAD
        emailErrorSpan.textContent = "";
        passwordErrorSpan.textContent = "";

        if (!email.value.trim()) {
            emailErrorSpan.textContent = "이메일을 입력해 주십시오.";
            isValid = false;
        }

        if (password.value.trim().length < 8) {
            passwordErrorSpan.textContent = "비밀번호는 8자 이상이어야 합니다.";
            isValid = false;
        }

=======
        // 초기화
        emailErrorSpan.textContent = "";
        passwordErrorSpan.textContent = "";

        // 이메일 검사
        if (!email.value.trim()) {
            emailErrorSpan.textContent = "이메일을 입력해 주세요.";
            isValid = false;
        }

        // 비밀번호 검사
        if (!password.value.trim()) {
            passwordErrorSpan.textContent = "비밀번호를 입력해 주세요.";
            isValid = false;
        }

        // 유효성 실패 → 서버로 보내지 않음
>>>>>>> home2
        if (!isValid) {
            e.preventDefault();
        }
    });
});
