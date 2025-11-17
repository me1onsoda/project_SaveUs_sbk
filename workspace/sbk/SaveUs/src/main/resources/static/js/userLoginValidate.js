document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("userLoginForm");
    if (!form) return;

    form.addEventListener("submit", function (e) {

        let isValid = true;

        const email = document.getElementById("email");
        const password = document.getElementById("password");

        const emailErrorSpan = document.getElementById("emailError");
        const passwordErrorSpan = document.getElementById("passwordError");

        emailErrorSpan.textContent = "";
        passwordErrorSpan.textContent = "";

        if (!email.value.trim()) {
            emailErrorSpan.textContent = "이메일을 입력해 주십시오.";
            isValid = false;
        }

        if (!password.value.trim()) {
            passwordErrorSpan.textContent = "비밀번호를 입력해 주십시오.";
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
});
