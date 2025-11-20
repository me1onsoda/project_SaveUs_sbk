document.addEventListener("DOMContentLoaded", function () {

    const btn = document.getElementById("mealAddBtn");
    const input = document.getElementById("mealSearchInput");

    if (!btn || !input) {
        console.log("버튼 또는 입력창을 찾을 수 없습니다.");
        return;
    }

    btn.addEventListener("click", async function () {

        const keyword = input.value.trim();
        if (keyword === "") {
            alert("음식명을 입력해주세요.");
            return;
        }

        try {
            const res = await fetch("/food/search?keyword=" + encodeURIComponent(keyword));
            const foods = await res.json();

            if (!foods || foods.length === 0) {
                alert("검색 결과가 없습니다.");
                return;
            }

            const food = foods[0];

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

