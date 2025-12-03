window.addEventListener("DOMContentLoaded", function () {

    const foundEmail = document.getElementById("foundEmail")?.value;
    const findError = document.getElementById("findError")?.value;

    /*
     * --------------------------------------------------
     * 아이디 찾기 성공 팝업 - UI 완전 복원
     * --------------------------------------------------
     */
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

            buttonsStyling: false,  // ★ 기본 색상 제거
            customClass: {
                confirmButton: "sw-btn",   // ★ 사용자 정의 스타일 사용
                cancelButton: "sw-btn"
            },

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

    /*
     * --------------------------------------------------
     * 실패 팝업 - UI 복원
     * --------------------------------------------------
     */
    if (findError) {
        Swal.fire({
            icon: "warning",
            title: "찾기 실패",
            text: findError,

            buttonsStyling: false,
            customClass: {
                confirmButton: "sw-btn"
            },

            background: "#ffffff",
            color: "#333333",
            borderRadius: "14px",
            width: "380px"
        });
    }


    /*
     * 숫자만 입력
     */
    ["year", "month", "day"].forEach(id => {
        const input = document.getElementById(id);
        if (!input) return;

        input.addEventListener("input", () => {
            input.value = input.value.replace(/[^0-9]/g, "");
        });
    });


    /*
     * 월 제한 (1~12)
     */
    document.getElementById("month").addEventListener("input", function () {
        let v = this.value;
        if (v.length > 2) v = v.slice(0, 2);
        if (Number(v) > 12) v = "12";
        this.value = v;
    });

    /*
     * 일 제한 (1~31)
     */
    document.getElementById("day").addEventListener("input", function () {
        let v = this.value;
        if (v.length > 2) v = v.slice(0, 2);
        if (Number(v) > 31) v = "31";
        this.value = v;
    });


    /*
     * 연도 제한
     */
    const currentYear = new Date().getFullYear();
    const yearInput = document.getElementById("year");

    yearInput.addEventListener("input", function () {
        let v = this.value.replace(/[^0-9]/g, "");
        if (v.length > 4) v = v.slice(0, 4);
        if (Number(v) > currentYear) v = currentYear;
        this.value = v;
    });


    /*
     * 월/일 blur 시 자동 0 보정
     * ex) 3 → 03, 7 → 07
     */
    ["month", "day"].forEach(id => {
        const input = document.getElementById(id);
        input.addEventListener("blur", function () {
            if (this.value.length === 1) {
                this.value = "0" + this.value;
            }
        });
    });

});
