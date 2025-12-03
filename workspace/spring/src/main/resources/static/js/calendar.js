
function selectDate(element) {
    const dateStr = element.getAttribute('data-date');

    if (!dateStr) return;

    const wrapper = document.querySelector('.calendar-wrapper');
    wrapper.classList.add('active');

    document.querySelectorAll('.day-box').forEach(box => box.classList.remove('selected'));
    element.classList.add('selected');

    document.getElementById('panelDateTitle').innerText = formatDateKorea(dateStr);

    const container = document.getElementById('mealListContainer');
    container.innerHTML = '<p class="empty-msg">데이터를 불러오는 중입니다...</p>';

    fetch(`/api/calendar/meals?date=${dateStr}`)
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            renderMealCards(data, container);
        })
        .catch(error => {
            console.error('Error:', error);
            container.innerHTML = '<p class="empty-msg">데이터를 불러오는데 실패했습니다.</p>';
        });
}

function closePanel() {
    const wrapper = document.querySelector('.calendar-wrapper');
    wrapper.classList.remove('active');

    document.querySelectorAll('.day-box').forEach(box => box.classList.remove('selected'));
}

function renderMealCards(mealList, container) {
    container.innerHTML = '';

    if (mealList.length === 0) {
        container.innerHTML = `
            <div class="empty-msg">
                <p>🍽️</p>
                <p>기록된 식사가 없습니다.</p>
            </div>`;
        return;
    }

    mealList.forEach(meal => {
        const kcal = meal.caloriesKcal || 0;
        const carbo = meal.carbsG || 0;
        const protein = meal.proteinG || 0;
        const fat = meal.fatsG || 0;

        const cardHtml = `
            <div class="meal-card">
                <div class="meal-header">
                    <span class="meal-name">${meal.mealName}</span>
                    <span class="meal-time">${meal.eatTime}</span>
                </div>
                <div class="meal-calories">${kcal} kcal</div>
                <div class="nutrient-info">
                    <span>탄 ${carbo}g</span>
                    <span>단 ${protein}g</span>
                    <span>지 ${fat}g</span>
                </div>
            </div>
        `;
        container.innerHTML += cardHtml;
    });
}

function formatDateKorea(dateString) {
    const date = new Date(dateString);
    return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
}