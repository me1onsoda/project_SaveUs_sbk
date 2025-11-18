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

// 닉네임 유효성 검사
document.addEventListener("DOMContentLoaded", () => {

    const nicknameInput = document.getElementById("nicknameInput");
    const result = document.getElementById("nicknameCheckResult");

    nicknameInput.addEventListener("input", () => {
        const nickname = nicknameInput.value.trim();

        // 1) 빈 값 검사
        if (nickname.length === 0) {
            result.textContent = "";
            return;
        }

        // 2) 길이 검사
        if (nickname.length < 2 || nickname.length > 12) {
            result.textContent = "닉네임은 2~12자 사이여야 합니다.";
            result.style.color = "#E74C3C";
            return;
        }

        // 3) 정규식 검사 (한글, 숫자, 영문, _ 허용)
        const regex = /^[a-zA-Z0-9가-힣_]+$/;
        if (!regex.test(nickname)) {
            result.textContent = "닉네임은 한글,영문,숫자, _ 만 사용할 수 있습니다.";
            result.style.color = "#E74C3C";
            return;
        }

        // 4) 중복 검사 요청
        fetch(`/user/checkNickname?nickname=${nickname}`)
            .then(res => res.text())
            .then(data => {
                if (data === "duplicate") {
                    result.textContent = "이미 사용 중인 닉네임입니다.";
                    result.style.color = "#E74C3C";
                } else {
                    result.textContent = "사용 가능한 닉네임입니다.";
                    result.style.color = "green";
                }
            })
            .catch(() => {
                result.textContent = "검사 중 오류가 발생했습니다.";
                result.style.color = "#E74C3C";
            });
    });

});


// 이메일 중복이면 제출막기
function validateForm() {
    const emailMsg = document.getElementById("emailCheckResult").innerText;
    const nicknameMsg = document.getElementById("nicknameCheckResult").innerText;

    // 이메일 중복이면 제출 막기
    if (emailMsg.includes("이미 사용 중")) {
        alert("이미 사용 중인 이메일입니다.");
        return false;
    }

    // 닉네임 중복이면 제출 막기
    if (nicknameMsg.includes("이미 사용 중") ||
        nicknameMsg.includes("닉네임은")) {
        alert("닉네임을 다시 확인해주세요.");
        return false;
    }

    return true;
}
