package kr.co.finotek.lotto.dto.response;

import java.time.LocalDate;

import kr.co.finotek.lotto.domain.lotto.Lotto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LottoResponseDto {

	private String roundNo;
	private int firstNum;
	private int secondNum;
	private int thirdNum;
	private int fourthNum;
	private int fifthNum;
	private int sixthNum;
	private int bonusNum;
	private LocalDate drawDate;
	
	public LottoResponseDto(Lotto entity) {
		this.roundNo = entity.getRoundNo();
		this.firstNum = entity.getFirstNum();
		this.secondNum = entity.getSecondNum();
		this.thirdNum = entity.getThirdNum();
		this.fourthNum = entity.getFourthNum();
		this.fifthNum = entity.getFifthNum();
		this.sixthNum = entity.getSixthNum();
		this.bonusNum = entity.getBonusNum();
		this.drawDate = entity.getDrawDate();
	}
	
	@Builder
	public LottoResponseDto(String roundNo, int firstNum, int secondNum,
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
	
	/** entity(db) -> dto(web) */
	public static LottoResponseDto fromEntity(Lotto lotto) {
		return LottoResponseDto.builder()
				.roundNo(lotto.getRoundNo())
				.firstNum(lotto.getFirstNum())
				.secondNum(lotto.getSecondNum())
				.thirdNum(lotto.getThirdNum())
				.fourthNum(lotto.getFourthNum())
				.fifthNum(lotto.getFifthNum())
				.sixthNum(lotto.getSixthNum())
				.bonusNum(lotto.getBonusNum())
				.drawDate(lotto.getDrawDate())
				.build();
	}
}
