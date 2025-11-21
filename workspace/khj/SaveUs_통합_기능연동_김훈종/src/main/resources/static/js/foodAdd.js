document.addEventListener("DOMContentLoaded", function () {

    const btn = document.getElementById("mealAddBtn");
    const input = document.getElementById("mealSearchInput");
    const box = document.getElementById("autocompleteBox");

    if (!btn || !input) {
        console.log("버튼 또는 입력창을 찾을 수 없습니다.");
        return;
    }

    // 자동완성 기능
    input.addEventListener("input", async function () {
        const keyword = input.value.trim();

        if (keyword.length === 0) {
            box.innerHTML = "";
            box.style.display = "none";
            return;
        }

        try {
            const res = await fetch(`/food/autocomplete?keyword=${encodeURIComponent(keyword)}`);
            const names = await res.json();

            if (!names || names.length === 0) {
                box.innerHTML = "";
                box.style.display = "none";
                return;
            }

            let html = "";
            names.forEach(n => {
                html += `<div class="autocomplete-item" data-name="${n}">${n}</div>`;
            });

            box.innerHTML = html;
            box.style.display = "block";

        } catch (e) {
            console.error(e);
        }
    });

    // 자동완성 선택 처리
    box.addEventListener("click", function (event) {
        if (event.target.classList.contains("autocomplete-item")) {
            const name = event.target.getAttribute("data-name");
            input.value = name;
            box.innerHTML = "";
            box.style.display = "none";
        }
    });

    // 추가 버튼 클릭 → 기존 기능 그대로 유지
    btn.addEventListener("click", async function () {

        // 자동완성 박스 닫기
        box.innerHTML = "";
        box.style.display = "none";

        const keyword = input.value.trim();
        if (keyword === "") {
            alert("음식명을 입력해주세요.");
            return;
        }

        try {
            // 음식 검색 요청
            const res = await fetch("/food/search?keyword=" + encodeURIComponent(keyword));
            const foods = await res.json();

            if (!foods || foods.length === 0) {
                alert("검색 결과가 없습니다.");
                return;
            }

            const food = foods[0];

            // 서버에 저장 요청
            const saveRes = await fetch("/meal/add-from-food", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    mealName: food.foodName,
                    calories: food.caloriesKcal,
                    carbs: food.carbsG,
                    protein: food.proteinG,
                    fat: food.fatG,
                    sugar: food.sugarG,
                    fiber: food.fiberG,
                    calcium: food.calciumMg,
                    sodium: food.sodiumMg
                })
            });

            const result = await saveRes.text();

            if (result === "OK") {
                alert("식사 목록에 추가되었습니다");
                location.reload();
            } else {
                alert("추가 중 오류가 발생했습니다: " + result);
            }

        } catch (e) {
            console.error(e);
            alert("요청 처리 중 오류가 발생했습니다.");
        }
    });
});
