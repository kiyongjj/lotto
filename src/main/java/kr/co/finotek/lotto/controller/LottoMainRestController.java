package kr.co.finotek.lotto.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.finotek.lotto.domain.lotto.Lotto;
import kr.co.finotek.lotto.dto.LottoMainDto;
import kr.co.finotek.lotto.dto.LottoNumberDto;
import kr.co.finotek.lotto.dto.LottoResult;
import kr.co.finotek.lotto.service.LottoMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lotto")
@Slf4j
public class LottoMainRestController {
	
	private final LottoMainService lottoMainService;
	
	/**
	 * 1. db에서 로또당첨번호 전체 조회
	 * 2. 로또 회차(roundNo) 조회
	 * @return
	 */
	@PostMapping("/selectLottoRoundNumber")
	public ResponseEntity<List<String>> selectLottoRoundNumber() {
		
		lottoMainService.initProcesses();
		
		List<String> result = lottoMainService.selectLottoRoundNumber();

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	/**
	 * 회차별 로또 당첨번호 db에 입력
	 * @param lottoNumberDto
	 * @return
	 */
	@PostMapping("/insertLottoNumber")
	public ResponseEntity<Object> insertLottoNumber(@RequestBody LottoNumberDto lottoNumberDto) {
		
		boolean result = lottoMainService.insertLottoNumber(lottoNumberDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	/**
	 * 로또번호 cost만큼 생성
	 * 필요없는거 같은데...
	 * @param cost
	 * @return
	 */
	@PostMapping("/buyingLottoTicket/{cost}")
	public ResponseEntity<List<LottoMainDto>> buyingLottoTicket(@PathVariable int cost) {
		
		List<LottoMainDto> result = lottoMainService.choice(cost);
		
		if(ObjectUtils.isNotEmpty(result)) {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		}
	}

	/**
	 * 멀티Thread 이용하여 로또 번호 추첨
	 * @return
	 * @throws InterruptedException
	 */
	@PostMapping("/collectLottoNumbers/{cost}")
	public ResponseEntity<List<LottoResult>> collectLottoNumbers(@PathVariable int cost) throws InterruptedException {
		
		List<LottoResult> result = lottoMainService.collectLottoNumbers(cost);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	/**
	 * 로또 당첨번호 조회
	 * @param roundNo
	 * @return
	 */
	@PostMapping("/selectLottoNumber")
	public ResponseEntity<LottoNumberDto> selectLottoNumber(@RequestBody String roundNo) {

		LottoNumberDto result = lottoMainService.selectLottoNumberByRound(roundNo);

		if(ObjectUtils.isEmpty(result)) {
			log.info("데이터가 없습니다.");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	/**
	 * 로또 당첨 이력 확인
	 * @param lottoMainDto
	 * @return
	 */
	@PostMapping("/checkWinningHistory")
	public ResponseEntity<Boolean> testLottoNumber(@RequestBody LottoMainDto lottoMainDto) {
		
		boolean result = lottoMainService.checkWinningHistory(lottoMainDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	
	/**
	 * 	excel 파일로 정리한 로또정보 insert하기 위한 코드(필요 시 주석 해제 후 사용)
	 * @param lottoNumberDto
	 * @return
	 */
	@PostMapping("/insertLottoNumbers")
	public ResponseEntity<Object> insertLottoNumbers(@RequestBody LottoNumberDto lottoNumberDto) {
		try {
			FileInputStream file = new FileInputStream("c:/temp/로또당첨번호.xlsx");
			try (// 엑셀 파일로 Workbook instance를 생성한다.
			XSSFWorkbook workbook = new XSSFWorkbook(file)) {
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
					if(!result) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body("처리가 실패되었습니다.");
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body("처리가 완료되었습니다.");
	}
	
	@GetMapping("/api/excel/download")
	public ResponseEntity<byte[]> downloadExcel() throws IOException {
		List<Lotto> lottoNumbers = lottoMainService.getAllLottoNumbers();
		log.info("*** lottoMainRestController.downloadExcel -lottoNumbers- : " + lottoNumbers.get(0).getFirstNum());
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("lotto");
		
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Round_No");
		headerRow.createCell(1).setCellValue("First_Num");
		headerRow.createCell(2).setCellValue("Secon_dNum");
		headerRow.createCell(3).setCellValue("Third_Num");
		headerRow.createCell(4).setCellValue("Fourt_hNum");
		headerRow.createCell(5).setCellValue("Fifth_Num");
		headerRow.createCell(6).setCellValue("Sixth_Num");
		headerRow.createCell(7).setCellValue("Bonus_Num");
		headerRow.createCell(8).setCellValue("Draw_Date");
		
		int rowNum = 1;
		for(Lotto lotto : lottoNumbers) {
			Row dataRow = sheet.createRow(rowNum++);
			dataRow.createCell(0).setCellValue(lotto.getRoundNo());
			dataRow.createCell(1).setCellValue(lotto.getFirstNum());
			dataRow.createCell(2).setCellValue(lotto.getSecondNum());
			dataRow.createCell(3).setCellValue(lotto.getThirdNum());
			dataRow.createCell(4).setCellValue(lotto.getFourthNum());
			dataRow.createCell(5).setCellValue(lotto.getFifthNum());
			dataRow.createCell(6).setCellValue(lotto.getSixthNum());
			dataRow.createCell(7).setCellValue(lotto.getBonusNum());
			dataRow.createCell(8).setCellValue(lotto.getDrawDate());
		}
		
		for(int i = 0 ; i < 9 ; i++) {
			sheet.autoSizeColumn(i);
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=employees.xlsx");
		headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
	}
}
