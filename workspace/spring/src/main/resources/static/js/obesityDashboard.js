document.addEventListener('DOMContentLoaded', () => {
    const percent = parseFloat(document.querySelector('.obesity-percent').innerText.replace('%', ''));
    const textSpan = document.querySelector('.obesityPercentText');

    let message = "";
    let color = "";

    if(percent == 0){
        message = "아직 식사기록이 없습니다.\n 식사를 기록해주세요!";
        color = "gray";
    }else if (percent <= 10) {
        message = "오늘 식단은 매우 안정적입니다.\n지금처럼 깔끔한 식단을 잘 유지하고 있어요!";
        color = "#1DAB87";
    } else if (percent <= 30) {
        message = "전반적으로 균형 잡힌 식단입니다.\n한두 끼만 더 가볍게 하면 더욱 좋습니다.";
        color = "#3BCB7A";
    } else if (percent <= 50) {
        message = "조금 과한 부분이 있어요.\n다음 식사에서는 탄수화물·지방을 조금만 줄여보세요.";
        color = "#FFC857";  // 노랑
    } else if (percent <= 70) {
        message = "오늘 식단은 다소 무겁습니다.\n수분을 충분히 섭취하고 저녁은 가볍게 추천드립니다.";
        color = "#FF9F40";
    } else if (percent < 90) {
        message = "위험도가 꽤 높아요.\n당류·나트륨 섭취가 많지 않았는지 체크해보세요.";
        color = "#FF6B6B";
    } else {
        message = "오늘 식단은 건강에 부담이 될 수 있는 수준입니다.\n내일은 가벼운 식사로 조절해보세요.";
        color = "#FF4D4D";
    }

    textSpan.innerHTML = `<strong>${message.replace(/\n/g, "<br>")}</strong>`;
    textSpan.style.color = color;
});


