

// /static/js/community.js

document.addEventListener('DOMContentLoaded', () => {

    // 현재 로그인한 사용자 ID (HTML에서 읽어옴)
    const postListContainer = document.querySelector('.post-list');
    const CURRENT_USER_ID = postListContainer ? parseInt(postListContainer.dataset.currentUserId, 10) : 0;

    // ----------------------------------------------------
    // 1. 사진 업로드 미리보기 (새 글 작성)
    // ----------------------------------------------------
    const newPostForm = document.querySelector('.new-post-form');
    const imageInput = document.getElementById('new-post-images');
    const previewContainer = document.getElementById('image-preview-container');
    const previewInner = document.getElementById('image-preview-inner');
    const prevBtn = document.getElementById('preview-prev-btn');
    const nextBtn = document.getElementById('preview-next-btn');
    const indicator = document.getElementById('preview-indicator');

    const fileNameDisplay = document.getElementById('file-name-display');

    let newPostFileObjects = [];
    let currentPreviewIndex = 0;

    if (imageInput) {
        imageInput.addEventListener('change', handleImagePreview);
    }

    function handleImagePreview(event) {
        const files = event.target.files;
        if (files.length === 0) return;
        newPostFileObjects.push(...Array.from(files));

        if (fileNameDisplay) {
            if (newPostFileObjects.length > 0) {
                fileNameDisplay.textContent = `${newPostFileObjects.length}개 이미지 선택됨`;
            } else {
                fileNameDisplay.textContent = '';
            }
        }

        event.target.value = null;
        renderNewPostPreview();
    }

    function renderNewPostPreview() {
        if(previewInner) previewInner.innerHTML = '';
        if(indicator) indicator.innerHTML = '';
        currentPreviewIndex = 0;

        if (newPostFileObjects.length === 0) {
            if(previewContainer) previewContainer.style.display = 'none';
            if (fileNameDisplay) fileNameDisplay.textContent = '';
            return;
        }

        if(previewContainer) previewContainer.style.display = 'block';

        newPostFileObjects.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                const imageUrl = e.target.result;
                const itemWrapper = document.createElement('div');
                itemWrapper.style.position = 'relative';
                itemWrapper.style.width = '100%';
                itemWrapper.style.flexShrink = '0';

                const imgElement = document.createElement('img');
                imgElement.src = imageUrl;
                imgElement.classList.add('carousel-image');
                itemWrapper.appendChild(imgElement);

                const deleteBtn = document.createElement('button');
                deleteBtn.type = 'button';
                deleteBtn.classList.add('delete-image-btn');
                deleteBtn.innerText = '×';
                deleteBtn.style.cssText = "position:absolute; top:5px; right:5px; z-index:10;";
                deleteBtn.addEventListener('click', (evt) => {
                    evt.stopPropagation();
                    newPostFileObjects.splice(index, 1);

                    if (fileNameDisplay) {
                        if (newPostFileObjects.length > 0) {
                            fileNameDisplay.textContent = `${newPostFileObjects.length}개 이미지 선택됨`;
                        } else {
                            fileNameDisplay.textContent = '';
                        }
                    }
                    renderNewPostPreview();
                });

                itemWrapper.appendChild(deleteBtn);
                if(previewInner) previewInner.appendChild(itemWrapper);

                if(indicator) {
                    const dot = document.createElement('span');
                    dot.classList.add('dot');
                    if (index === 0) dot.classList.add('active');
                    dot.addEventListener('click', () => updatePreviewCarousel(index));
                    indicator.appendChild(dot);
                }
            };
            reader.readAsDataURL(file);
        });

        if (newPostFileObjects.length > 1) {
            if(prevBtn) prevBtn.style.display = 'block';
            if(nextBtn) nextBtn.style.display = 'block';
            if(indicator) indicator.style.display = 'flex';
        } else {
            if(prevBtn) prevBtn.style.display = 'none';
            if(nextBtn) nextBtn.style.display = 'none';
            if(indicator) indicator.style.display = 'none';
        }
        updatePreviewCarousel(0);
    }

    function updatePreviewCarousel(newIndex) {
        const totalImages = newPostFileObjects.length;
        if (totalImages === 0) {
            if(previewContainer) previewContainer.style.display = 'none';
            return;
        }
        if (newIndex < 0) newIndex = totalImages - 1;
        else if (newIndex >= totalImages) newIndex = 0;
        currentPreviewIndex = newIndex;
        const offset = -currentPreviewIndex * 100;
        if(previewInner) previewInner.style.transform = `translateX(${offset}%)`;
        if(indicator) {
            const dots = indicator.querySelectorAll('.dot');
            dots.forEach(dot => dot.classList.remove('active'));
            if(dots[currentPreviewIndex]) {
                dots[currentPreviewIndex].classList.add('active');
            }
        }
    }

    if(prevBtn) prevBtn.addEventListener('click', () => updatePreviewCarousel(currentPreviewIndex - 1));
    if(nextBtn) nextBtn.addEventListener('click', () => updatePreviewCarousel(currentPreviewIndex + 1));

    if (newPostForm) {
        newPostForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const submitBtn = document.getElementById('new-post-submit-btn');
            const contentInput = document.getElementById('new-post-content');
            const content = contentInput.value;
            if (!content.trim() && newPostFileObjects.length === 0) {
                alert('내용이나 사진을 추가하세요.');
                return;
            }
            submitBtn.disabled = true;
            submitBtn.textContent = "Posting...";
            const formData = new FormData();
            formData.append('content', content);
            newPostFileObjects.forEach(file => {
                formData.append('images', file);
            });
            try {
                const response = await fetch('/community/post/new', {
                    method: 'POST',
                    body: formData
                });
                if (response.ok) {
                    location.reload();
                } else if (response.status === 401) {
                    alert('로그인이 필요합니다.');
                } else {
                    throw new Error('Post creation failed');
                }
            } catch (error) {
                console.error('Error creating post:', error);
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = "Post";
            }
        });
    }

    // ----------------------------------------------------
    // '더보기' (이벤트 위임)
    // ----------------------------------------------------
    if (postListContainer) {
        postListContainer.addEventListener('click', (e) => {
            if (e.target.classList.contains('more-button')) {
                const contentText = e.target.closest('.post-content-container').querySelector('.post-content-text');
                if (contentText) {
                    contentText.classList.add('expanded');
                    e.target.style.display = 'none';
                }
            }
        });
    }

    // 초기 '더보기' 활성화
    document.querySelectorAll('.post-content-container').forEach(container => {
        const contentText = container.querySelector('.post-content-text');
        const moreButton = container.querySelector('.more-button');
        if (contentText && moreButton && contentText.scrollHeight > contentText.clientHeight) {
            moreButton.classList.remove('hidden');
        }
    });

    // ----------------------------------------------------
    // 캐러셀 (초기화)
    // ----------------------------------------------------
    function initializeCarousel(carousel) {
        if (!carousel || carousel.id === 'image-preview-container' || carousel.id === 'edit-preview-container') {
            return;
        }
        const inner = carousel.querySelector('.carousel-inner');
        const images = carousel.querySelectorAll('.carousel-image');
        if (!inner || images.length <= 1) return;
        const prevBtn = carousel.querySelector('.carousel-control.prev');
        const nextBtn = carousel.querySelector('.carousel-control.next');
        const indicatorContainer = carousel.querySelector('.carousel-indicator');
        let currentIndex = 0;
        let dots = indicatorContainer ? indicatorContainer.querySelectorAll('.dot') : [];
        if (dots.length === 0 && indicatorContainer) {
             images.forEach((_, index) => {
                const dot = document.createElement('span');
                dot.classList.add('dot');
                if (index === 0) dot.classList.add('active');
                dot.addEventListener('click', () => updateCarousel(index));
                if(indicatorContainer) indicatorContainer.appendChild(dot);
            });
            dots = indicatorContainer.querySelectorAll('.dot');
        } else {
             indicatorContainer.querySelectorAll('.dot').forEach((dot, index) => {
                dot.replaceWith(dot.cloneNode(true));
            });
            indicatorContainer.querySelectorAll('.dot').forEach((dot, index) => {
                 dot.addEventListener('click', () => updateCarousel(index));
            });
        }
        function updateCarousel(newIndex) {
            if (newIndex < 0) newIndex = images.length - 1;
            else if (newIndex >= images.length) newIndex = 0;
            currentIndex = newIndex;
            inner.style.transform = `translateX(${-currentIndex * 100}%)`;
            dots.forEach(dot => dot.classList.remove('active'));
            if(dots[currentIndex]) dots[currentIndex].classList.add('active');
        }
        if(prevBtn) prevBtn.addEventListener('click', () => updateCarousel(currentIndex - 1));
        if(nextBtn) nextBtn.addEventListener('click', () => updateCarousel(currentIndex + 1));
    }
    document.querySelectorAll('.post-media-carousel').forEach(initializeCarousel);


    // ----------------------------------------------------
    // Popular / Latest 탭 전환
    // ----------------------------------------------------
    const feedFilters = document.querySelector('.feed-filters');
    if (feedFilters && postListContainer) {

        const initialLatestHtml = postListContainer.innerHTML;

        feedFilters.addEventListener('click', async (e) => {
            if (!e.target.classList.contains('filter-tab')) return;

            feedFilters.querySelectorAll('.filter-tab').forEach(tab => tab.classList.remove('active'));
            e.target.classList.add('active');

            const tabName = e.target.textContent;

            if (tabName === 'Latest') {
                postListContainer.innerHTML = initialLatestHtml;
                activateDynamicFeatures(postListContainer);

            } else if (tabName === 'Popular') {
                try {
                    postListContainer.innerHTML = '<p>Loading popular posts...</p>';

                    const response = await fetch('/api/posts/popular');
                    if (!response.ok) throw new Error('Failed to fetch popular posts');

                    const popularPosts = await response.json();

                    renderPostList(popularPosts, postListContainer);

                } catch (error) {
                    console.error('Error loading popular posts:', error);
                    // [수정] 널 예외처리 버그 수정
                    // console.error('Error loading popular posts:', error);
                    postListContainer.innerHTML = '<p>Error loading posts.</p>';
                }
            }
        });
    }

    // [신규] AJAX 로드 후 JS 기능 재활성화
    function activateDynamicFeatures(container) {
        container.querySelectorAll('.post-content-container').forEach(c => {
            const contentText = c.querySelector('.post-content-text');
            const moreButton = c.querySelector('.more-button');
            if (contentText && moreButton && contentText.scrollHeight > contentText.clientHeight) {
                moreButton.classList.remove('hidden');
            }
        });
        container.querySelectorAll('.post-media-carousel').forEach(initializeCarousel);
    }

    // [신규] 게시물 목록 렌더링
    function renderPostList(posts, container) {
        if (posts.length === 0) {
            container.innerHTML = '<p>No posts found.</p>';
            return;
        }
        const listHtml = posts.map(post => renderPostCardHtml(post)).join('');
        container.innerHTML = listHtml;
        activateDynamicFeatures(container);
    }

    // [신규] 개별 게시물 카드 HTML 생성
    function renderPostCardHtml(post) {
        let carouselHtml = '';
        if (post.imageUrls && post.imageUrls.length > 0) {
            const innerHtml = post.imageUrls.map(url => `<img src="${url}" alt="Meal Photo" class="carousel-image">`).join('');
            const prevBtn = post.imageUrls.length > 1 ? '<button class="carousel-control prev">&lt;</button>' : '';
            const nextBtn = post.imageUrls.length > 1 ? '<button class="carousel-control next">&gt;</button>' : '';
            const indicatorDots = post.imageUrls.length > 1
                ? post.imageUrls.map((_, i) => `<span class="dot ${i === 0 ? 'active' : ''}"></span>`).join('')
                : '';

            carouselHtml = `
                <div class="post-media-carousel">
                    <div class="carousel-inner">${innerHtml}</div>
                    ${prevBtn}
                    ${nextBtn}
                    <div class="carousel-indicator">${indicatorDots}</div>
                </div>
            `;
        }

        const likeButtonActive = post.likedByMe ? 'active' : '';
        const likeIcon = post.likedByMe ? '❤️' : '🤍';

        const actionMenuHtml = (CURRENT_USER_ID === post.userId) ? `
            <div class="post-actions-menu">
                <button class="post-edit-btn" data-post-id="${post.postId}">수정</button>
                <button class="post-delete-btn" data-post-id="${post.postId}">삭제</button>
            </div>
        ` : '';

        // [수정] 널 예외처리 버그 수정 (post.content가 null일 수 있음)
        const postContentHtml = (post.content || '').replace(/\n/g, '<br>');

        return `
            <article class="post-card card" data-post-id="${post.postId}" data-post-author-id="${post.userId}">
                <div class="post-header">
                    <a href="/user/profile/${post.userId}" class="author-link">
                        <img src="${post.authorProfileImageUrl || '/default-avatar.png'}" alt="Profile Avatar" class="post-avatar">
                    </a>
                    <div class="post-author-info">
                        <a href="/user/profile/${post.userId}" class="author-link">
                            <span class="author-name">${post.authorNickname}</span>
                        </a>
                        <span class="author-details">
                            <span class="author-persona">${post.authorPersona || ''}</span>
                            <span> · ${post.timeAgo}</span>
                        </span>
                    </div>
                    ${actionMenuHtml}
                    <div class="post-health-score">
                        Health Score: <strong>${post.healthScore}</strong>
                    </div>
                </div>

                <div class="post-view-mode">
                    ${carouselHtml}

                    <div class="post-actions-bar">
                        <button class="like-button ${likeButtonActive}" data-post-id="${post.postId}">
                            <span>${likeIcon}</span>
                            <span class="like-count-text">${post.likeCount}</span>
                        </button>
                        <button class="comment-button" data-modal-target="post-modal" data-post-id="${post.postId}">
                            <span title="댓글 보기">💬</span>
                            <span class="comment-count-text">${post.commentCount}</span>
                        </button>
                    </div>

                    <div class="post-content-container">
                        <p class="post-content-text">${postContentHtml}</p>
                        <button class="more-button hidden">...더보기</button>
                    </div>
                </div>

                <div class="post-edit-mode" style="display: none;">
                </div>

                <div class="post-comments-summary">
                    <div class="comment-input-area">
                        <input type="text" placeholder="댓글 달기..." data-post-id="${post.postId}" class="comment-input-field">
                        <button class="comment-submit-btn" data-post-id="${post.postId}">게시</button>
                    </div>
                </div>
            </article>
        `;
    }

    // ----------------------------------------------------
    // [수정] 1-c ~ 3 : *** 이벤트 위임(Event Delegation) 방식 ***
    // ----------------------------------------------------

    // --- 1. 좋아요 토글 핸들러 ---
    async function handleLikeToggle(button) {
        const postId = button.dataset.postId;
        const postCard = button.closest('.post-card');
        const likeCountSpan = postCard.querySelector('.like-count-text');
        const likeIcon = button.querySelector('span');

        const isActive = button.classList.toggle('active');
        likeIcon.textContent = isActive ? '❤️' : '🤍';
        let currentCount = parseInt(likeCountSpan.textContent);
        currentCount = isActive ? currentCount + 1 : currentCount - 1;
        likeCountSpan.textContent = currentCount;

        try {
            const response = await fetch(`/api/posts/${postId}/like`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
            });
            if (response.status === 401) {
                alert('로그인이 필요합니다.');
                button.classList.toggle('active');
                likeIcon.textContent = isActive ? '🤍' : '❤️';
                likeCountSpan.textContent = currentCount - (isActive ? 1 : -1);
                return;
            }
            if (!response.ok) { throw new Error('Like request failed'); }
            const data = await response.json();
            if (data.success) {
                likeCountSpan.textContent = data.newLikeCount;
            } else { throw new Error('Like update failed on server'); }
        } catch (error) {
            console.error('Error toggling like:', error);
            button.classList.toggle('active');
            likeIcon.textContent = isActive ? '🤍' : '❤️';
            likeCountSpan.textContent = currentCount - (isActive ? 1 : -1);
        }
    }

    // --- 3. 댓글 '게시' 버튼 (카드 하단) 핸들러 ---
    async function handleSubmitComment(button) {
        const postId = button.dataset.postId;
        const inputField = button.previousElementSibling; // 버튼 바로 앞의 input
        const content = inputField.value;

        if (!content.trim()) {
            alert("댓글 내용을 입력하세요.");
            return;
        }

        try {
            const response = await postComment(postId, content);
            if (response.success) {
                location.reload(); // [정책] 카드 하단 입력은 새로고침
            } else if (response.message === "Login required") {
                alert('로그인이 필요합니다.');
            } else {
                alert("댓글 게시에 실패했습니다: " + (response.message || ''));
            }
        } catch (error) {
            alert("댓글 전송 중 오류가 발생했습니다.");
        }
    }

    // --- 4. 게시물 '삭제' 핸들러 ---
    async function handleDeletePost(button) {
        const postId = button.dataset.postId;
        const postCard = button.closest('.post-card');

        if (!confirm('정말 이 게시물을 삭제하시겠습니까?')) {
            return;
        }

        try {
            const response = await fetch(`/api/posts/${postId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                postCard.remove(); // DOM에서 즉시 삭제
            } else if (response.status === 401 || response.status === 403) {
                alert('삭제할 권한이 없습니다.');
            } else {
                throw new Error('Failed to delete post');
            }
        } catch (error) {
            console.error('Error deleting post:', error);
            alert('삭제 중 오류가 발생했습니다.');
        }
    }

    // --- 5. 게시물 '수정' 폼 표시 핸들러 ---
    function handleShowEditForm(button) {
        const postCard = button.closest('.post-card');
        const postData = extractPostDataFromDOM(postCard);
        showPostEditForm(postCard, postData);
    }

    // --- [핵심 수정] 2. 모달 헬퍼 함수들 (블록 밖으로 이동) ---

    // (Helper) DOM에서 게시물 데이터 추출
    function extractPostDataFromDOM(postCardElement) {
        if (!postCardElement) {
            console.error("Post card element not found!");
            return { postId: "error", content: "데이터를 찾을 수 없습니다.", authorNickname: "Unknown", authorProfileImageUrl: "", imageUrls: [] };
        }
        const viewMode = postCardElement.querySelector('.post-view-mode');
        if (!viewMode) {
                console.error(".post-view-mode wrapper not found in post card!");
                return { postId: "error", content: "UI 래퍼를 찾을 수 없습니다.", authorNickname: "Unknown", authorProfileImageUrl: "", imageUrls: [] };
        }
        const authorName = postCardElement.querySelector('.author-name')?.textContent || 'Unknown';
        const avatarUrl = postCardElement.querySelector('.post-avatar')?.src || '';
        const content = viewMode.querySelector('.post-content-text')?.textContent || '';
        const images = viewMode.querySelectorAll('.carousel-image');
        const imageUrls = Array.from(images).map(img => {
            try {
                return new URL(img.src).pathname;
            } catch (e) {
                return img.src;
            }
        });
        const postId = postCardElement.dataset.postId;
        return { postId, content, authorNickname: authorName, authorProfileImageUrl: avatarUrl, imageUrls: imageUrls };
    }

    // (Helper) 모달 렌더링
    function renderModalContent(post, comments, currentUserId) {
        const modalBody = modal.querySelector('.modal-body-container');
        if (!modalBody) return;

        // [수정] 널 예외처리 버그 수정
        const postContentHtml = (post.content || '').replace(/\n/g, '<br>');

        modalBody.innerHTML = `
            <div class="modal-body-container">
                <div class="modal-post-media">
                    ${renderCarouselHtml(post.imageUrls)}
                </div>
                <div class="modal-comments-area">
                    <div class="modal-post-header">
                        <img src="${post.authorProfileImageUrl || '/default-avatar.png'}" alt="${post.authorNickname}" class="post-avatar">
                        <span class="author-name">${post.authorNickname}</span>
                    </div>
                    <div class="modal-post-content">
                        <p>${postContentHtml}</p>
                    </div>
                    <div class="modal-comments-list">
                        ${comments.length > 0 ? comments.map(c => renderCommentHtml(c, currentUserId)).join('') : '<p class="no-comments">아직 댓글이 없습니다.</p>'}
                    </div>
                    <div class="modal-comment-input">
                        <input type="text" placeholder="댓글 달기..." data-post-id="${post.postId}" id="modal-comment-input-field">
                        <button class="post-comment-btn" id="modal-comment-submit-btn" data-post-id="${post.postId}">게시</button>
                    </div>
                </div>
            </div>
        `;
    }

    // (Helper) 모달 캐러셀 HTML 생성기
    function renderCarouselHtml(imageUrls) {
        if (!imageUrls || imageUrls.length === 0) {
            return '<img src="https://placehold.co/600x600/eeeeee/cccccc?text=No+Image" alt="No Image" class="carousel-image" style="aspect-ratio: 1 / 1; object-fit: cover;">';
        }
        const imageTags = imageUrls.map(url =>
            `<img src="${url}" alt="Post Image" class="carousel-image">`
        ).join('');
        const prevBtnHtml = imageUrls.length > 1 ? '<button class="carousel-control prev">&lt;</button>' : '';
        const nextBtnHtml = imageUrls.length > 1 ? '<button class="carousel-control next">&gt;</button>' : '';
        const dotsHtml = imageUrls.length > 1
            ? imageUrls.map((_, index) =>
                `<span class="dot ${index === 0 ? 'active' : ''}" data-index="${index}"></span>`
                ).join('')
            : '';
        return `
            <div class="carousel-inner">${imageTags}</div>
            ${prevBtnHtml}
            ${nextBtnHtml}
            <div class="carousel-indicator">${dotsHtml}</div>
        `;
    }

    // (Helper) 댓글 아이템 HTML 생성기
    function renderCommentHtml(comment, currentUserId) {
        const actionsMenu = (currentUserId === comment.userId)
            ? `
            <div class="comment-actions">
                <button class="comment-edit-btn" data-comment-id="${comment.commentId}">수정</button>
                <button class="comment-delete-btn" data-comment-id="${comment.commentId}">삭제</button>
            </div>
            `
            : '';
        return `
            <div class="comment-item" data-comment-id="${comment.commentId}">
                <img src="${comment.authorProfileImageUrl || 'https://placehold.co/32x32/eeeeee/cccccc?text=U'}" alt="" class="comment-avatar">
                <div class="comment-text-content">
                    <span class="comment-author">
                        <strong>${comment.authorNickname}</strong>
                    </span>
                    <span class="comment-text">${comment.content}</span>
                    <span class="comment-time">${comment.timeAgo}</span>
                </div>
                ${actionsMenu}
            </div>
        `;
    }

    // (Helper) 댓글 POST 요청 공통 함수
    async function postComment(postId, content) {
        const response = await fetch(`/api/posts/${postId}/comment`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: content })
        });
        if (!response.ok && response.status !== 401) {
            throw new Error('Comment post failed');
        }
        return await response.json();
    }

    // --- 2. 댓글 모달 열기 핸들러 (본체) ---
    async function openCommentModal(button) {
        const postId = button.dataset.postId;
        const clickedPostCard = button.closest('.post-card');
        const modalBody = modal.querySelector('.modal-body-container');

        if (!clickedPostCard || !modalBody) {
            console.error('Could not find parent .post-card or modalBody');
            return;
        }
        modalBody.innerHTML = '<h2>Loading...</h2>'; // 리스너가 없는 modalBody 파괴
        modal.style.display = 'block';

        try {
            const commentsResponse = await fetch(`/api/posts/${postId}/comments`);
            if (!commentsResponse.ok) {
                throw new Error(`HTTP error! status: ${commentsResponse.status}`);
            }
            const comments = await commentsResponse.json();
            const postData = extractPostDataFromDOM(clickedPostCard);

            // 리스너가 없는 새 modalBody 생성
            renderModalContent(postData, comments, CURRENT_USER_ID);

            const modalCarouselElement = modal.querySelector('.modal-post-media');
            if (modalCarouselElement) {
                initializeCarousel(modalCarouselElement);
            }
        } catch (error) {
            console.error('Error fetching post data:', error);
            if (modalBody) modalBody.innerHTML = '<h2>데이터 로드 실패.</h2>';
        }
    }


    // --- 메인 피드(.post-list) 이벤트 위임 리스너 ---
    if (postListContainer) {
        postListContainer.addEventListener('click', async (e) => {

            // --- 1. 좋아요 토글 ---
            const likeButton = e.target.closest('.like-button');
            if (likeButton) {
                e.preventDefault();
                await handleLikeToggle(likeButton);
                return;
            }

            // --- 2. 댓글 모달 열기 ---
            const commentButton = e.target.closest('.comment-button');
            if (commentButton) {
                e.preventDefault();
                // [핵심 수정] 밖으로 꺼낸 함수 호출
                await openCommentModal(commentButton);
                return;
            }

            // --- 3. 댓글 '게시' 버튼 (카드 하단) ---
            const submitButton = e.target.closest('.comment-submit-btn');
            if (submitButton) {
                e.preventDefault();
                await handleSubmitComment(submitButton);
                return;
            }

            // --- 4. 게시물 '삭제' 버튼 ---
            const deleteButton = e.target.closest('.post-delete-btn');
            if (deleteButton) {
                e.preventDefault();
                await handleDeletePost(deleteButton);
                return;
            }

            // --- 5. 게시물 '수정' 버튼 ---
            const editButton = e.target.closest('.post-edit-btn');
            if (editButton) {
                e.preventDefault();
                handleShowEditForm(editButton);
                return;
            }

            // --- 6. 수정 '취소' 버튼 (동적 생성) ---
            const cancelEditButton = e.target.closest('.edit-cancel-btn');
            if (cancelEditButton) {
                e.preventDefault();
                const postCard = cancelEditButton.closest('.post-card');
                const viewMode = postCard.querySelector('.post-view-mode');
                const editMode = postCard.querySelector('.post-edit-mode');
                editMode.style.display = 'none';
                viewMode.style.display = 'block';
                editMode.innerHTML = '';
                return;
            }
        });
    }

    // --- [핵심 수정] 모달(Modal) 이벤트 리스너 (수정) ---
    const modal = document.getElementById('post-modal');
    if (modal) {
        const closeButton = modal.querySelector('.close-button');

        // 1. 모달 닫기
        if(closeButton) closeButton.addEventListener('click', () => modal.style.display = 'none');
        window.addEventListener('click', (event) => {
            if (event.target === modal) modal.style.display = 'none';
        });

        // 2. [핵심 수정] 모달 내부의 모든 클릭 이벤트를 'modal'에서 위임받아 처리
        // (modalBody가 아닌)
        modal.addEventListener('click', async (e) => {

            // --- 6-1. 모달 - 댓글 '삭제' ---
            const deleteBtn = e.target.closest('.comment-delete-btn');
            if (deleteBtn) {
                e.preventDefault();
                const commentId = deleteBtn.dataset.commentId;
                const commentItem = deleteBtn.closest('.comment-item');
                if (!confirm('이 댓글을 삭제하시겠습니까?')) { return; }
                try {
                    const response = await fetch(`/api/comments/${commentId}`, { method: 'DELETE' });
                    if (response.ok) {
                        commentItem.remove();
                    } else { alert('댓글 삭제에 실패했습니다.'); }
                } catch (error) { console.error('Error deleting comment:', error); }
                return;
            }

            // --- 6-2. 모달 - 댓글 '수정' (폼 열기) ---
            const editBtn = e.target.closest('.comment-edit-btn');
            if (editBtn) {
                e.preventDefault();
                const commentItem = editBtn.closest('.comment-item');
                const textContent = commentItem.querySelector('.comment-text-content');
                const originalText = commentItem.querySelector('.comment-text').textContent;
                const commentId = editBtn.dataset.commentId;

                const otherEditInput = modal.querySelector('.comment-edit-input');
                if(otherEditInput) {
                    const originalItem = otherEditInput.closest('.comment-item');
                    if (originalItem && originalItem.dataset.originalHtml) {
                         originalItem.innerHTML = originalItem.dataset.originalHtml;
                    }
                }
                commentItem.dataset.originalHtml = commentItem.innerHTML;
                textContent.innerHTML = `
                    <input type="text" class="comment-edit-input" value="${originalText}">
                    <button class="comment-save-btn" data-comment-id="${commentId}">저장</button>
                    <button class="comment-cancel-btn">취소</button>
                `;
                return;
            }

            // --- 6-3. 모달 - 댓글 '수정 취소' (동적) ---
            const cancelBtn = e.target.closest('.comment-cancel-btn');
            if (cancelBtn) {
                e.preventDefault();
                const commentItem = cancelBtn.closest('.comment-item');
                if (commentItem.dataset.originalHtml) {
                    commentItem.innerHTML = commentItem.dataset.originalHtml;
                }
                return;
            }

            // --- 6-4. 모달 - 댓글 '수정 저장' (동적) ---
            const saveBtn = e.target.closest('.comment-save-btn');
            if (saveBtn) {
                e.preventDefault();
                const commentId = saveBtn.dataset.commentId;
                const commentItem = saveBtn.closest('.comment-item');
                const newContent = commentItem.querySelector('.comment-edit-input').value;
                if (!newContent.trim()) { alert("내용을 입력하세요."); return; }
                try {
                    const response = await fetch(`/api/comments/${commentId}`, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ content: newContent })
                    });
                    if (response.ok) {
                        const result = await response.json();
                        const newCommentHtml = renderCommentHtml(result.updatedComment, CURRENT_USER_ID);
                        commentItem.outerHTML = newCommentHtml;
                    } else { alert('댓글 수정에 실패했습니다.'); }
                } catch (error) { console.error('Error updating comment:', error); }
                return;
            }

            // --- 6-5. 모달 - 댓글 '게시' ---
            const submitBtn = e.target.closest('#modal-comment-submit-btn');
            if (submitBtn) {
                e.preventDefault();
                const postId = submitBtn.dataset.postId;
                const modalInput = modal.querySelector('#modal-comment-input-field');
                const content = modalInput.value;
                if (!content.trim()) { alert("댓글 내용을 입력하세요."); return; }

                submitBtn.disabled = true;
                submitBtn.textContent = "게시 중...";
                try {
                    const response = await postComment(postId, content);
                    if (response.success) {
                        modalInput.value = '';
                        const commentList = modal.querySelector('.modal-comments-list');
                        const newCommentHtml = renderCommentHtml(response.newComment, CURRENT_USER_ID);
                        const noComments = commentList.querySelector('.no-comments');
                        if(noComments) noComments.remove();
                        commentList.insertAdjacentHTML('beforeend', newCommentHtml);
                    } else if (response.message === "Login required") {
                        alert('로그인이 필요합니다.');
                    } else { alert("댓글 게시에 실패했습니다: " + (response.message || '')); }
                } catch (error) { alert("댓글 전송 중 오류가 발생했습니다."); }
                finally {
                    submitBtn.disabled = false;
                    submitBtn.textContent = "게시";
                }
                return;
            }
        });
    } // if (modal) 끝


    // ----------------------------------------------------
    // [수정] 5단계: '통합 캐러셀' 폼 생성
    // ----------------------------------------------------
    function showPostEditForm(postCard, postData) {
        const viewMode = postCard.querySelector('.post-view-mode');
        const editMode = postCard.querySelector('.post-edit-mode');
        const originalContent = postData.content;

        const imagesToDelete = new Set();
        let newFilesForEdit = [];
        let currentEditPreviewIndex = 0;
        let existingImageUrls = [...postData.imageUrls];

        editMode.innerHTML = `
            <textarea class="post-edit-textarea">${originalContent}</textarea>
            <div id="edit-preview-container" class="post-media-carousel" style="display: none;">
                <div class="carousel-inner" id="edit-preview-inner"></div>
                <button type="button" class="carousel-control prev" id="edit-preview-prev-btn">&lt;</button>
                <button type="button" class="carousel-control next" id="edit-preview-next-btn">&gt;</button>
                <div class="carousel-indicator" id="edit-preview-indicator"></div>
            </div>
            <div class="custom-file-upload" style="margin-top: 15px;">
                <label for="edit-post-images" class="custom-file-button">
                    <span>📷</span>
                </label>
                <input type="file" name="images" multiple="multiple" accept="image/*"
                       class="post-edit-images" id="edit-post-images" style="display: none;">
                </div>
            <div class="edit-actions">
                <button class="edit-cancel-btn">Cancel</button>
                <button class="edit-save-btn">Save</button>
            </div>
        `;

        viewMode.style.display = 'none';
        editMode.style.display = 'block';

        const editPreviewContainer = editMode.querySelector('#edit-preview-container');
        const editPreviewInner = editMode.querySelector('#edit-preview-inner');
        const editIndicator = editMode.querySelector('#edit-preview-indicator');
        const editPrevBtn = editMode.querySelector('#edit-preview-prev-btn');
        const editNextBtn = editMode.querySelector('#edit-preview-next-btn');
        const newImagesInput = editMode.querySelector('.post-edit-images');

        newImagesInput.addEventListener('change', (event) => {
            const files = event.target.files;
            if (files.length === 0) return;
            newFilesForEdit.push(...Array.from(files));
            event.target.value = null;
            renderCombinedEditCarousel();
        });

        async function renderCombinedEditCarousel() {
            editPreviewInner.innerHTML = '';
            editIndicator.innerHTML = '';
            currentEditPreviewIndex = 0;
            const existingItems = existingImageUrls.map((url, index) => ({ type: 'existing', data: url, originalIndex: index }));
            const newItems = newFilesForEdit.map((file, index) => ({ type: 'new', data: file, originalIndex: index }));
            const combinedItems = [...existingItems, ...newItems];
            const totalImages = combinedItems.length;

            if (totalImages === 0) {
                editPreviewContainer.style.display = 'none';
                return;
            }
            editPreviewContainer.style.display = 'block';

            const urlPromises = combinedItems.map(item => getPreviewUrl(item));
            const allUrls = await Promise.all(urlPromises);

            allUrls.forEach((imageUrl, globalIndex) => {
                const item = combinedItems[globalIndex];
                const itemWrapper = document.createElement('div');
                itemWrapper.style.position = 'relative';
                itemWrapper.style.width = '100%';
                itemWrapper.style.flexShrink = '0';
                const imgElement = document.createElement('img');
                imgElement.src = imageUrl;
                imgElement.classList.add('carousel-image');
                itemWrapper.appendChild(imgElement);
                const deleteBtn = document.createElement('button');
                deleteBtn.type = 'button';
                deleteBtn.classList.add('delete-image-btn');
                deleteBtn.innerText = '×';
                deleteBtn.style.cssText = "position:absolute; top:5px; right:5px; z-index:10;";
                deleteBtn.addEventListener('click', (evt) => {
                    evt.stopPropagation();
                    if (item.type === 'existing') {
                        imagesToDelete.add(item.data);
                        existingImageUrls.splice(item.originalIndex, 1);
                    } else {
                        newFilesForEdit.splice(item.originalIndex, 1);
                    }
                    renderCombinedEditCarousel();
                });
                itemWrapper.appendChild(deleteBtn);
                editPreviewInner.appendChild(itemWrapper);
                const dot = document.createElement('span');
                dot.classList.add('dot');
                if (globalIndex === 0) dot.classList.add('active');
                dot.addEventListener('click', () => updateEditCarouselUI(globalIndex, totalImages));
                editIndicator.appendChild(dot);
            });

            if (totalImages > 1) {
                editPrevBtn.style.display = 'block';
                editNextBtn.style.display = 'block';
                editIndicator.style.display = 'flex';
            } else {
                editPrevBtn.style.display = 'none';
                editNextBtn.style.display = 'none';
                editIndicator.style.display = 'none';
            }
            updateEditCarouselUI(0, totalImages);
        }

        function updateEditCarouselUI(newIndex, totalImages) {
            if (totalImages === 0) return;
            if (newIndex < 0) newIndex = totalImages - 1;
            else if (newIndex >= totalImages) newIndex = 0;
            currentEditPreviewIndex = newIndex;
            const offset = -currentEditPreviewIndex * 100;
            editPreviewInner.style.transform = `translateX(${offset}%)`;
            const dots = editIndicator.querySelectorAll('.dot');
            dots.forEach(dot => dot.classList.remove('active'));
            if(dots[currentEditPreviewIndex]) dots[currentEditPreviewIndex].classList.add('active');
        }

        editPrevBtn.addEventListener('click', () => {
            const total = editIndicator.querySelectorAll('.dot').length;
            updateEditCarouselUI(currentEditPreviewIndex - 1, total);
        });
        editNextBtn.addEventListener('click', () => {
            const total = editIndicator.querySelectorAll('.dot').length;
            updateEditCarouselUI(currentEditPreviewIndex + 1, total);
        });

        function getPreviewUrl(item) {
            return new Promise((resolve, reject) => {
                if (item.type === 'existing') {
                    resolve(item.data);
                } else {
                    const reader = new FileReader();
                    reader.onload = (e) => resolve(e.target.result);
                    reader.onerror = (e) => reject(e);
                    reader.readAsDataURL(item.data);
                }
            });
        }

        editMode.querySelector('.edit-save-btn').addEventListener('click', async () => {
            await handlePostUpdate(postCard, viewMode, editMode, imagesToDelete, newFilesForEdit);
        });

        renderCombinedEditCarousel();
    }

    async function handlePostUpdate(postCard, viewMode, editMode, imagesToDelete, newFilesForEdit) {
        const postId = postCard.dataset.postId;
        const newContent = editMode.querySelector('.post-edit-textarea').value;
        const newImages = newFilesForEdit;

        const formData = new FormData();
        formData.append('content', newContent);
        if (newImages && newImages.length > 0) {
            Array.from(newImages).forEach(file => {
                formData.append('images', file);
            });
        }
        if (imagesToDelete.size > 0) {
            imagesToDelete.forEach(url => {
                formData.append('imagesToDelete', url);
            });
        }

        try {
            const response = await fetch(`/api/posts/${postId}`, {
                method: 'PUT',
                body: formData
            });
            if (response.ok) {
                const result = await response.json();
                const updatedPostCardHtml = renderPostCardHtml(result.updatedPost);
                postCard.outerHTML = updatedPostCardHtml;

                const newCardElement = postListContainer.querySelector(`[data-post-id="${postId}"]`);
                if (newCardElement) {
                    const newCarousel = newCardElement.querySelector('.post-media-carousel');
                    if (newCarousel) {
                        initializeCarousel(newCarousel);
                    }
                    const newContentContainer = newCardElement.querySelector('.post-content-container');
                    if (newContentContainer) {
                        const contentText = newContentContainer.querySelector('.post-content-text');
                        const moreButton = newContentContainer.querySelector('.more-button');
                        if (contentText && moreButton && contentText.scrollHeight > contentText.clientHeight) {
                            moreButton.classList.remove('hidden');
                        }
                    }
                }
            } else {
                throw new Error('Failed to update post');
            }
        } catch (error) {
            console.error('Error updating post:', error);
            alert('게시물 수정에 실패했습니다.');
        }
    }

});
