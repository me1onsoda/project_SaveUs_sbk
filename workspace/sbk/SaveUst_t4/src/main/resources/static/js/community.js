// /static/js/community.js

document.addEventListener('DOMContentLoaded', () => {
    // 게시글 내용 '더보기' 토글 기능
    document.querySelectorAll('.post-content-container').forEach(container => {
        const contentText = container.querySelector('.post-content-text');
        const moreButton = container.querySelector('.more-button');
        if (contentText.scrollHeight > contentText.clientHeight) {
            moreButton.classList.remove('hidden');
            moreButton.addEventListener('click', () => {
                contentText.classList.add('expanded');
                moreButton.style.display = 'none';
            });
        }
    });

    // 1-f. 좋아요 토글 및 카운트 (AJAX 적용)
    document.querySelectorAll('.like-button').forEach(button => {
        button.addEventListener('click', async () => {
            const postId = button.dataset.postId;
            const postCard = button.closest('.post-card');
            const likeCountSpan = postCard.querySelector('.like-count');
            const likeIcon = button.querySelector('span');

            //UI 즉시 업데이트 (Optimistic UI)
            const isActive = button.classList.toggle('active');
            likeIcon.textContent = isActive ? '❤️' : '🤍';

            // (임시) 카운트 즉시 반영
            let currentCount = parseInt(likeCountSpan.textContent.split(' ')[0]);
            currentCount = isActive ? currentCount + 1 : currentCount - 1;
            likeCountSpan.textContent = `${currentCount} likes`;

            try {
                //서버에 AJAX 요청 (CommunityApiController)
                const response = await fetch(`/api/posts/${postId}/like`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                });

                if (!response.ok) {
                    throw new Error('Like request failed');
                }

                const data = await response.json();

                // 서버 응답으로 UI 최종 동기화
                if (data.success) {
                    likeCountSpan.textContent = `${data.newLikeCount} likes`;
                } else {
                    // 서버 실패 시 UI 롤백
                    button.classList.toggle('active'); // 원상 복구
                    likeIcon.textContent = isActive ? '🤍' : '❤️';
                }
            } catch (error) {
                console.error('Error toggling like:', error);
                // 에러 발생 시 UI 롤백
                button.classList.toggle('active');
                likeIcon.textContent = isActive ? '🤍' : '❤️';
            }
        });
    });


    // 미지 캐러셀 (다중 이미지 넘기기)
    document.querySelectorAll('.post-media-carousel').forEach(carousel => {
        const inner = carousel.querySelector('.carousel-inner');
        const images = carousel.querySelectorAll('.carousel-image');
        if (images.length <= 1) return;

        const prevBtn = carousel.querySelector('.carousel-control.prev');
        const nextBtn = carousel.querySelector('.carousel-control.next');
        const indicatorContainer = carousel.querySelector('.carousel-indicator');
        let currentIndex = 0;

        images.forEach((_, index) => {
            const dot = document.createElement('span');
            dot.classList.add('dot');
            if (index === 0) dot.classList.add('active');
            dot.addEventListener('click', () => updateCarousel(index));
            indicatorContainer.appendChild(dot);
        });
        const dots = indicatorContainer.querySelectorAll('.dot');

        function updateCarousel(newIndex) {
            if (newIndex < 0) newIndex = images.length - 1;
            else if (newIndex >= images.length) newIndex = 0;

            currentIndex = newIndex;
            inner.style.transform = `translateX(${-currentIndex * 100}%)`;
            dots.forEach(dot => dot.classList.remove('active'));
            dots[currentIndex].classList.add('active');
        }

        prevBtn.addEventListener('click', () => updateCarousel(currentIndex - 1));
        nextBtn.addEventListener('click', () => updateCarousel(currentIndex + 1));
    });

    //
    // 모달 창 제어 (댓글/상세 보기)
    const modal = document.getElementById('post-modal');
    const closeButton = modal.querySelector('.close-button');
    const modalBody = modal.querySelector('.modal-body-container');

    // 댓글 보기 버튼 클릭 이벤트
    document.querySelectorAll('[data-modal-target="post-modal"]').forEach(button => {
        button.addEventListener('click', async (event) => {
            const postId = button.dataset.postId;
            const clickedPostCard = button.closest('.post-card'); // 클릭한 카드 찾기

            modalBody.innerHTML = '<h2>Loading...</h2>';
            modal.style.display = 'block';

            try {
                const commentsResponse = await fetch(`/api/posts/${postId}/comments`);
                if (!commentsResponse.ok) {
                   throw new Error(`HTTP error! status: ${commentsResponse.status}`);
                }
                const comments = await commentsResponse.json();

                // DOM에서 실제 게시글 정보 가져오기 (findPostDataInDOM 함수 대체)
                const postData = extractPostDataFromDOM(clickedPostCard);

                // 모달 콘텐츠 렌더링
                renderModalContent(postData, comments);

                //모달 내부의 댓글 '게시' 버튼에 이벤트 리스너 추가
                addModalCommentSubmitListener();

            } catch (error) {
                console.error('Error fetching post data:', error);
                modalBody.innerHTML = '<h2>데이터 로드 실패.</h2>';
            }
        });
    });

    // 모달 닫기
    closeButton.addEventListener('click', () => modal.style.display = 'none');
    window.addEventListener('click', (event) => {
        if (event.target === modal) modal.style.display = 'none';
    });

    // DOM에서 게시물 데이터를 추출하는 헬퍼 함수
    function extractPostDataFromDOM(postCardElement) {
        if (!postCardElement) {
            console.error("Post card element not found!");
            return { postId: "error", content: "데이터를 찾을 수 없습니다.", authorNickname: "Unknown", authorProfileImageUrl: "", imageUrls: [] };
        }

        const authorName = postCardElement.querySelector('.author-name')?.textContent || 'Unknown';
        const avatarUrl = postCardElement.querySelector('.post-avatar')?.src || '';
        const content = postCardElement.querySelector('.post-content-text')?.textContent || '';
        const images = postCardElement.querySelectorAll('.carousel-image');
        const imageUrls = Array.from(images).map(img => img.src);
        const postId = postCardElement.querySelector('.like-button')?.dataset.postId; // like 버튼에서 postId 가져오기

        return { postId, content, authorNickname: authorName, authorProfileImageUrl: avatarUrl, imageUrls };
    }

    // 달 렌더링 헬퍼 함수
    function renderModalContent(post, comments) {
        modalBody.innerHTML = `
            <div class="modal-body-container">
                <!-- 왼쪽: 이미지 캐러셀 영역 -->
                <div class="modal-post-media">
                    ${renderCarouselHtml(post.imageUrls)} <!-- 캐러셀 HTML 생성 함수 사용 -->
                </div>
                <!-- 오른쪽: 댓글 및 상세 내용 영역 -->
                <div class="modal-comments-area">
                    <div class="modal-post-header">
                        <img src="${post.authorProfileImageUrl}" alt="${post.authorNickname}" class="post-avatar">
                        <span class="author-name">${post.authorNickname}</span>
                    </div>

                    <div class="modal-post-content">
                        <p>${post.content}</p>
                    </div>

                    <div class="modal-comments-list">
                        ${comments.length > 0 ? comments.map(c => renderCommentHtml(c)).join('') : '<p class="no-comments">아직 댓글이 없습니다.</p>'}
                    </div>

                    <div class="modal-comment-input">
                        <!-- [수정] data-post-id 추가 -->
                        <input type="text" placeholder="댓글 달기..." data-post-id="${post.postId}" id="modal-comment-input-field">
                        <button class="post-comment-btn" id="modal-comment-submit-btn" data-post-id="${post.postId}">게시</button>
                    </div>
                </div>
            </div>
        `;
    }

    // 모달 캐러셀 HTML 생성기
    function renderCarouselHtml(imageUrls) {
        if (!imageUrls || imageUrls.length === 0) {
            return '<img src="https://placehold.co/400x400/eeeeee/cccccc?text=No+Image" alt="No Image" class="modal-carousel-image">';
        }
        const imageTags = imageUrls.map(url => `<img src="${url}" alt="Post Image" class="modal-carousel-image">`).join('');
        return `<div class="modal-carousel-inner">${imageTags}</div>`;
    }

    // 댓글 아이템 HTML 생성기 (재사용 위함)
    function renderCommentHtml(comment) {
        return `
            <div class="comment-item" data-comment-id="${comment.commentId}">
                <span class="comment-author">
                    <img src="${comment.authorProfileImageUrl || 'https://placehold.co/32x32/eeeeee/cccccc?text=U'}" alt="" class="comment-avatar">
                    <strong>${comment.authorNickname}</strong>
                </span>
                <span class="comment-text">${comment.content}</span>
                <span class="comment-time">${comment.timeAgo}</span>
            </div>
        `;
    }

    // 댓글 입력 (AJAX)
    document.querySelectorAll('.comment-submit-btn').forEach(button => {
        button.addEventListener('click', async (e) => {
            const postId = e.target.dataset.postId;
            const inputField = e.target.previousElementSibling; // 버튼 바로 앞의 input
            const content = inputField.value;

            if (!content.trim()) {
                alert("댓글 내용을 입력하세요.");
                return;
            }

            // (간단 버전: 그냥 페이지 리로드)
            // 여기서는 AJAX를 사용하되, 성공 시 페이지 리로드를 하여 새 댓글 + 카운트를 갱신합니다.

            try {
                const response = await postComment(postId, content);
                if (response.success) {
                    location.reload(); // 성공 시 페이지 새로고침
                } else {
                    alert("댓글 게시에 실패했습니다: " + response.message);
                }
            } catch (error) {
                 alert("댓글 전송 중 오류가 발생했습니다.");
            }
        });
    });

    // 모달 내부의 '게시' 버튼 이벤트 (비동기 갱신 방식)
    function addModalCommentSubmitListener() {
        const modalSubmitBtn = document.getElementById('modal-comment-submit-btn');
        const modalInput = document.getElementById('modal-comment-input-field');

        if (!modalSubmitBtn) return;

        modalSubmitBtn.addEventListener('click', async () => {
            const postId = modalSubmitBtn.dataset.postId;
            const content = modalInput.value;

            if (!content.trim()) {
                alert("댓글 내용을 입력하세요.");
                return;
            }

            modalSubmitBtn.disabled = true; // 중복 클릭 방지
            modalSubmitBtn.textContent = "게시 중...";

            try {
                const response = await postComment(postId, content);

                if (response.success) {
                    modalInput.value = ''; // 입력창 비우기

                    // 새 댓글 DOM에 추가
                    const commentList = document.querySelector('.modal-comments-list');
                    const newCommentHtml = renderCommentHtml(response.newComment);

                    // "댓글 없음" 메시지 제거
                    const noComments = commentList.querySelector('.no-comments');
                    if(noComments) noComments.remove();

                    commentList.insertAdjacentHTML('beforeend', newCommentHtml);

                } else {
                    alert("댓글 게시에 실패했습니다: " + response.message);
                }
            } catch (error) {
                alert("댓글 전송 중 오류가 발생했습니다.");
            } finally {
                 modalSubmitBtn.disabled = false;
                 modalSubmitBtn.textContent = "게시";
            }
        });
    }

    // [신규] 댓글 POST 요청 공통 함수
    async function postComment(postId, content) {
        const response = await fetch(`/api/posts/${postId}/comment`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: content })
        });
        if (!response.ok) {
            throw new Error('Comment post failed');
        }
        return await response.json();
    }


});