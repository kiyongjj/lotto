/**
 * lotto script
 */
 
$(function() {

	localStorage.setItem("bbsContext", "http://localhost:8081");

	document.getElementById('drawDate').value = new Date().toISOString().substring(0, 10);

	selectRoundNumbers();

	$('#downloadExcel').click(function() {
		$.ajax({
			type:'post',
			dataType: "json",
			contentType: 'application/json',
//			data: JSON.stringify(param),
			url:localStorage.getItem("bbsContext") + "/lotto/api/excel/download",
			success:function(data) {
				
				console.log("naverLogin", data);
			},
			error:function(data,sataus,err) {
				alert("데이터 요청에 실패하였습니다.\r status : " + status);
			}
		});
	});
	
	// 로또 당첨번호 DB검색
	$('#selectLottoNumber').click(function() {
		console.log("selectLottoNumber");
		let param = {};
		const roundNo = document.getElementById('roundNumber');
		param.roundNo = roundNo.options[roundNo.selectedIndex].value;
		
		console.log(param);
		
		$.ajax({
			type:'post',
			dataType: "json",
			contentType: 'application/json',
			data: JSON.stringify(param),
			data: param.roundNo,
			url:localStorage.getItem("bbsContext") + "/lotto/selectLottoNumber",
			success:function(data) {
				
				console.log("selectLottoNumber", data);
				initializeDynamicTable(data, 'R');
				
			},
			error:function(data,sataus,err) {
				alert("데이터 요청에 실패하였습니다.\r status : " + status);
			}
		});
	});
	
	$('#checkWinningHistory').click(function() {

		if(!$('#lottoNumbers').val()) {
			alert("입력값이 없습니다.");
			return;
		}
		
		let param = {}; 
		param = splitLotto($('#lottoNumbers').val());
		console.log(param);

		$.ajax({
			type:'post',
			dataType: "json",
			contentType: 'application/json',
			data: JSON.stringify(param),
			url:localStorage.getItem("bbsContext") + "/lotto/checkWinningHistory",
			success:function(data) {
				
				console.log(data);
				//initializeDynamicTable(data, 'B');
				
			},
			error:function(data,sataus,err) {
				alert("데이터 요청에 실패하였습니다.\r status : " + status);
			}
		});
	});
	
	//로또 excel 데이터 db저장 함수
	$('#insertLottoNumbers').click(function() {
		
		let param = {};
		param.roundNo = $('#roundNo').val() + "회";
		$.ajax({
			type:'post',
			dataType: "json",
			contentType: 'application/json',
			data: JSON.stringify(param),
			url:localStorage.getItem("bbsContext") + "/lotto/insertLottoNumbers",
			success:function(data) {
				
				console.log(data);
				
			},
			error:function(data,sataus,err) {
				alert("데이터 요청에 실패하였습니다.\r status : " + status);
			}
		});
		
	});
	
	$('#insertLottoNumber').click(function() {
		
		if($('#roundNo').val() == '' || $('#lottoNumbers').val() == '' || $('#drawDate').val() == '') {
			alert("입력값이 없습니다.");
			return;
		}
		
		let param = {}; 
		param = splitLotto($('#lottoNumbers').val());
		param.roundNo = $('#roundNo').val() + "회";
		param.drawDate = $('#drawDate').val();
		console.log(param);
		
		$.ajax({
			type:'post',
			dataType: "json",
			contentType: 'application/json',
			data: JSON.stringify(param),
			url:localStorage.getItem("bbsContext") + "/lotto/insertLottoNumber",
			success:function(data) {
				console.log(data);
				location.reload(true);
			},
			error:function(data,sataus,err) {
				alert("데이터 요청에 실패하였습니다.\r status : " + status);
			}
		});
	});
});
 
//로또 회차정보 조회 함수
function selectRoundNumbers() {

	$.ajax({
		type:'post',
		dataType: "json",
		contentType: 'application/json',
		url:localStorage.getItem("bbsContext") + "/lotto/selectLottoRoundNumber",
		success:function(data) {
			console.log(data);
			makeOptionsOfRoundNumbers(data);
		},
		error:function(data,sataus,err) {
			alert("데이터 요청에 실패하였습니다.\r status : " + status);
		}
	});
}

//회차 정보를 받아서 select box의 option 데이터 생성 함수
function makeOptionsOfRoundNumbers(data) {
	
	$('#roundNumber').empty();
	$('#roundNumber').append('<option value="" selected disabled>선택해 주세요.</option>');
	
	for(let i = 0 ; i < data.length ; i++) {
		if(i == 0) {
			//$( '#roundNumber' ).append( '<option selected="selected" value="' + data[i].roundNo + '">' + data[i].roundNo + '</option>' );
			$( '#roundNumber' ).append( '<option selected="selected" value="' + data[i] + '">' + data[i] + '</option>' );
		} else {
			//$( '#roundNumber' ).append( '<option value="' + data[i].roundNo + '">' + data[i].roundNo + '</option>' );
			$( '#roundNumber' ).append( '<option value="' + data[i] + '">' + data[i] + '</option>' );
		}
	}
}

