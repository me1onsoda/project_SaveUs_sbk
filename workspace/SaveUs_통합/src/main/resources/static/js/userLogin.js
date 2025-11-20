document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("loginForm");  // 수정됨
    if (!form) return;

    form.addEventListener("submit", function (e) {

        let isValid = true;

        const email = document.getElementById("email");
        const password = document.getElementById("password");

        const emailErrorSpan = document.getElementById("emailError");
        const passwordErrorSpan = document.getElementById("passwordError");

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
        if (!isValid) {
            e.preventDefault();
        }
    });
});


window.onload = function () {
    const resetSuccess = document.getElementById("resetSuccess")?.value;

    if (resetSuccess === "true") {
        Swal.fire({
            title: "비밀번호 변경 완료",
            html: `
                <p style="font-size:16px; margin-top:6px;">
                    비밀번호가 성공적으로 변경되었습니다.
                </p>
                <p style="font-size:16px;">새 비밀번호로 로그인해주세요.</p>
            `,
            confirmButtonText: "확인",
            customClass: {
                confirmButton: "sw-btn"
            },

            allowOutsideClick: false,
            allowEscapeKey: false,
            allowEnterKey: false,
            buttonsStyling: false,
            background: "#ffffff",
            width: "420px",
            allowOutsideClick: false
        });
    }
};
