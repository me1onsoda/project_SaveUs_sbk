// /static/js/community.js

document.addEventListener('DOMContentLoaded', () => {
    // ----------------------------------------------------
    // 1-b. 게시글 내용 '더보기' 토글 기능
    // ----------------------------------------------------
    const contentContainers = document.querySelectorAll('.post-content-container');
    contentContainers.forEach(container => {
        const contentText = container.querySelector('.post-content-text');
        const moreButton = container.querySelector('.more-button');

        // 텍스트가 3줄을 초과하는지 확인하는 함수 (일반적인 방식)
        // 실제 높이를 계산하여 max-height보다 큰 경우에만 버튼을 표시
        if (contentText.scrollHeight > contentText.clientHeight) {
            moreButton.classList.remove('hidden');
            moreButton.addEventListener('click', () => {
                contentText.classList.add('expanded');
                moreButton.style.display = 'none';
            });
        }
    });

    // ----------------------------------------------------
    // 1-f. 좋아요 토글 및 카운트 (비동기 요청 가정)
    // ----------------------------------------------------
    document.querySelectorAll('.like-button').forEach(button => {
        button.addEventListener('click', () => {
            const postId = button.dataset.postId;
            const isActive = button.classList.toggle('active');
            const likeCountSpan = button.closest('.post-card').querySelector('.like-count');
            let currentCount = parseInt(likeCountSpan.textContent.split(' ')[0]);

            // 1. UI 즉시 업데이트
            button.innerHTML = isActive ? '<span>❤️</span>' : '<span>🤍</span>';
            currentCount = isActive ? currentCount + 1 : currentCount - 1;
            likeCountSpan.textContent = `${currentCount} likes`;

            // 2. 서버에 AJAX 요청 (실제 로직에서는 fetch()를 사용)
            console.log(`Post ${postId}: 좋아요 상태를 ${isActive}로 토글`);
            // fetch('/api/like', { method: 'POST', body: JSON.stringify({ postId: postId, action: isActive ? 'like' : 'unlike' }) })
            //   .then(response => response.json())
            //   .then(data => { /* 서버 응답 후 최종 카운트 업데이트 */ });
        });
    });

    // ----------------------------------------------------
    // 1-d. 이미지 캐러셀 (다중 이미지 넘기기)
    // ----------------------------------------------------
    document.querySelectorAll('.post-media-carousel').forEach(carousel => {
        const inner = carousel.querySelector('.carousel-inner');
        const images = carousel.querySelectorAll('.carousel-image');
        const prevBtn = carousel.querySelector('.carousel-control.prev');
        const nextBtn = carousel.querySelector('.carousel-control.next');
        const indicatorContainer = carousel.querySelector('.carousel-indicator');
        let currentIndex = 0;

        if (images.length <= 1) return; // 이미지가 1개 이하면 캐러셀 기능 비활성화

        // 인디케이터 생성
        images.forEach((_, index) => {
            const dot = document.createElement('span');
            dot.classList.add('dot');
            if (index === 0) dot.classList.add('active');
            dot.addEventListener('click', () => updateCarousel(index));
            indicatorContainer.appendChild(dot);
        });
        const dots = indicatorContainer.querySelectorAll('.dot');

        function updateCarousel(newIndex) {
            if (newIndex < 0) {
                newIndex = images.length - 1; // 끝에서 처음으로
            } else if (newIndex >= images.length) {
                newIndex = 0; // 처음에서 끝으로
            }
            currentIndex = newIndex;
            const offset = -currentIndex * 100;
            inner.style.transform = `translateX(${offset}%)`;

            // 인디케이터 업데이트
            dots.forEach(dot => dot.classList.remove('active'));
            dots[currentIndex].classList.add('active');
        }

        prevBtn.addEventListener('click', () => updateCarousel(currentIndex - 1));
        nextBtn.addEventListener('click', () => updateCarousel(currentIndex + 1));
    });

    // ----------------------------------------------------
    // 1-c. 모달 창 제어 (댓글/상세 보기)
    // ----------------------------------------------------
    const modal = document.getElementById('post-modal');
    const closeButton = modal.querySelector('.close-button');
    const modalBody = modal.querySelector('.modal-body-container');

    // 댓글 보기 버튼 클릭 이벤트
    document.querySelectorAll('[data-modal-target="post-modal"]').forEach(button => {
        button.addEventListener('click', async () => {
            const postId = button.dataset.postId;

            // 1. 모달 뼈대 초기화 및 표시
            modalBody.innerHTML = '<h2>Loading...</h2>';
            modal.style.display = 'block';

            // 2. 서버에서 상세 데이터 (댓글 목록 포함) AJAX로 가져오기
            try {
                // 이 엔드포인트는 HomeController.java에 추가한 /api/posts/{postId}/comments 엔드포인트를 사용
                const commentsResponse = await fetch(`/api/posts/${postId}/comments`);
                const comments = await commentsResponse.json();

                // 3. (임시) 게시글 상세 정보 (실제로는 별도 API 필요, 여기서는 postList에서 찾음)
                const postData = findPostDataInDOM(postId);

                // 4. 모달 콘텐츠 렌더링
                renderModalContent(postData, comments);

            } catch (error) {
                console.error('Error fetching post data:', error);
                modalBody.innerHTML = '<h2>데이터 로드 실패.</h2>';
            }
        });
    });

    // 모달 닫기
    closeButton.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    // ----------------------------------------------------
    // 모달 렌더링 헬퍼 함수
    // ----------------------------------------------------

    // DOM에서 기존 게시물 정보를 찾아 임시로 사용하는 함수 (개발 편의를 위해)
    function findPostDataInDOM(postId) {
        // 실제 운영 환경에서는 별도의 /api/posts/{postId} 엔드포인트에서 모든 데이터를 가져와야 합니다.
        // 여기서는 예시용 데이터만 반환
        return {
            postId: postId,
            content: "이것은 상세 모달에 표시될 게시글 내용입니다. 실제로는 매우 길 수 있습니다.",
            authorNickname: "TestUser",
            authorProfileImageUrl: "/images/avatars/sophia.png",
            imageUrls: ["/images/meals/plant-bowl.png", "/images/meals/salmon.png"], // 임시 다중 이미지
        };
    }

    function renderModalContent(post, comments) {
        modalBody.innerHTML = `
            <div class="modal-body-container">
                <!-- 왼쪽: 이미지 캐러셀 영역 -->
                <div class="modal-post-media">
                    ${renderCarouselHtml(post.imageUrls)}
                </div>
                <!-- 오른쪽: 댓글 및 상세 내용 영역 -->
                <div class="modal-comments-area">
                    <!-- 상세 내용 헤더 -->
                    <div class="modal-post-header">
                        <img src="${post.authorProfileImageUrl}" alt="${post.authorNickname}" class="post-avatar">
                        <span class="author-name">${post.authorNickname}</span>
                    </div>

                    <!-- 게시글 내용 -->
                    <div class="modal-post-content">
                        <p>${post.content}</p>
                    </div>

                    <!-- 댓글 목록 -->
                    <div class="modal-comments-list">
                        ${comments.map(c => `
                            <div class="comment-item">
                                <span class="comment-author">
                                    <img src="${c.authorProfileImageUrl || post.authorProfileImageUrl}" alt="" class="comment-avatar">
                                    <strong>${c.authorNickname}</strong>
                                </span>
                                <span class="comment-text">${c.content}</span>
                                <span class="comment-time">${c.timeAgo}</span>
                            </div>
                        `).join('')}
                    </div>

                    <!-- 댓글 입력 폼 (하단 고정) -->
                    <div class="modal-comment-input">
                        <input type="text" placeholder="댓글 달기..." data-post-id="${post.postId}">
                        <button class="post-comment-btn">게시</button>
                    </div>
                </div>
            </div>
        `;
        // 모달 캐러셀 기능 다시 활성화 (렌더링 후)
        // (복잡해지므로 JS에서는 생략하고, CSS만 적용)
    }

    function renderCarouselHtml(imageUrls) {
        // 모달 내부용 캐러셀 HTML 생성 (단순히 이미지를 나열)
        const imageTags = imageUrls.map(url => `<img src="${url}" alt="Post Image" class="modal-carousel-image">`).join('');
        return `
            <div class="modal-carousel-inner">
                ${imageTags}
            </div>
            <!-- (실제로는 여기에 모달용 캐러셀 제어 버튼도 필요) -->
        `;
    }
});