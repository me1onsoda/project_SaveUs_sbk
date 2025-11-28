document.addEventListener("DOMContentLoaded", () => {

    // 로그인한 사용자 ID 가져오기
    const userId = document.body.getAttribute("data-user-id");
    if (!userId) {
        console.error("userId 찾을 수 없음");
        return;
    }

    const apiUrl = `/api/food/recommend/${userId}`;

    fetch(apiUrl, {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(res => res.json())
        .then(data => {
            console.log("식단 추천 결과:", data);

            const listEl = document.getElementById("foodRecommendList");
            const deficitEl = document.getElementById("deficitList");

            // 추천 음식 표시
            listEl.innerHTML = "";
            data.recommended.forEach(item => {
                const li = document.createElement("li");
                li.innerText = `${item.food_name} (${item.category})`;
                listEl.appendChild(li);
            });

            // 부족 영양소 표시
            deficitEl.innerHTML = "";
            if (data.deficit.length === 0) {
                deficitEl.innerHTML = "<li>부족한 영양소 없음</li>";
            } else {
                data.deficit.forEach(d => {
                    const li = document.createElement("li");
                    li.innerText = d;
                    deficitEl.appendChild(li);
                });
            }
        })
        .catch(err => {
            console.error("식단 추천 API 오류:", err);
            document.getElementById("foodRecommendList").innerHTML =
                "<li>추천 데이터를 불러올 수 없습니다.</li>";
        });
});
