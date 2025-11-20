/*
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

    // 모달 열기
    function openLeaderboardModal(e) {
        e.preventDefault();
        const modal = document.getElementById('leaderboardModal');
        const list = document.getElementById('fullLeaderboardList');

        // 1. 모달 띄우기
        modal.style.display = 'flex';

        // 2. 서버에서 전체 데이터 가져오기 (AJAX)
        fetch('/api/leaderboard')
            .then(response => response.json())
            .then(data => {
                list.innerHTML = ''; // 기존 목록 비우기

                // 데이터 하나씩 HTML로 만들기
                data.forEach((entry, index) => {
                    const li = document.createElement('li');
                    li.innerHTML = `
                        <span class="rank">${index + 1}</span>
                        <img src="${entry.userProfileImg || '/images/avatars/default.png'}" class="avatar">
                        <span class="username">${entry.userNickname}</span>
                        <span class="score">${entry.score}점</span>
                    `;
                    list.appendChild(li);
                });
            })
            .catch(err => console.error('랭킹 로딩 실패:', err));
    }

    // 모달 닫기
    function closeLeaderboardModal() {
        document.getElementById('leaderboardModal').style.display = 'none';
    }

    // 배경 클릭 시 닫기
    window.onclick = function(event) {
        const modal = document.getElementById('leaderboardModal');
        if (event.target == modal) {
            closeLeaderboardModal();
        }
    }
});*/
// challenges.js

document.addEventListener('DOMContentLoaded', () => {

    // ============================================================
    // 1. 탭 전환 로직 (기존 코드 유지)
    // ============================================================
    const tabsContainer = document.querySelector('.challenge-tabs');
    const tabContents = document.querySelectorAll('.tab-content');
    const tabButtons = document.querySelectorAll('.tab-btn');

    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }

    function activateTab(tabId) {
        tabButtons.forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.tab === tabId) btn.classList.add('active');
        });

        tabContents.forEach(content => {
            content.classList.remove('active');
            if (content.id === tabId) content.classList.add('active');
        });
    }

    // 초기 탭 설정
    const currentTab = getQueryParam('tab');
    if (currentTab === 'explore-tab') {
        activateTab('explore-tab');
    } else {
        activateTab('my-challenges-tab');
    }

    // 탭 클릭 이벤트 리스너
    if (tabsContainer) {
        tabsContainer.addEventListener('click', (e) => {
            const clickedTab = e.target.closest('.tab-btn');
            if (!clickedTab) return;

            const tabId = clickedTab.dataset.tab;
            activateTab(tabId);

            const url = new URL(window.location);
            url.searchParams.set('tab', tabId);
            window.history.pushState({}, '', url);
        });
    }

    // "지금 챌린지 둘러보기" 버튼
    const exploreNowBtn = document.querySelector('.btn-explore-now');
    if (exploreNowBtn) {
        exploreNowBtn.addEventListener('click', () => {
            activateTab('explore-tab');
            const url = new URL(window.location);
            url.searchParams.set('tab', 'explore-tab');
            window.history.pushState({}, '', url);
        });
    }


    // ============================================================
    // 2. 리더보드 모달 로직 (Community.js 스타일로 변경됨)
    // ============================================================

    const leaderboardBtn = document.querySelector('.btn-view-all'); // HTML의 '전체 순위 보기' 버튼
    const modal = document.getElementById('leaderboardModal');
    const closeBtn = modal ? modal.querySelector('.close-btn') : null;
    const list = document.getElementById('fullLeaderboardList');

    // (1) 모달 열기 함수 (내부 함수로 정의)
    function openLeaderboardModal(e) {
        e.preventDefault(); // a 태그 링크 이동 방지

        if (!modal) return;
        modal.style.display = 'flex';

        // 서버 데이터 요청
        fetch('/api/leaderboard')
            .then(response => response.json())
            .then(data => {
                list.innerHTML = ''; // 초기화

                data.forEach((entry, index) => {
                    const li = document.createElement('li');
                    const imgSrc = entry.userProfileImg ? entry.userProfileImg : '/images/avatars/default.png';

                    li.innerHTML = `
                        <span class="rank">${index + 1}</span>
                        <img src="${imgSrc}" class="avatar" alt="User">
                        <span class="username">${entry.userNickname}</span>
                        <span class="score">${entry.score}점</span>
                    `;
                    list.appendChild(li);
                });
            })
            .catch(err => console.error('랭킹 로딩 실패:', err));
    }

    // (2) 모달 닫기 함수
    function closeLeaderboardModal() {
        if (modal) modal.style.display = 'none';
    }

    // (3) 이벤트 리스너 연결 (여기가 핵심 변경 사항)

    // '전체 순위 보기' 버튼 클릭 시
    if (leaderboardBtn) {
        leaderboardBtn.addEventListener('click', openLeaderboardModal);
    }

    // 'X' 버튼 클릭 시
    if (closeBtn) {
        closeBtn.addEventListener('click', closeLeaderboardModal);
    }

    // 모달 배경 클릭 시 닫기 (window 전체에 걸어서 처리)
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeLeaderboardModal();
        }
    });

});