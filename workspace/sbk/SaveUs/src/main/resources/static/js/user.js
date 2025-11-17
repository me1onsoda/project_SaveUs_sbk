let emailChecked = false;
let emailAvailable = false;

// 이메일 조합
function buildEmail() {
    const idPart = document.getElementById("emailId").value;
    const domainPart = document.getElementById("emailDomain").value;

    if (idPart === "" || domainPart === "") {
        return "";
    }
    return idPart + "@" + domainPart;
}

// 이메일 중복확인
function checkEmail() {
    const fullEmail = buildEmail();

    if (fullEmail === "") {
        document.getElementById("emailCheckResult").textContent = "이메일을 입력해주세요.";
        return;
    }

    document.getElementById("emailInput").value = fullEmail;

    fetch("/user/checkEmail?email=" + encodeURIComponent(fullEmail))
        .then(res => res.text())
        .then(result => {
            const msg = document.getElementById("emailCheckResult");
            emailChecked = true;

            if (result === "duplicate") {
                msg.textContent = "이미 사용 중인 이메일입니다.";
                msg.style.color = "red";
                emailAvailable = false;
            } else {
                msg.textContent = "사용 가능한 이메일입니다.";
                msg.style.color = "green";
                emailAvailable = true;
            }
        });
}

// 이메일 변경 시 초기화
function resetEmailCheck() {
    emailChecked = false;
    emailAvailable = false;
    document.getElementById("emailCheckResult").textContent = "";
}

// 개별 실시간 검증
function validateInput(field) {
    const value = document.getElementById(field)?.value;
    const error = document.getElementById(field + "Error");

    if (!error) return;

    if (value === "" || value == null || value === "0") {
        error.textContent = "입력해주세요.";
    } else {
        error.textContent = "";
    }
}

// 전체 제출 검증
function validateForm() {
    let valid = true;

    const fields = [
        { id: "emailId", error: "emailCheckResult" },
        { id: "password", error: "passwordError" },
        { id: "nickname", error: "nicknameError" },
        { id: "birthdate", error: "birthdateError" },
        { id: "height", error: "heightError" },
        { id: "currentWeight", error: "weightError" }
    ];

    fields.forEach(item => {
        const v = document.getElementById(item.id).value;
        const e = document.getElementById(item.error);

        if (v === "" || v == null || v === "0") {
            e.textContent = "입력해주세요.";
            valid = false;
        } else {
            e.textContent = "";
        }
    });

    // 목표 선택 검증
    const goalSelected = document.querySelector("input[name='mainGoal']:checked");
    if (!goalSelected) {
        document.getElementById("goalError").textContent = "목표를 선택해주세요.";
        valid = false;
    } else {
        document.getElementById("goalError").textContent = "";
    }

    // 이메일
    const fullEmail = buildEmail();
    document.getElementById("emailInput").value = fullEmail;

    if (fullEmail === "") {
        document.getElementById("emailCheckResult").textContent = "이메일을 입력해주세요.";
        valid = false;
    } else if (!emailChecked) {
        alert("이메일 중복 확인을 먼저 진행하세요.");
        valid = false;
    } else if (!emailAvailable) {
        alert("사용할 수 없는 이메일입니다.");
        valid = false;
    }

    return valid;
}
