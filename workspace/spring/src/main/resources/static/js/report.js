/* src/main/resources/static/js/report.js */

const COLOR_PRIMARY = '#1DAB87';
const COLOR_DANGER = '#FF6B6B';
const COLOR_WARNING = '#FFC857';
const COLOR_INFO = '#4A90E2';

let scoreChart, radarChart, obesityChart, bmiChart;
let carbChart, proteinChart, fatChart;

document.addEventListener('DOMContentLoaded', () => {
    const today = new Date().toISOString().split('T')[0];

    const dietInput = document.getElementById('dietDate');
    const diabetesInput = document.getElementById('diabetesDate');
    const obesityInput = document.getElementById('obesityDate');

    if (dietInput) dietInput.value = today;
    if (diabetesInput) diabetesInput.value = today;
    if (obesityInput) obesityInput.value = today;

    loadRadarSection(today);
    loadDiabetesSection(today);
    loadObesitySection(today);
    loadCommonData(today);

    if (dietInput) dietInput.addEventListener('change', (e) => loadRadarSection(e.target.value));
    if (diabetesInput) diabetesInput.addEventListener('change', (e) => loadDiabetesSection(e.target.value));
    if (obesityInput) obesityInput.addEventListener('change', (e) => loadObesitySection(e.target.value));
});

// 1. 식단 분석 (레이더)
function loadRadarSection(date) {
    fetch(`/api/report/daily?date=${date}`)
        .then(res => res.json())
        .then(data => {
            const chartArea = document.getElementById('dietChartArea');
            const noDataMsg = document.getElementById('dietNoData');

            chartArea.style.display = 'block';
            if(noDataMsg) noDataMsg.style.display = 'none';

            if (!data.hasData) {
                updateRadarChart(null);
            } else {
                updateRadarChart(data);
            }
        })
        .catch(err => console.error("Radar load error:", err));
}

