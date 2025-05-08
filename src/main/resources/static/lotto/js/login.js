/**
 * 
 */
 
 $(function() {

	window.addEventListener("keydown", (e) => {
		if(e.key == 'Enter') {
			console.log("test");
			if(notEmptyChk()) {
				login();
			}
		}
	});
	$('#submit').click(function() {
		login();
	});
 });
 
function login() {
	let param = {};
	param.member_id = $('#id').val();
	param.password = $('#password').val();
	console.log("submit", param);
	
	$.ajax({
		type:'post',
		dataType: "json",
		contentType: 'application/json',
		data: JSON.stringify(param),
		url:localStorage.getItem("bbsContext") + "/member/login",
		success:function(data) {
			
			console.log(data);
			if(data == true) {
				console.log("true");
				document.location.href = "http://localhost:8081/lotto/html/index.html";
			}
			
		},
		error:function(data,sataus,err) {
			alert("데이터 요청에 실패하였습니다.\r status : " + status);
		}
	});
}

function notEmptyChk() {

	let rtn = false;

	if(!isEmpty($('#id').val()) && !isEmpty($('#password').val())) {
		rtn = true;
	}

	return rtn;
}

// 빈 값 체크 함수
const isEmpty = (input) => {
	if (
		typeof input === "undefined" ||
		input === null ||
		input === "" ||
		input === "null" ||
		input.length === 0 ||
		(typeof input === "object" && !Object.keys(input).length)
		)
	{
		return true;
	}
	else return false;
}