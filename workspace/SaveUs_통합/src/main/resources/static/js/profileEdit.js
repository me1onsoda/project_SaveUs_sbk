// 프로필 이미지 미리보기 기능
document.addEventListener("DOMContentLoaded", () => {

    const fileInput = document.getElementById("profileImageInput");
    const previewImg = document.getElementById("profilePreview");

    if (!fileInput || !previewImg) return;

    fileInput.addEventListener("change", (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // 이미지 미리보기
        previewImg.src = URL.createObjectURL(file);
    });
});

document.addEventListener("DOMContentLoaded", () => {

    const newPw = document.querySelector("input[name='newPassword']");
    const confirmPw = document.querySelector("input[name='confirmPassword']");
    const msg = document.getElementById("pwCheckMsg");
    const submitBtn = document.getElementById("pwSubmitBtn");

    function checkMatch() {

        // 둘 중 하나라도 비어있으면 버튼 비활성화
        if (newPw.value === "" || confirmPw.value === "") {
            msg.innerText = "";
            submitBtn.disabled = true;
            return false;
        }

        // 일치할 때
        if (newPw.value === confirmPw.value) {
            msg.style.color = "green";
            msg.innerText = "비밀번호가 일치합니다.";
            submitBtn.disabled = false;   // 버튼 활성화
            return true;

        // 불일치할 때
        } else {
            msg.style.color = "#E74C3C";
            msg.innerText = "비밀번호가 일치하지 않습니다.";
            submitBtn.disabled = true;    // 버튼 비활성화
            return false;
        }
    }

    newPw.addEventListener("input", checkMatch);
    confirmPw.addEventListener("input", checkMatch);
});
