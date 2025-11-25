window.onload = function () {
    const foundEmail = document.getElementById("foundEmail")?.value;
    const findError = document.getElementById("findError")?.value;

    // 아이디 찾기 성공 팝업
    if (foundEmail) {
        Swal.fire({
            title: "아이디 찾기 완료",
            html: `
                <p style="font-size:16px; margin-top:6px;">회원님의 아이디는</p>
                <strong style="font-size:18px; color:#1ABC9C;">${foundEmail}</strong>
            `,
            showCancelButton: true,
            confirmButtonText: "로그인",
            cancelButtonText: "비밀번호 찾기",

            allowOutsideClick: false,
            allowEscapeKey: false,
            allowEnterKey: false,

            customClass: {
                confirmButton: "sw-btn",
                cancelButton: "sw-btn"
            },
            buttonsStyling: false,
            background: "#ffffff",
            color: "#333333",
            borderRadius: "14px",
            width: "420px"
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = "/login";
            } else {
                window.location.href = "/user/find-pw";
            }
        });
    }

    // 실패 팝업
    if (findError) {
        Swal.fire({
            icon: "warning",
            title: "찾기 실패",
            text: findError,
            customClass: {
                confirmButton: "sw-btn"
            },
            buttonsStyling: false,
            background: "#ffffff",
            color: "#333333",
            borderRadius: "14px",
            width: "380px"
        });
    }

    /* 아이디 찾기 페이지 생년월일 자동 보정 추가 */
    ["year", "month", "day"].forEach(id => {
        const input = document.getElementById(id);
        if (!input) return;

        // 숫자만 입력
        input.addEventListener("input", () => {
            input.value = input.value.replace(/[^0-9]/g, "");
        });

        // 자동 0 보정
        input.addEventListener("blur", () => {
            if (id === "month" || id === "day") {
                if (input.value.length === 1) {
                    input.value = "0" + input.value;
                }
            }
        });
    });
};
