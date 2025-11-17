function updateEmail() {
    const id = document.getElementById("emailId").value;
    const domain = document.getElementById("emailDomain").value;

    const full = id && domain ? id + "@" + domain : "";
    document.getElementById("emailInput").value = full;
}

function resetEmailCheck() {
    const result = document.getElementById("emailCheckResult");
    result.innerText = "";
    result.style.color = "";
}

function checkEmail() {
    const email = document.getElementById("emailInput").value;
    const result = document.getElementById("emailCheckResult");

    if (!email) {
        result.innerText = "이메일을 입력해주세요.";
        result.style.color = "#E74C3C";
        return;
    }

    fetch(`/user/checkEmail?email=${email}`)
        .then(res => res.text())
        .then(status => {
            if (status === "duplicate") {
                result.innerText = "이미 사용 중인 이메일입니다.";
                result.style.color = "#E74C3C";
            } else {
                result.innerText = "사용 가능한 이메일입니다.";
                result.style.color = "green";
            }
        });
}

/* 이메일 복원 기능 + 생년월일 나이 계산 기능 */
window.addEventListener('DOMContentLoaded', function () {

    /* 이메일 복원 */
    const full = document.getElementById('emailInput')?.value;

    if (full) {
        const parts = full.split('@');
        document.getElementById('emailId').value = parts[0];
        document.getElementById('emailDomain').value = parts[1];
    }

    /* 생년월일 -> 나이 자동 계산 */
    const birthInput = document.getElementById("birthdate");
    const ageInput = document.getElementById("age");

    if (birthInput && ageInput) {
        birthInput.addEventListener("change", function () {
            ageInput.value = calculateAge(this.value);
        });

        /* 페이지 로딩 시 이미 값이 있을 경우 자동 계산 */
        if (birthInput.value) {
            ageInput.value = calculateAge(birthInput.value);
        }
    }
});

/* 나이 계산 함수 */
function calculateAge(birth) {
    if (!birth) return "";

    const today = new Date();
    const birthDate = new Date(birth);

    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    const d = today.getDate() - birthDate.getDate();

    if (m < 0 || (m === 0 && d < 0)) {
        age--;
    }
    return age;
}
