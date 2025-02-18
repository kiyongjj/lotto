package kr.co.finotek.lotto.dto.request;

import java.time.LocalDate;

import kr.co.finotek.lotto.domain.lotto.Lotto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LottoWriteDto {

	private String roundNo;
	private int firstNum;
	private int secondNum;
	private int thirdNum;
	private int fourthNum;
	private int fifthNum;
	private int sixthNum;
	private int bonusNum;
	private LocalDate drawDate;

	@Builder
	public LottoWriteDto(String roundNo, int firstNum, int secondNum,
			int thirdNum, int fourthNum, int fifthNum, int sixthNum,
			int bonusNum, LocalDate drawDate) {
		this.roundNo = roundNo;
		this.firstNum = firstNum;
		this.secondNum = secondNum;
		this.thirdNum = thirdNum;
		this.fourthNum = fourthNum;
		this.fifthNum = fifthNum;
		this.sixthNum = sixthNum;
		this.bonusNum = bonusNum;
		this.drawDate = drawDate;
	}
	
	/** dto -> entity */
	public Lotto toEntity() {
		return Lotto.builder()
				.roundNo(roundNo)
				.firstNum(firstNum)
				.secondNum(secondNum)
				.thirdNum(thirdNum)
				.fourthNum(fourthNum)
				.fifthNum(fifthNum)
				.sixthNum(sixthNum)
				.bonusNum(bonusNum)
				.drawDate(drawDate)
				.build();
	}
}