// 2. 당뇨 위험도
function loadDiabetesSection(date) {
    fetch(`/api/report/daily?date=${date}`)
        .then(res => res.json())
        .then(data => {
            const scoreBadge = document.getElementById('diabetesScoreBadge');
            const scoreNum = scoreBadge.querySelector('.score-num');

            // 요소 선택
            const scoreUnit = scoreBadge.querySelector('.score-unit');
            const scoreLabel = scoreBadge.querySelector('.score-label');

            const riskText = document.getElementById('diabetesRiskText');
            const commentBox = document.getElementById('diabetesComment');

            const now = new Date();
            const offset = now.getTimezoneOffset() * 60000;
            const today = new Date(now.getTime() - offset).toISOString().split('T')[0];

            if (!data.hasData) {
                if (date === today) {
                    // Case 1: 오늘 (분석 대기)
                    scoreNum.innerText = '분석 중';
                    scoreNum.style.fontSize = '24px';

                    if(scoreUnit) scoreUnit.style.display = 'none';
                    if(scoreLabel) scoreLabel.style.display = 'none';

                    riskText.innerText = '결과 대기';
                    riskText.className = 'risk-level-text text-excellent'; // 파란색

                    commentBox.innerText = '오늘 기록의 당뇨 위험도 분석은 12시가 지난 후 확인 할 수 있습니다.';
                    scoreBadge.style.background = 'linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%)';
                } else {
                    // Case 2: 과거 (기록 없음)
                    scoreNum.innerText = '-';
                    scoreNum.style.fontSize = '';

                    if(scoreUnit) scoreUnit.style.display = '';
                    if(scoreLabel) scoreLabel.style.display = '';

                    riskText.innerText = '-';
                    riskText.className = 'risk-level-text';

                    commentBox.innerText = '기록된 데이터가 없습니다.';
                    scoreBadge.style.background = '#ccc';
                }
            } else {
                if (date === today) {
                    // Case 1: 오늘 (분석 대기)
                    scoreNum.innerText = '분석 중';
                    scoreNum.style.fontSize = '24px';

                    if(scoreUnit) scoreUnit.style.display = 'none';
                    if(scoreLabel) scoreLabel.style.display = 'none';

                    riskText.innerText = '결과 대기';
                    riskText.className = 'risk-level-text text-excellent'; // 파란색

                    commentBox.innerText = '오늘 기록의 당뇨 위험도 분석은 12시가 지난 후 확인 할 수 있습니다.';
                    scoreBadge.style.background = 'linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%)';
                }
                else{
                // 데이터 있음
                                const score = data.diabetesScore;
                                const rLevel = data.diabetesRiskLevel ? data.diabetesRiskLevel : '';

                                scoreNum.style.fontSize = '';
                                scoreNum.innerText = score;

                                if(scoreUnit) scoreUnit.style.display = '';
                                if(scoreLabel) scoreLabel.style.display = '';

                                riskText.className = 'risk-level-text';

                                if (rLevel === 'EXCELLENT') {
                                    riskText.innerText = '최고예요 (EXCELLENT)';
                                    riskText.classList.add('text-excellent');
                                    scoreBadge.style.background = 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)';
                                } else if (rLevel === 'GOOD') {
                                    riskText.innerText = '좋아요 (GOOD)';
                                    riskText.classList.add('text-good');
                                    scoreBadge.style.background = 'linear-gradient(135deg, #1DAB87 0%, #20c997 100%)';
                                } else if (rLevel === 'NORMAL') {
                                    riskText.innerText = '보통 (NORMAL)';
                                    riskText.classList.add('text-normal');
                                    scoreBadge.style.background = 'linear-gradient(135deg, #f6d365 0%, #fda085 100%)';
                                } else if (rLevel === 'WARNING') {
                                    riskText.innerText = '주의 (WARNING)';
                                    riskText.classList.add('text-warning');
                                    scoreBadge.style.background = 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)';
                                } else if (rLevel === 'DANGER') {
                                    riskText.innerText = '위험 (DANGER)';
                                    riskText.classList.add('text-danger');
                                    scoreBadge.style.background = 'linear-gradient(135deg, #ff0844 0%, #ffb199 100%)';
                                } else {
                                    riskText.innerText = rLevel;
                                    scoreBadge.style.background = '#ccc';
                                }

                                if (data.diabetesComment) {
                                    commentBox.innerHTML = data.diabetesComment;
                                } else {
                                    commentBox.innerText = "분석 내용이 없습니다.";
                                }
                }

            }
        })
        .catch(err => console.error("Diabetes load error:", err));
}

// 3. 비만 위험도 (색상 통일 로직 적용)
function loadObesitySection(date) {
    fetch(`/api/report/daily?date=${date}`)
        .then(res => res.json())
        .then(data => {
            const percent = Math.round(data.obesityProbability || 0);
            const percentText = document.getElementById('obesityPercent');
            const commentText = document.getElementById('obesityCommentText');

            let message = "";
            let color = "";

            // [색상 및 멘트 결정 로직]
            if (!data.hasData) {
                message = "식사 기록이 없어 분석할 수 없습니다.";
                color = "#888"; // 회색
            } else if (percent <= 10) {
                message = "오늘 식단은 매우 안정적입니다.\n지금처럼 깔끔한 식단을 잘 유지하고 있어요!";
                color = "#1DAB87"; // 진한 초록
            } else if (percent <= 30) {
                message = "전반적으로 균형 잡힌 식단입니다.\n한두 끼만 더 가볍게 하면 더욱 좋습니다.";
                color = "#3BCB7A"; // 연한 초록
            } else if (percent <= 50) {
                message = "조금 과한 부분이 있어요.\n다음 식사에서는 탄수화물·지방을 조금만 줄여보세요.";
                color = "#FFC857"; // 노랑
            } else if (percent <= 70) {
                message = "오늘 식단은 다소 무겁습니다.\n수분을 충분히 섭취하고 저녁은 가볍게 추천드립니다.";
                color = "#FF9F40"; // 주황
            } else if (percent < 90) {
                message = "위험도가 꽤 높아요.\n당류·나트륨 섭취가 많지 않았는지 체크해보세요.";
                color = "#FF6B6B"; // 연한 빨강
            } else {
                message = "오늘 식단은 건강에 부담이 될 수 있는 수준입니다.\n내일은 가벼운 식사로 조절해보세요.";
                color = "#FF4D4D"; // 진한 빨강
            }

            // 1) 퍼센트 텍스트 색상 적용
            percentText.innerText = `${percent}%`;
            percentText.style.color = color;

            // 2) 코멘트 적용
            if(commentText) {
                commentText.innerHTML = `<strong>${message.replace(/\n/g, "<br>")}</strong>`;
                commentText.style.color = color;
            }

            // 3) [핵심] 결정된 색상을 차트 함수로 전달
            updateObesityChart(percent, color);
        })
        .catch(err => console.error("Obesity load error:", err));
}

