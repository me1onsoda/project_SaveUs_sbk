/* 이메일 조합 */
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

/* 이메일 중복 체크 */
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

/* 숫자만 입력 */
function onlyNumber(input) {
    input.value = input.value.replace(/[^0-9]/g, "");
}

/* 1자리 → 2자리 보정 */
function autoPad(input) {
    if (input.value.length === 1) {
        input.value = "0" + input.value;
    }
}

/* 나이 자동 계산 */
function calculateAge() {

    const year = document.getElementById("year").value;
    const month = document.getElementById("month").value;
    const day = document.getElementById("day").value;

    if (year.length !== 4 || month.length !== 2 || day.length !== 2) return;

    const birthStr = `${year}-${month}-${day}`;
    const today = new Date();
    const birthDate = new Date(birthStr);

    if (isNaN(birthDate.getTime())) return;

    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() + 1 - (birthDate.getMonth() + 1);

    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }

    document.getElementById("age").value = age;
    document.getElementById("birthdate").value = birthStr;
}

/* 닉네임 중복 + 유효성 */
document.addEventListener("DOMContentLoaded", () => {

    /* 이메일 복원 */
    const full = document.getElementById('emailInput')?.value;

    if (full) {
        const parts = full.split('@');
        document.getElementById('emailId').value = parts[0];
        document.getElementById('emailDomain').value = parts[1];
    }

    /* 생년월일 텍스트박스 이벤트 */
    ["year", "month", "day"].forEach(id => {
        const input = document.getElementById(id);

        input.addEventListener("input", () => {
            onlyNumber(input);

            if (id === "month" || id === "day") {
                if (input.value.length >= 2) autoPad(input);
            }
        });

        input.addEventListener("blur", () => {
            if (id === "month" || id === "day") {
                if (input.value.length === 1) autoPad(input);
            }
            calculateAge();
        });
    });

    /* 닉네임 검사 */
    const nicknameInput = document.getElementById("nicknameInput");
    const result = document.getElementById("nicknameCheckResult");

    nicknameInput.addEventListener("input", () => {
        const nickname = nicknameInput.value.trim();

        if (nickname.length === 0) {
            result.textContent = "";
            return;
        }

        if (nickname.length < 2 || nickname.length > 12) {
            result.textContent = "닉네임은 2~12자 사이여야 합니다.";
            result.style.color = "#E74C3C";
            return;
        }

        const regex = /^[a-zA-Z0-9가-힣_]+$/;
        if (!regex.test(nickname)) {
            result.textContent = "닉네임은 한글,영문,숫자, _ 만 사용할 수 있습니다.";
            result.style.color = "#E74C3C";
            return;
        }

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

/* 제출 전 검사 */
function validateForm() {

    const emailMsg = document.getElementById("emailCheckResult").innerText;
    const nicknameMsg = document.getElementById("nicknameCheckResult").innerText;
    const birth = document.getElementById("birthdate").value;

    if (!birth) {
        alert("생년월일을 올바르게 입력해주세요.");
        return false;
    }

    if (emailMsg.includes("이미 사용 중")) {
        alert("이미 사용 중인 이메일입니다.");
        return false;
    }

    if (nicknameMsg.includes("이미 사용 중") ||
        nicknameMsg.includes("닉네임은")) {
        alert("닉네임을 다시 확인해주세요.");
        return false;
    }

    return true;
}

function calculateBMI() {
    const h = parseFloat(document.getElementById("heightInput").value);
    const w = parseFloat(document.getElementById("weightInput").value);

    if (!h || !w) return;

    const meter = h / 100.0;
    const bmi = w / (meter * meter);

    // 소수 첫째 자리까지 반올림
    const bmiRounded = Math.round(bmi * 10) / 10;

    document.getElementById("bmiInput").value = bmiRounded;
}

document.getElementById("heightInput").addEventListener("input", calculateBMI);
document.getElementById("weightInput").addEventListener("input", calculateBMI);
