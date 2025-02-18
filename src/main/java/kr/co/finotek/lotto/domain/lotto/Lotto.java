package kr.co.finotek.lotto.domain.lotto;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="lotto_numbers")
public class Lotto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ROUND_NO", nullable = false)
	private String roundNo;
	
	@Column
	private int firstNum;
	
	@Column
	private int secondNum;
	
	@Column
	private int thirdNum;
	
	@Column
	private int fourthNum;
	
	@Column
	private int fifthNum;
	
	@Column
	private int sixthNum;
	
	@Column
	private int bonusNum;
	
	@Column
	private LocalDate drawDate;
	
	@Builder
	public Lotto(String roundNo, int firstNum, int secondNum,
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
}
