let isChecked = false; //중복체크 버튼 클릭
let idAvailable = false; // 아이디 사용불가 (디폴트)

// 중복체크 버튼 클릭했을 때
function checkId(){
   idValue = document.getElementById('idInput').value; // input에 입력한 아이디

  if(idValue == ''){
    alert("아이디를 입력하세요");
    return;
  }
  fetch("/checkId.mb?id="+encodeURIComponent(idValue))
        .then(response=> response.text())
        .then(result => {
                 const msgSpan = document.getElementById('idCheckResult');
                 isChecked = true; // 중복체크 클릭시 true
                 if(result == "duplicate"){
                        msgSpan.textContent  = "이미 사용중인 아이디 입니다";
                        msgSpan.style.color="red";
                        idAvailable = false;
                 }else{
                        msgSpan.textContent = "사용가능한 아이디 입니다";
                        msgSpan.style.color="green";
                        idAvailable = true;
                 }
            })
            .catch(error => {
                    console.log("에러발생", error);
            })
}// CheckId

// 새로운 입력이 들어왔을떄
function resetIdCheck(){
    isChecked = false;
    idAvailable = false;
    const msgSpan = document.getElementById('idCheckResult');
    msgSpan.textContent = ''
    msgSpan.style.color = "gray";

}//resultIdCheck

// submit클릭했을떄
function validateForm(){
    alert(111)

    if(!isChecked){
        alert("아이디 중복체크 먼저")
        return false;
    }

    if(!idAvailable){ // !로 강제로 참으로 바꿈
        alert("아이디 사용중인 아이디");
        return false;
    }
    return true;

}
// 회원가입 페이지 이동
function register() {
    location.href = "/minsert.mb";
}

