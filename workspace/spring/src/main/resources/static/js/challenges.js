document.addEventListener('DOMContentLoaded', () => {

    // ============================================================
    // 탭 전환 로직
    // ============================================================
    const tabsContainer = document.querySelector('.challenge-tabs');
    const tabContents = document.querySelectorAll('.tab-content');
    const tabButtons = document.querySelectorAll('.tab-btn');

    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }

    function activateTab(tabId) {
        if(!tabButtons.length) return;

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
    // 리더보드 모달 로직 (오류 수정됨)
    // ============================================================

    const modal = document.getElementById('leaderboardModal');
    const closeBtn = modal ? modal.querySelector('.close-btn') : null;
    const list = document.getElementById('fullLeaderboardList');

    // 모달 열기 버튼을 안전하게 찾기
    const leaderboardBtn = document.querySelector('.btn-view-all');

    // 모달 열기 함수
    function openLeaderboardModal(e) {
        if(e) e.preventDefault(); // a 태그 링크 이동 방지
        console.log('Top 50 버튼 클릭됨!'); // [디버깅용 로그]

        if (!modal) {
            console.error('모달 요소를 찾을 수 없습니다.');
            return;
        }

        modal.style.display = 'flex';

        // 서버 데이터 요청
        fetch('/api/leaderboard')
            .then(response => {
                if(!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(data => {
                console.log('데이터 수신 성공:', data); // [디버깅용 로그]
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

    // 모달 닫기 함수
    function closeLeaderboardModal() {
        if (modal) modal.style.display = 'none';
    }

    // 이벤트 리스너 연결 [수정]
    if (leaderboardBtn) {
        leaderboardBtn.addEventListener('click', openLeaderboardModal);
        console.log('Top 50 버튼 이벤트 연결 완료'); // [디버깅용 로그]
    } else {
        console.warn('Top 50 버튼을 찾을 수 없습니다.');
    }

    // 'X' 버튼 클릭 시
    if (closeBtn) {
        closeBtn.addEventListener('click', closeLeaderboardModal);
    }

    // 모달 배경 클릭 시 닫기
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeLeaderboardModal();
        }
    });

});