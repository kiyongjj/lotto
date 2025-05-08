package kr.co.finotek.lotto.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import kr.co.finotek.lotto.domain.lotto.Lotto;
import kr.co.finotek.lotto.dto.LottoMainDto;
import kr.co.finotek.lotto.dto.LottoNumberDto;
import kr.co.finotek.lotto.dto.response.LottoResponseDto;
import kr.co.finotek.lotto.mapper.LottoMainMapper;
import kr.co.finotek.lotto.repo.LottoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("lottoMainService")
@AllArgsConstructor
@Slf4j
public class LottoMainService {

	private final LottoRepository lottoRepository;
	private LottoMainMapper lottoMainMapper;
	
	private static final int START_LOTTO_NUMBER = 1;
	private static final int LOTTO_NUMBER_COUNT = 6;
	private static final int END_LOTTO_NUMBER = 45;
	
	private List<LottoMainDto> cachedLottoNumbers = new ArrayList<>();
	private List<Double> probability = new ArrayList<>();
	
	
	public void initProcesses() {
		// 로또 번호 캐싱
		cachedLottoNumbers = selectLottoAllRounds();
		probability = getProbility(cachedLottoNumbers);
	}
	
	
	/**
	 * 로또 번호 추출 진입점
	 * @param count
	 * @return List<LottoMainDto>
	 */
	public List<LottoMainDto> choice(int count) {
		List<LottoMainDto> result = new ArrayList<>();
		List<LottoMainDto> tmpList = new ArrayList<>();
		List<Integer> lotto = new ArrayList<>();
		
		int rand = generateRandomNum(1000, 10000);
		
		System.out.println(rand);
		
		for(int i = 0 ; i < rand ; i++) {
			boolean rtn = true;
			lotto = choiceLottoNumbers();
			rtn = checkLottoNumber(tmpList, lotto);

			if(!rtn) {
				tmpList.add(convertLotto(lotto));
			}
		}
	
		log.info("*** lottoMainService.choice -tmpList- : " + tmpList.size());
		
//		for(LottoMainDto dto : result) {
//			System.out.println(dto);
//		}

		for(int j = 0 ; j < count ; j++) {
			result.add(tmpList.get(generateRandomNum(rand)));
		}
		
		log.info("*** lottoMainService.choice -result- : " + result);
		
		return result;
	}
	
	/**
	 * 로또 당첨번호 테스트용 코드
	 * @param lottoMainDto
	 * @return
	 */
	public boolean testLotto(LottoMainDto lottoMainDto) {
		
		boolean rtn = false;
		int count = 0;
		List<Integer> inputLotto = new ArrayList<>();
		List<Integer> newLotto = new ArrayList<>();

		inputLotto = reverseLotto(lottoMainDto);
		log.info("inputLotto : " + inputLotto);
		while(!rtn) {

			newLotto = choiceLottoNumbers();
			
			rtn = newLotto.containsAll(inputLotto);
			count++;
			if(rtn) {
				log.info("newLotto : " + newLotto);
				log.info("rtn : " + rtn + " , count : " + count);
			}
			if((count % 1000) == 0) {
				log.info("newLotto : " + newLotto);
				log.info("rtn : " + rtn + " , count : " + count);
			}
			if(count > 5000000) {
				rtn = true;
			}
		}
		return rtn;
	}
	

