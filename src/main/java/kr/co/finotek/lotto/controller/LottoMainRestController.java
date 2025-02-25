package kr.co.finotek.lotto.controller;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.finotek.lotto.dto.LottoMainDto;
import kr.co.finotek.lotto.dto.LottoNumberDto;
import kr.co.finotek.lotto.dto.response.LottoResponseDto;
import kr.co.finotek.lotto.service.LottoMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lotto")
@Slf4j
public class LottoMainRestController {
	
	private final LottoMainService lottoMainService;
	
	@PostMapping("/selectLottoRoundNumber")
	public ResponseEntity<List<String>> selectLottoRoundNumber() {
		
		lottoMainService.initProcesses();
		
		List<String> result = lottoMainService.selectLottoRoundNumber();

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	
	@PostMapping("/insertLottoNumber")
	public ResponseEntity<Object> insertLottoNumbers(@RequestBody LottoNumberDto lottoNumberDto) {
		
		boolean result = lottoMainService.insertLottoNumber(lottoNumberDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	
	@PostMapping("/buyingLottoTicket/{cost}")
	public ResponseEntity<List<LottoMainDto>> buyingLottoTicket(@PathVariable int cost) {
		
		List<LottoMainDto> result = lottoMainService.choice(cost);
		
		if(ObjectUtils.isNotEmpty(result)) {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		}
	}
	
	
	@PostMapping("/selectLottoNumber")
	public ResponseEntity<LottoResponseDto> selectLottoNumber(@RequestBody String roundNo) {

		log.debug("roundNo : " + roundNo);
		
		LottoResponseDto result = lottoMainService.selectLottoNumberByRound(roundNo);

		if(ObjectUtils.isEmpty(result)) {
			System.out.println("데이터가 없습니다.");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	
//	@PostMapping("/testLottoNumber")
//	public ResponseEntity<LottoMainDto> testLottoNumber() {
//		
//		LottoMainDto result = lottoMainService.choiceTest();
//
//		System.out.println("result :: " + result);
//		boolean rtn = lottoMainService.winningLotteryTest(result);
//		System.out.println("rtn : " + rtn);
//		
//		return ResponseEntity.status(HttpStatus.OK).body(result);
//	}

	
/**
 * 	excel 파일로 정리한 로또정보 insert하기 위한 코드(필요 시 주석 해제 후 사용)
 * @param lottoNumberDto
 * @return
 */
/*
	@PostMapping("/insertLottoNumbers")
	public DrsResponseEntity<Object> insertLottoNumbers(@RequestBody LottoNumberDto lottoNumberDto) {
		try {
			FileInputStream file = new FileInputStream("c:/temp/로또당첨번호.xlsx");
			// 엑셀 파일로 Workbook instance를 생성한다.
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			// workbook의 첫번째 sheet를 가져온다.
			XSSFSheet sheet = workbook.getSheetAt(1);
			LottoNumberDto ltd = new LottoNumberDto();
			for(Row row : sheet) { // 행을 받아옴
				List<Object> lst = new ArrayList<Object>();
				for(Cell cell : row) { // n번째 행에 열 값들을 받음
					switch (cell.getCellType()) {
						case NUMERIC:
							lst.add((int)cell.getNumericCellValue());
							break;
						case STRING:
							lst.add(cell.getStringCellValue());
							break;
						default:
							throw new IllegalStateException("Unexpected value: " + cell.getCellType());
					}
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
				ltd.setRoundNo((String)lst.get(0));
				ltd.setFirstNum((int)lst.get(1));
				ltd.setSecondNum((int)lst.get(2));
				ltd.setThirdNum((int)lst.get(3));
				ltd.setFourthNum((int)lst.get(4));
				ltd.setFifthNum((int)lst.get(5));
				ltd.setSixthNum((int)lst.get(6));
				ltd.setBonusNum((int)lst.get(7));
				ltd.setDrawDate(LocalDate.parse((CharSequence) lst.get(8), formatter));
				boolean result = lottoMainService.insertLottoNumber(ltd);
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseService.toResponseEntity("조회가 완료되었습니다.", null);
	}
*/
}