function chageRoundNumber() {
	let roundSelect = document.getElementById("roundNumber");
	
	let param = {};
	
	// select element에서 선택된 option의 value가 저장된다.
	let selectValue = roundSelect.options[roundSelect.selectedIndex].value;
	
	// select element에서 선택된 option의 text가 저장된다.
	let selectText = roundSelect.options[roundSelect.selectedIndex].text;
	console.log(selectValue, selectText);
	
	param.roundNo = selectValue;
	
	//selectRoundNumbers(param);
}

function splitLotto(data) {
	
	let param = {};
	let resultArray = data.split(',');
	
	param.firstNum = resultArray[0];
	param.secondNum = resultArray[1];
	param.thirdNum = resultArray[2];
	param.fourthNum = resultArray[3];
	param.fifthNum = resultArray[4];
	param.sixthNum = resultArray[5];
	if(resultArray.length == 7) {
		param.bonusNum = resultArray[6];
	}
	
	return param;
}

//로또 구매 함수
function buyingLottoTicket() {
	
	var html = null;
	const cost = $('#buyingCost').val();
	//console.log(cost);
	$.ajax({
		type:'post',
		dataType: "json",
		contentType: 'application/json',
		//data: JSON.stringify(param),
		
		url:localStorage.getItem("bbsContext") + "/lotto/buyingLottoTicket/" + cost,
		success:function(data) {
			
			console.log(data);
			
			if(data !== null) {
				initializeDynamicTable(data, "B");
			} else {
				console.log("data is null");
			}
			
		},
		error:function(data,sataus,err) {
			alert("데이터 요청에 실패하였습니다.\r status : " + status);
		}
	});
	
}
// 테이블을 동적으로 생성하는 함수
function initializeDynamicTable(data, rtn) {
	/** rtn 값 
	* 'B' : Buy
	* 'T' : Test
	* 'R' : Select Lotto by Round_No
	 */
	
	deleteDynamicTable();
	// thead 데이터 배열
	let headers = null;
	if(rtn == "B") {
		headers = ["firstNum", "secondNum", "thirdNum", "fourthNum", "fifthNum", "sixthNum"];
	} else {
		headers = ["roundNo", "firstNum", "secondNum", "thirdNum", "fourthNum", "fifthNum", "sixthNum", "bonusNum", "drawDate"];
	}
	
	//console.log("initializeDynamicTable", data);
	// 테이블 요소 생성
	const table = document.createElement('table');
	table.classList.add('data-table');
	
	// 테이블 헤드 생성
	const thead = table.createTHead();
	const headerRow = thead.insertRow();
	headers.forEach(headerText => {
		const th = document.createElement('th');
		th.textContent = headerText;
		headerRow.appendChild(th);
	});
    
    if(rtn == 'R') {
		// 테이블 바디 생성
		const tbody = table.createTBody();
		const row = tbody.insertRow();
		
	    headers.forEach(header => {
			const cell = row.insertCell();
			cell.textContent = data[header];
		});
	} else {
		// 테이블 바디 생성
		const tbody = table.createTBody();

		data.forEach(item => {
			console.log("item", item);
			const row = tbody.insertRow();
		    headers.forEach(header => {
				const cell = row.insertCell();
				cell.textContent = item[header];
			});
		});
	}
	
	// 테이블을 생성하고 HTML 요소에 추가
	const tableContainer = document.getElementById('lotto-container');
	tableContainer.appendChild(table);
}

// 테이블을 삭제하는 함수
function deleteDynamicTable() {
	// 테이블이 담겨있는 부모 요소를 찾아서 삭제
	const tableContainer = document.getElementById("lotto-container");
	const table = tableContainer.querySelector("table");

	if (table) {
		tableContainer.removeChild(table);
	}
}


function collectLottoNumbers() {

	const cost = $('#buyingCost').val();
	$.ajax({
		type:'post',
		dataType: "json",
		contentType: 'application/json',
		url:localStorage.getItem("bbsContext") + "/lotto/collectLottoNumbers/" + cost,
		success:function(data) {
			console.log(data);

			if(data !== null) {
				initializeDynamicTable(data, "B");
			} else {
				console.log("data is null");
			}
		},
		error:function(data,sataus,err) {
			alert("데이터 요청에 실패하였습니다.\r status : " + status);
		}
	});
}