    /**
	 * 랜덤 횟수만큼 로또 번호 추출, List에 저장.
	 * 무작위 순번에 해당하는 로또 번호세트 리턴
	 * @return LottoMainDto
	 */
	public List<Integer> choiceLottoNumbers() {
		
		List<Integer> results = new ArrayList<>();
	
		boolean rtn = true;
		
    	List<Integer> lottoNumberCollections = collectNumbers();
    	results = selectNumbers(lottoNumberCollections);
		rtn = checkLottoNumber(cachedLottoNumbers, results);
		if(rtn) {
			log.info("result :: " + results + ", rtn :: " + rtn);
		}
		
		return results;
	}
	   
	    
	public LottoMainDto choiceLottoNumbers2() {
		List<LottoMainDto> result = new ArrayList<>();
	
		int randomNum = generateRandomNum(1000, 10000);
		
		boolean rtn = true;
		
		for(int i = 0 ; i < randomNum ; i++) {
	    	List<Integer> lottoNumberCollections = collectNumbers();
	    	List<Integer> results = selectNumbers(lottoNumberCollections);
			rtn = checkLottoNumber(cachedLottoNumbers, results);
			if(rtn) {
				log.info("result :: " + results + ", rtn :: "
				+ rtn + ", count :: " + i);
				i = i - 1;
			} else {
				result.add(convertLotto(results));
			}
		}
		randomNum = generateRandomNum(0, randomNum);
		
		return result.get(randomNum);
	}
	
	
	/**
	 * 로또 번호 6개 추출하는 함수
	 * probabilityBasedSelection() 함수를 6번 호출할 예정
	 * 리턴된 로또번호를 리스트에 답아서 리스트 리턴함 
	 * @param lottoNumberCollections
	 * @return List<Integer>
	 */
	public List<Integer> selectNumbers(List<Integer> lottoNumberCollections) {
		
		List<Integer> result = new ArrayList<Integer>();
		SecureRandom secureRandom = new SecureRandom();
	
		for(int i = 0 ; i < LOTTO_NUMBER_COUNT ; i++) {
			
			Collections.shuffle(lottoNumberCollections);
	
			double rand = secureRandom.nextDouble();
			double cumulativeProbability = 0.0;
			
			for(int j = 0 ; j < lottoNumberCollections.size() ; j++) {
				cumulativeProbability += probability.get(lottoNumberCollections.get(j) - 1);
				if(lottoNumberCollections.size() > 39 && rand >= cumulativeProbability) {
					j = 0;
				} else {
					result.add(lottoNumberCollections.get(j));
					lottoNumberCollections.remove(j);
					break;
				}
				Collections.shuffle(lottoNumberCollections);
			}
		}
		Collections.sort(result);
	
		return result;
	}
	
	
	/**
	 * 로또 번호 collection 형식으로 구하는 로직
	 * @return List<Integer>
	 */
	public List<Integer> collectNumbers() {
		
		@SuppressWarnings("removal")
	List<Integer> lottoNumberCollections = IntStream.rangeClosed(START_LOTTO_NUMBER, END_LOTTO_NUMBER)
			.mapToObj(Integer::new)				//람다 code :: .mapToObj(x -> new Integer(x))
				.collect(Collectors.toList());
		
		Collections.sort(lottoNumberCollections);
		
		return lottoNumberCollections;
	}
	
	
	/**
	 * 추첨한 로또번호와 기 당첨번호 비교
	 * @param lottoNumber
	 * @return boolean
	 */
	public boolean checkLottoNumber(List<LottoMainDto> cachedLottoNumbers, List<Integer> lottoNumber) {
		
		boolean rtn = false;
		
		for(int i = 0 ; i < cachedLottoNumbers.size() ; i++) {
			List<Integer> tmpList = reverseLotto(cachedLottoNumbers.get(i));
	
			rtn = lottoNumber.containsAll(tmpList);
	
			if(rtn) {
				log.info("tmpList :: " + tmpList + ", rtn :: " + rtn + " , count :: " + i);
				break;
			}
		}
		
		return rtn;
	}
	
	
	/**
	 * List<Integer> => LottoMainDto
	 * @param results
	 * @return LottoMainDto
	 */
	public LottoMainDto convertLotto(List<Integer> results) {
		
		LottoMainDto lnd = new LottoMainDto();
		
		lnd.setFirstNum(results.get(0));
		lnd.setSecondNum(results.get(1));
		lnd.setThirdNum(results.get(2));
		lnd.setFourthNum(results.get(3));
		lnd.setFifthNum(results.get(4));
		lnd.setSixthNum(results.get(5));
		
		return lnd;
	}
	
	
	/**
	 * LottoMainDto => List<Integer>
	 * @param lottoMainDto
	 * @return
	 */
	public List<Integer> reverseLotto(LottoMainDto lottoMainDto) {
		
		List<Integer> result = new ArrayList<>();
		
		result.add(lottoMainDto.getFirstNum());
		result.add(lottoMainDto.getSecondNum());
		result.add(lottoMainDto.getThirdNum());
		result.add(lottoMainDto.getFourthNum());
		result.add(lottoMainDto.getFifthNum());
		result.add(lottoMainDto.getSixthNum());
		
		return result;
	}
	
	
	/**
	 * Lotto 당첨번호 누적 확률 계산
	 * @param cachedLottoNumbers
	 * @return List<Double>
	 */
	public List<Double> getProbility(List<LottoMainDto> cachedLottoNumbers) {
		
		int[] count = new int[45]; // 각 숫자의 선택 횟수를 저장할 배열
	int totalIterations  = cachedLottoNumbers.size() * 6;
	List<Double> tmpProbability = new ArrayList<>(); // 각 숫자의 확률을 저장할 배열
		
		for(int i = 0 ; i < cachedLottoNumbers.size() ; i++) {
			
			count[cachedLottoNumbers.get(i).getFirstNum() - 1]++;
			count[cachedLottoNumbers.get(i).getSecondNum() - 1]++;
			count[cachedLottoNumbers.get(i).getThirdNum() - 1]++;
			count[cachedLottoNumbers.get(i).getFourthNum() - 1]++;
			count[cachedLottoNumbers.get(i).getFifthNum() - 1]++;
			count[cachedLottoNumbers.get(i).getSixthNum() - 1]++;
		}
		for(int j = 0 ; j < count.length ; j++) {
			tmpProbability.add((double)count[j] / totalIterations);
		}
	
		return tmpProbability;
	}
	
	
	/**
	 * 보안성 난수 생성 함수 SecureRandom 사용
	 * @param start
	 * @param end
	 * @return Integer
	 */
	public int generateRandomNum(int start, int end) {
		SecureRandom secureRandom = new SecureRandom();
	
		return secureRandom.nextInt(start, end);
	}
	public int generateRandomNum(int bound) {
		SecureRandom secureRandom = new SecureRandom();
	
		return secureRandom.nextInt(bound);
	}
	
	
	/**
	 * Mapper 호출
	 * @param lottoNumberDto
	 * @return
	 */
	public List<String> selectLottoRoundNumber() {
		return lottoMainMapper.selectLottoRoundNumber();
	}
	
	
	public List<LottoMainDto> selectLottoAllRounds() {
		return lottoMainMapper.selectLottoAllRounds();
	}
	
	
	public LottoResponseDto selectLottoNumberByRound(String roundNo) {
		Lotto lotto = lottoRepository.findById(roundNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
		return new LottoResponseDto(lotto);
	}
	
	
	public boolean insertLottoNumber(LottoNumberDto lottoNumberDto) {
		
		boolean rtn = false;
		
		if (ObjectUtils.isNotEmpty(lottoNumberDto)) {
	    	rtn = lottoMainMapper.insertLottoNumber(lottoNumberDto) > 0;
		}
		
		return rtn;
	}

}
