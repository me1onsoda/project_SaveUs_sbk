/*<<<<<<< HEAD:workspace/SaveUs_통합_Mysql/src/main/resources/static/js/foodWeight.js
document.addEventListener("DOMContentLoaded", function () {

    const saveBtn = document.getElementById("saveWeightBtn");
    const input = document.getElementById("todayWeightInput");
    const currentWeightText = document.getElementById("currentWeightText");
    const bmiText = document.getElementById("bmiValue");
    const bmiStatus = document.getElementById("bmiStatus");

    function getBmiStatus(bmi) {
        if (bmi < 18.5) return "저체중";
        if (bmi < 23) return "정상";
        if (bmi < 25) return "과체중";
        return "비만";
    }

    function updateStatus() {
        const bmi = parseFloat(bmiText.textContent);
        if (!isNaN(bmi)) {
            bmiStatus.textContent = getBmiStatus(bmi);
        }
    }

    // 초기 로딩 시 상태 표시
    updateStatus();

    if (!saveBtn || !input) return;

    saveBtn.addEventListener("click", function () {

        const weight = parseFloat(input.value);

        if (!weight || weight <= 0) {
            alert("올바른 체중을 입력하십시오.");
            return;
        }

        *//* 체중 즉시 반영 *//*
        currentWeightText.textContent = weight.toFixed(1) + " kg";

        *//* BMI 즉시 계산 *//*
        const height = parseFloat(currentWeightText.dataset.height);
        const meter = height / 100.0;
        const raw = weight / (meter * meter);
        const bmi = Math.round(raw * 10) / 10;

        *//* BMI 화면 반영 *//*
        bmiText.textContent = bmi.toFixed(1);

        *//* BMI 상태 반영 *//*
        bmiStatus.textContent = getBmiStatus(bmi);

        *//* 서버 반영 *//*
        fetch("/user/update-weight", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ weight: weight })
        })
        .then(r => r.text())
        .then(msg => console.log("결과:", msg));
    });
});*/

document.addEventListener("DOMContentLoaded", function () {

    const saveBtn = document.getElementById("saveWeightBtn");
    const input = document.getElementById("todayWeightInput");
    const currentWeightText = document.getElementById("currentWeightText");
    const bmiText = document.getElementById("bmiValue");
    const bmiStatus = document.getElementById("bmiStatus");

    function getBmiStatus(bmi) {
        if (bmi < 18.5) return "저체중";
        if (bmi < 23) return "정상";
        if (bmi < 25) return "과체중";
        return "비만";
    }

    function updateStatus() {
        const bmi = parseFloat(bmiText.textContent);
        if (!isNaN(bmi)) {
            bmiStatus.textContent = getBmiStatus(bmi);
        }
    }

    updateStatus();

    if (!saveBtn || !input) return;

    saveBtn.addEventListener("click", function () {

        const weight = parseFloat(input.value);

        if (!weight || weight <= 0) {
            alert("올바른 체중을 입력하십시오.");
            return;
        }

        currentWeightText.textContent = weight.toFixed(1) + " kg";

        const height = parseFloat(currentWeightText.dataset.height);
        const meter = height / 100.0;
        const bmi = weight / (meter * meter);

        bmiText.textContent = bmi.toFixed(1);
        bmiStatus.textContent = getBmiStatus(bmi);

        fetch("/user/update-weight", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ weight: weight })
        })
        .then(r => r.text())
        .then(msg => {
            console.log("결과:", msg);
            location.reload();
        });
    });
});


