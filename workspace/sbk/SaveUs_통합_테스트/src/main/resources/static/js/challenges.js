// challenges.js

document.addEventListener('DOMContentLoaded', () => {

    const tabsContainer = document.querySelector('.challenge-tabs');
    const tabContents = document.querySelectorAll('.tab-content');
    const tabButtons = document.querySelectorAll('.tab-btn');

    // URL 파라미터에서 탭 정보 가져오기
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }

    // 탭 활성화 함수 (UI 업데이트)
    function activateTab(tabId) {
        // 모든 버튼 비활성화
        tabButtons.forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.tab === tabId) btn.classList.add('active');
        });

        // 모든 컨텐츠 숨기기
        tabContents.forEach(content => {
            content.classList.remove('active');
            if (content.id === tabId) content.classList.add('active');
        });
    }

    // 페이지 로드 시 실행: URL에 tab 파라미터가 있으면 해당 탭 열기
    const currentTab = getQueryParam('tab');
    if (currentTab === 'explore-tab') {
        activateTab('explore-tab');
    } else {
        // 기본값은 나의 챌린지
        activateTab('my-challenges-tab');
    }

    // 탭 클릭 이벤트
    if (tabsContainer) {
        tabsContainer.addEventListener('click', (e) => {
            const clickedTab = e.target.closest('.tab-btn');
            if (!clickedTab) return;

            const tabId = clickedTab.dataset.tab;

            // UI 변경
            activateTab(tabId);

            // URL을 변경하여 새로고침 해도 상태 유지 (history API 사용)
            // 기존 검색어(keyword, tag)가 있다면 유지해야 함
            const url = new URL(window.location);
            url.searchParams.set('tab', tabId);
            window.history.pushState({}, '', url);
        });
    }

    // "지금 챌린지 둘러보기" 버튼 클릭 시 처리
    const exploreNowBtn = document.querySelector('.btn-explore-now');
    if (exploreNowBtn) {
        exploreNowBtn.addEventListener('click', () => {
            activateTab('explore-tab');
            // URL 업데이트
            const url = new URL(window.location);
            url.searchParams.set('tab', 'explore-tab');
            window.history.pushState({}, '', url);
        });
    }
});