// 4. 공통 트렌드
function loadCommonData(date) {
    fetch(`/api/report/daily?date=${date}`)
        .then(res => res.json())
        .then(data => {
            updateCommonCharts(data);
        })
        .catch(err => console.error("Common load error:", err));
}


// --- 차트 함수들 ---

function updateRadarChart(data) {
    const ctxRadar = document.getElementById('diabetesRadarChart');
    if (radarChart) radarChart.destroy();

    let myIntake, goal, maxVal, stepSize, suggestedMax;
    let isEmpty = false;

    if (data && data.radarMyIntake) {
        myIntake = data.radarMyIntake;
        goal = data.radarGoal || [100, 100, 100, 100, 100];
        maxVal = Math.max(...myIntake);
        suggestedMax = 120;
        stepSize = 20;
        if (maxVal > 120) {
            suggestedMax = Math.ceil(maxVal / 50) * 50;
            stepSize = 50;
        }
    } else {
        myIntake = [0, 0, 0, 0, 0];
        goal = [100, 100, 100, 100, 100];
        suggestedMax = 100;
        stepSize = 20;
        isEmpty = true;
    }

    const emptyTextPlugin = {
        id: 'emptyText',
        afterDraw: (chart) => {
            if (isEmpty) {
                const { ctx, width, height } = chart;
                ctx.save();
                ctx.textAlign = 'center';
                ctx.textBaseline = 'middle';
                ctx.font = 'bold 14px "Inter", sans-serif';
                ctx.fillStyle = '#888';
                ctx.fillText('기록 없음', width / 2, height / 2);
                ctx.restore();
            }
        }
    };

    radarChart = new Chart(ctxRadar, {
        type: 'radar',
        data: {
            labels: ['탄수화물', '단백질', '지방', '당류', '나트륨'],
            datasets: [{
                label: '나의 섭취(%)',
                data: myIntake,
                borderColor: isEmpty ? 'transparent' : COLOR_DANGER,
                backgroundColor: isEmpty ? 'transparent' : 'rgba(255, 107, 107, 0.2)',
                borderWidth: 2,
                pointRadius: isEmpty ? 0 : 3
            }, {
                label: '권장(%)',
                data: goal,
                borderColor: COLOR_PRIMARY,
                backgroundColor: 'transparent',
                borderDash: [5, 5],
                pointRadius: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                r: {
                    suggestedMin: 0,
                    suggestedMax: suggestedMax,
                    pointLabels: { font: { size: 11 } },
                    ticks: {
                        display: true,
                        stepSize: stepSize,
                        backdropColor: 'transparent',
                        font: { size: 9 },
                        color: '#999'
                    }
                }
            },
            plugins: { legend: { position: 'bottom', labels: { boxWidth: 10 } } }
        },
        plugins: [emptyTextPlugin]
    });
}

// [수정] 색상을 매개변수로 받아서 그림
function updateObesityChart(probability, color) {
    const ctxObesity = document.getElementById('obesityRiskChart');
    if (obesityChart) obesityChart.destroy();

    // 만약 color가 안 넘어왔으면 기본값 (혹시 모를 방어 코드)
    const riskColor = color || '#FF6B6B';

    obesityChart = new Chart(ctxObesity, {
        type: 'doughnut',
        data: {
            labels: ['위험도', '안전'],
            datasets: [{
                data: [probability, 100 - probability],
                backgroundColor: [riskColor, '#EEEEEE'], // 전달받은 색상 적용
                borderWidth: 0,
                hoverOffset: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '85%',
            plugins: { legend: { display: false }, tooltip: { enabled: false } },
            animation: { animateScale: true, animateRotate: true }
        }
    });
}

function updateCommonCharts(data) {
    document.getElementById('avgScoreBadge').innerText = `평균 ${data.averageScore}점`;
    const ctxScore = document.getElementById('scoreChart');
    if (scoreChart) scoreChart.destroy();

    scoreChart = new Chart(ctxScore, {
        type: 'line',
        data: {
            labels: data.scoreDates || [],
            datasets: [{
                label: '점수',
                data: data.scoreValues || [],
                borderColor: COLOR_PRIMARY,
                backgroundColor: 'rgba(29, 171, 135, 0.1)',
                fill: true,
                tension: 0,
                pointRadius: 3,
                pointBackgroundColor: '#fff',
                pointBorderColor: COLOR_PRIMARY
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            scales: { y: { min: 0, max: 100 }, x: { grid: { display: false } } },
            plugins: { legend: { display: false } }
        }
    });

    const ctxBmi = document.getElementById('bmiChart');
    if (bmiChart) bmiChart.destroy();
    const weightList = data.weightValues || [];
    const bmiData = data.bmiValues || [];

    let yMin = 10, yMax = 35;
    if (bmiData.length > 0) {
        const validValues = bmiData.filter(v => v != null);
        if (validValues.length > 0) {
            const minVal = Math.min(...validValues);
            const maxVal = Math.max(...validValues);
            yMin = Math.floor(minVal - 2);
            yMax = Math.ceil(maxVal + 2);
            if (yMin < 0) yMin = 0;
        }
    }

    bmiChart = new Chart(ctxBmi, {
        type: 'line',
        data: {
            labels: data.scoreDates || [],
            datasets: [{
                label: 'BMI',
                data: bmiData,
                borderColor: COLOR_INFO,
                backgroundColor: 'rgba(74, 144, 226, 0.1)',
                fill: true,
                tension: 0,
                pointRadius: 3,
                pointBackgroundColor: '#fff',
                pointBorderColor: COLOR_INFO
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            scales: {
                y: { beginAtZero: false, min: yMin, max: yMax, ticks: { stepSize: 1 } },
                x: { grid: { display: false } }
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) label += ': ';
                            if (context.parsed.y !== null) label += context.parsed.y;
                            const w = weightList[context.dataIndex];
                            if (w) return [label, `체중: ${w} kg`];
                            return label;
                        }
                    }
                }
            }
        }
    });

    const createStepChart = (ctxId, chartRef, label, dates, codes, color) => {
        const ctx = document.getElementById(ctxId);
        if (chartRef) chartRef.destroy();
        return new Chart(ctx, {
            type: 'line',
            data: {
                labels: dates || [],
                datasets: [{
                    label: label, data: codes || [],
                    borderColor: color, stepped: true, fill: false,
                    borderWidth: 2, pointRadius: 3
                }]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                scales: {
                    y: {
                        min: 0, max: 4,
                        ticks: {
                            stepSize: 1, font: { size: 9 },
                            callback: function(val) {
                                if (val === 1) return '저';
                                if (val === 2) return '균형';
                                if (val === 3) return '고';
                                return '';
                            }
                        }, grid: { drawBorder: false }
                    },
                    x: {
                        display: true,
                        ticks: { maxTicksLimit: 6, font: { size: 10 } },
                        grid: { display: false }
                    }
                },
                plugins: {
                    legend: { display: false },
                    title: { display: true, text: label, align: 'start', font: { size: 12 }, padding: { bottom: 5 } }
                }
            }
        });
    };

    carbChart = createStepChart('carbChart', carbChart, '탄수화물', data.dietDates, data.carbCodes, '#FFC857');
    proteinChart = createStepChart('proteinChart', proteinChart, '단백질', data.dietDates, data.proteinCodes, '#1DAB87');
    fatChart = createStepChart('fatChart', fatChart, '지방', data.dietDates, data.fatCodes, '#FF6B6B');

    const ul = document.getElementById('topMealsList');
    ul.innerHTML = '';
    if (data.topMeals && data.topMeals.length > 0) {
        data.topMeals.forEach((m, i) => {
            const li = document.createElement('li');
            const rank = i + 1;
            const category = m.mealTime ? m.mealTime : '기타';

            let rankClass = '';
            if (rank === 1) rankClass = 'rank-1';
            else if (rank === 2) rankClass = 'rank-2';
            else if (rank === 3) rankClass = 'rank-3';

            li.innerHTML = `
                <div style="display:flex; align-items:center;">
                    <span class="rank-badge ${rankClass}">${rank}</span>
                    <span class="meal-name">${m.mealName}</span>
                </div>
                <span class="meal-cat">${category}</span>
            `;
            ul.appendChild(li);
        });
    } else {
        ul.innerHTML = '<p style="color:#999; text-align:center; padding: 20px;">아직 식사 기록이 충분하지 않습니다.</p>';
    }
}
// -------------------------- 운동 추천 기능 추가 --------------------------

function loadExerciseRecommend() {
    const userIdEl = document.querySelector("body").getAttribute("data-user-id");
    let userId = userIdEl ? userIdEl : null;

    if (!userId) {
        console.warn("운동 추천: userId 없음");
        return;
    }

    fetch(`/exercise/recommend/${userId}`)
        .then(res => res.json())
        .then(data => renderExerciseSection(data))
        .catch(err => console.error("Exercise load error:", err));
}

function renderExerciseSection(data) {
    const warmup = data.routine?.warmup;
    const main = data.routine?.main;
    const cool = data.routine?.cool;

    const warmupBox = document.querySelector(".exercise-row > div:nth-child(1)");
    const mainBox = document.querySelector(".exercise-row > div:nth-child(2)");
    const coolBox = document.querySelector(".exercise-row > div:nth-child(3)");

    renderExerciseCard(warmupBox, warmup, "준비운동");
    renderExerciseCard(mainBox, main, "본운동");
    renderExerciseCard(coolBox, cool, "정리운동");
}

function renderExerciseCard(container, item, title) {
    if (!container) return;

    if (!item) {
        container.innerHTML = `
            <div class="exercise-block">
                <h4 class="exercise-title">${title}</h4>
                <div class="exercise-card placeholder">
                    <p class="no-video-text">추천 데이터 없음</p>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="exercise-block">
            <h4 class="exercise-title">${title}</h4>
            <div class="exercise-card">
                <div class="exercise-thumbnail">
                    <img src="${item.thumb || '/images/icon/no_video.png'}" alt="영상 썸네일"/>
                </div>
                <div class="exercise-info">
                    <p class="exercise-name">${item.name}</p>
                    ${
                        item.url
                            ? `<a href="${item.url}" class="exercise-btn primary-btn" target="_blank">영상 보기 ▶</a>`
                            : `<p class="no-video-text">영상 없음</p>`
                    }
                </div>
            </div>
        </div>
    `;
}
