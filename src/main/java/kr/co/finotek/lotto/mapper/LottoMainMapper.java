package kr.co.finotek.lotto.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.finotek.lotto.dto.LottoMainDto;
import kr.co.finotek.lotto.dto.LottoNumberDto;

@Mapper
public interface LottoMainMapper {

	List<LottoMainDto> selectLottoAllRounds();
	
	LottoNumberDto selectLottoNumberByRound(String roundNo);
	
	int insertLottoNumber(LottoNumberDto lottoNumberDto);

	int existLottoNumber(LottoNumberDto lottoNumberDto);

//	List<LottoNumberDto> selectLottoRoundNumber();
	List<String> selectLottoRoundNumber();

	int probabilityLottoNumber(LottoMainDto lottoMainDto);
	
	int countOfLottoRound();

	List<LottoNumberDto> selectLottoNumbers();

}
