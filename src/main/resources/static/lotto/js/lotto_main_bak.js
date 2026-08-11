/* ================================
 * lotto_main.js (refined)
 * lotto-container 전용 렌더링 로직 반영
 * 기존 API / jQuery 흐름 유지
 * ================================ */

$(function () {

  localStorage.setItem("bbsContext", "http://localhost:8081");

  // 오늘 날짜 기본 세팅
  document.getElementById('drawDate').value = new Date().toISOString().substring(0, 10);

  selectRoundNumbers();

  /* ================================
   * 당첨번호 조회
   * ================================ */
  $('#selectLottoNumber').click(function () {
    const roundSelect = document.getElementById('roundNumber');
    const roundNo = roundSelect.options[roundSelect.selectedIndex].value;

    $.ajax({
      type: 'post',
      dataType: 'json',
      contentType: 'application/json',
      data: roundNo,
      url: localStorage.getItem("bbsContext") + "/lotto/selectLottoNumber",
      success: function (data) {
        renderWinningNumbers(data);
      },
      error: function () {
        alert("데이터 요청에 실패하였습니다.");
      }
    });
  });

  /* ================================
   * 구매 버튼
   * ================================ */
  $('#collectLottoNumbers, .collectLottoNumbers').click(function () {
    collectLottoNumbers();
  });
});

/* ================================
 * 회차 조회
 * ================================ */
function selectRoundNumbers() {
  $.ajax({
    type: 'post',
    dataType: 'json',
    contentType: 'application/json',
    url: localStorage.getItem("bbsContext") + "/lotto/selectLottoRoundNumber",
    success: function (data) {
      makeOptionsOfRoundNumbers(data);
    }
  });
}

function makeOptionsOfRoundNumbers(data) {
  const $select = $('#roundNumber');
  $select.empty();
  $select.append('<option disabled selected>선택해 주세요</option>');

  data.forEach((round, index) => {
    const selected = index === 0 ? 'selected' : '';
    $select.append(`<option ${selected} value="${round}">${round}</option>`);
  });
}

/* ================================
 * lotto-container 렌더링 로직
 * ================================ */
const lottoContainer = document.getElementById('lotto-container');

function renderWinningNumbers(data) {
  clearContainer();

  const numbers = [
    data.firstNum,
    data.secondNum,
    data.thirdNum,
    data.fourthNum,
    data.fifthNum,
    data.sixthNum
  ];

  lottoContainer.innerHTML = `
    <div class="result-box">
      <h3>${data.roundNo}회차 당첨번호</h3>
      <div class="balls">
        ${numbers.map(createBall).join('')}
        <span class="bonus">+</span>
        ${createBall(data.bonusNum)}
      </div>
      <p class="draw-date">추첨일 : ${data.drawDate}</p>
    </div>
  `;
}

function renderBuyingNumbers(list) {
  clearContainer();

  const rows = list.map((item, idx) => {
    const nums = [
      item.firstNum,
      item.secondNum,
      item.thirdNum,
      item.fourthNum,
      item.fifthNum,
      item.sixthNum
    ];

    return `
      <div class="buy-row">
        <strong>${idx + 1}번</strong>
        <div class="balls">
          ${nums.map(createBall).join('')}
        </div>
      </div>
    `;
  }).join('');

  lottoContainer.innerHTML = `
    <div class="result-box">
      <h3>구매한 로또 번호</h3>
      ${rows}
    </div>
  `;
}

/* ================================
 * 구매 API
 * ================================ */
function collectLottoNumbers() {
  const cost = $('#buyingCost').val();

  if (!cost || cost < 1000) {
    alert('구매 금액을 입력하세요.');
    return;
  }

  $.ajax({
    type: 'post',
    dataType: 'json',
    contentType: 'application/json',
    url: localStorage.getItem("bbsContext") + "/lotto/collectLottoNumbers/" + cost,
    success: function (data) {
      renderBuyingNumbers(data);
    },
    error: function () {
      alert("데이터 요청에 실패하였습니다.");
    }
  });
}

/* ================================
 * 공(ball) 관련 유틸
 * ================================ */
function createBall(num) {
  return `<span class="lotto-ball ball-${getBallColor(num)}">${num}</span>`;
}

function getBallColor(num) {
  if (num <= 10) return 'yellow';
  if (num <= 20) return 'blue';
  if (num <= 30) return 'red';
  if (num <= 40) return 'gray';
  return 'green';
}

function clearContainer() {
  lottoContainer.innerHTML = '';
}
