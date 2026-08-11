package kr.co.finotek.lotto.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import kr.co.finotek.lotto.domain.lotto.Lotto;
import kr.co.finotek.lotto.dto.LottoMainDto;
import kr.co.finotek.lotto.dto.LottoNumberDto;
import kr.co.finotek.lotto.dto.LottoResult;
import kr.co.finotek.lotto.mapper.LottoMainMapper;
import kr.co.finotek.lotto.repo.LottoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("lottoMainService")
@AllArgsConstructor
@Slf4j
public class LottoMainService extends Thread {

	private final LottoRepository lottoRepository;
	private final LottoMainMapper lottoMainMapper;
	
	private static final int START_LOTTO_NUMBER = 1;
	private static final int LOTTO_NUMBER_COUNT = 6;
	private static final int END_LOTTO_NUMBER = 45;
	
//	private static final long DRAW_COUNT = 130_000_000L; // 1억회 이상 가능
	private static final long DRAW_COUNT = 130_000L; // 1억회 이상 가능
	private static final int THREAD_POOL_SIZE = 8;
	private static final int BATCH_SIZE = 10_000; // 메모리 절약용 배치 단위
	
	private List<LottoMainDto> cachedLottoNumbers = new ArrayList<>();
	private List<Double> probability = new ArrayList<>();
	
	
	public void initProcesses() {
		// 로또 번호 캐싱
		List<LottoMainDto> tmpCachedLottoNumbers = selectLottoAllRounds();

		for(LottoMainDto dto : tmpCachedLottoNumbers) {
    		List<Integer> tcl = reverseLotto("init", dto);
    		tcl.add(sumList(tcl));
    		cachedLottoNumbers.add(convertLotto("init", tcl));
    	}

		probability = getProbility(cachedLottoNumbers);

	}

	public List<Lotto> getAllLottoNumbers() {
		return lottoRepository.findAll();
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
    	List<LottoResult> result2 = new ArrayList<>();
    	
    	// test code START
//    	try {
//			result2 = collectLottoNumbers2();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	System.out.println("result2.size :: " + result2.get(result2.size() -1));
//    	System.out.println("result2.size :: " + result2.get(result2.size() -1).getNumbers());
//    	for(int i = 0 ; i < result2.size() ; i++) {
//    		if
//    	}
    	// test code END
    	
    	int rand = generateRandomNum(1000, 10000);
//    	int selectedNum = generateRandomNum(120,160);
    	
    	for(int i = 0 ; i < rand ; i++) {
    		boolean rtn = false;
    		lotto = choiceLottoNumbers();

//    		if(lotto.size() > 0 && lotto.get(6) == selectedNum) {
    		if(lotto.size() > 0) {
    			rtn = checkLottoNumber(tmpList, lotto);
        		if(!rtn) {
        			tmpList.add(convertLotto("init", lotto));
        		}
    		}
    	}
    	
    	
    	
    	
    	
    	rand = generateRandomNum(tmpList.size() - count);
    	
    	for(int j = 0 ; j < count ; j++) {
    		result.add(tmpList.get(rand + j));
    	}
    	
    	log.info("*** lottoMainService.choice -result- : " + result);
    	
    	return result;
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
			log.info("*** lottoMainService.choiceLottoNumbers -result- : " + results + ", rtn :: " + rtn);
			results.clear();
		} else {
			return results;
		}
		return results;
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
		result.add(sumList(result));
	
		return result;
	}
	

	
	
	/**
	 * 추첨한 로또번호와 기 당첨번호 비교
	 * @param lottoNumber
	 * @return boolean
	 */
	public boolean checkLottoNumber(List<LottoMainDto> cachedLottoNumbers, List<Integer> lottoNumber) {
		
		boolean rtn = false;

		for(int i = 0 ; i < cachedLottoNumbers.size() ; i++) {
			List<Integer> tmpList = reverseLotto("check", cachedLottoNumbers.get(i));
	
			rtn = lottoNumber.containsAll(tmpList);
			
			if(rtn) {
				rtn = true;
				log.info("tmpList :: " + tmpList + ", rtn :: " + rtn + " , count :: " + i);
				return rtn;
			}
		}
		
		return rtn;
	}
	
	
	/**
	 * List<Integer> => LottoMainDto
	 * @param results
	 * @return LottoMainDto
	 */
	public LottoMainDto convertLotto(String code, List<Integer> results) {

		LottoMainDto lnd = new LottoMainDto();
		
		lnd.setFirstNum(results.get(0));
		lnd.setSecondNum(results.get(1));
		lnd.setThirdNum(results.get(2));
		lnd.setFourthNum(results.get(3));
		lnd.setFifthNum(results.get(4));
		lnd.setSixthNum(results.get(5));
		lnd.setTotalSum(results.get(6));
		if(code.equals("collect")) {
			lnd.setCollectCnt(results.get(7));
		}
		
		return lnd;
	}
	
	
	/**
	 * LottoMainDto => List<Integer>
	 * @param lottoMainDto
	 * @return
	 */
	public List<Integer> reverseLotto(String code, LottoMainDto lottoMainDto) {
		
		List<Integer> result = new ArrayList<>();
		
			result.add(lottoMainDto.getFirstNum());
			result.add(lottoMainDto.getSecondNum());
			result.add(lottoMainDto.getThirdNum());
			result.add(lottoMainDto.getFourthNum());
			result.add(lottoMainDto.getFifthNum());
			result.add(lottoMainDto.getSixthNum());
		if(code.equals("check")) {
			result.add(lottoMainDto.getTotalSum());
		}
		
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

	
    public Integer sumList(List<Integer> lotto) {
    	return lotto.stream().mapToInt(Integer::intValue).sum();
    }


	public List<LottoMainDto> collectLottoNumbers222() {
    	
    	List<LottoMainDto> tmpList = new ArrayList<>();
    	List<Integer> lotto = new ArrayList<>();
    	for(int i = 0 ; i < 120000000 ; i++) {
    		lotto = choiceLottoNumbers();

    		if(lotto.size() > 0) {
	    		if(tmpList.size() > 0) {
	//    	    	log.info("*** lottoMainService.collectLottoNumbers *** true : " + i);
	    			tmpList.add(convertLotto2(tmpList, lotto));
	    		} else {
	    	    	lotto.add(1);
	    	    	log.info("*** lottoMainService.collectLottoNumbers *** lotto : " + lotto);
	    			tmpList.add(convertLotto("collect", lotto));
	    		}
	    	}
    	}
    	filteringNumbers(tmpList);
    	
    	return tmpList;
    }
	public LottoMainDto convertLotto2(List<LottoMainDto> cachedLottoNumbers, List<Integer> lottoNumber) {

		LottoMainDto lnd = new LottoMainDto();
		boolean rtn = false;

//		log.info("*** lottoMainService.convertLotto2 *** cachedLottoNumbers : " + cachedLottoNumbers.size());
		for(int i = 0 ; i < cachedLottoNumbers.size() ; i++) {
			List<Integer> tmpList = reverseLotto("collect", cachedLottoNumbers.get(i));
	
			rtn = lottoNumber.containsAll(tmpList);

			if(rtn) {
//				log.info("*** lottoMainService.convertLotto2 *** tmpList :: " + tmpList);
//				log.info("*** lottoMainService.convertLotto2 *** cachedLottoNumbers :: " + cachedLottoNumbers.get(i));
				cachedLottoNumbers.get(i).setCollectCnt(cachedLottoNumbers.get(i).getCollectCnt() + 1);
				
//				log.info("*** lottoMainService.convertLotto2 *** cachedLottoNumbers :: " + cachedLottoNumbers.get(i).getCollectCnt());
				
			} else {
				lnd.setFirstNum(lottoNumber.get(0));
				lnd.setSecondNum(lottoNumber.get(1));
				lnd.setThirdNum(lottoNumber.get(2));
				lnd.setFourthNum(lottoNumber.get(3));
				lnd.setFifthNum(lottoNumber.get(4));
				lnd.setSixthNum(lottoNumber.get(5));
				lnd.setTotalSum(lottoNumber.get(6));
				lnd.setCollectCnt(1);
			}
		}
		
		return lnd;
	}
	public void filteringNumbers(List<LottoMainDto> numbers) {
		log.info("*** lottoMainService.filteringNumbers *** numbers.size() : " + numbers.size());
//		int count = 0;
		for(LottoMainDto dto : numbers) {
			if(dto.getCollectCnt() > 10) {
//				count++;
				log.info("*** lottoMainService.filteringNumbers *** numbers : " + dto);
			}
		}
//		for(int i = 0 ; i < 5 ; i++) {
//			log.info("*** lottoMainService.filteringNumbers *** numbers : " + numbers.get(i));
//		}
//		log.info("*** lottoMainService.filteringNumbers *** numbers count : " + count);
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
		List<LottoMainDto> lottoNumbers = lottoMainMapper.selectLottoAllRounds();
		return lottoNumbers;
	}
	
	
	public LottoNumberDto selectLottoNumberByRound(String roundNo) {
		LottoNumberDto lottoNumbers = lottoMainMapper.selectLottoNumberByRound(roundNo);
		return lottoNumbers;
	}
	
	
	public boolean insertLottoNumber(LottoNumberDto lottoNumberDto) {
		
		boolean rtn = false;
		
		if (ObjectUtils.isNotEmpty(lottoNumberDto)) {
	    	rtn = lottoMainMapper.insertLottoNumber(lottoNumberDto) > 0;
		}
		
		return rtn;
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

		log.info("*** lottoMainService.testLotto -lottoMainDto- : " + lottoMainDto);
		inputLotto = reverseLotto("test", lottoMainDto);
		inputLotto.add(sumList(inputLotto));
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
	public boolean checkWinningHistory(LottoMainDto lottoMainDto) {
		boolean rtn = false;
		List<Integer> inputLotto = reverseLotto("test", lottoMainDto);
		inputLotto.add(sumList(inputLotto));
		log.info("*** lottoMainService.testLotto2 -inputLotto- : " + inputLotto);

		for(LottoMainDto lotto : cachedLottoNumbers) {

			List<Integer> tmpList = reverseLotto("check", lotto);
			
			rtn = inputLotto.containsAll(tmpList);
		}
		return rtn;
	}

	public List<LottoResult> collectLottoNumbers2() throws InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		
		// thread-safe 누적용 맵
		ConcurrentMap<List<Integer>, AtomicLong> countMap = new ConcurrentHashMap<>();
		
		long totalBatches = (DRAW_COUNT + BATCH_SIZE - 1) / BATCH_SIZE;
		
		System.out.printf("총 %,d회 추첨을 %,d회 배치로 처리합니다.%n", DRAW_COUNT, totalBatches);
		
		for (long batch = 0; batch < totalBatches; batch++) {
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			
			for (int i = 0; i < BATCH_SIZE && (batch * BATCH_SIZE + i) < DRAW_COUNT; i++) {
				futures.add(CompletableFuture.runAsync(() -> {
					List<Integer> numbers = drawLottoNumbers();
					countMap.computeIfAbsent(numbers, k -> new AtomicLong(0)).incrementAndGet();
				}, executor));
			}
			
			// 배치 완료 대기
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			
			if ((batch + 1) % 100 == 0) {
				System.out.printf("진행률: %,d / %,d 배치 완료%n", batch + 1, totalBatches);
			}
		}
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
		
		// LottoResult 리스트 생성
		List<LottoResult> lottoResults = countMap.entrySet().stream()
				.map(entry -> new LottoResult(
						entry.getKey(),
						entry.getKey().stream().mapToInt(Integer::intValue).sum(),
						entry.getValue().get()
						))
				.sorted(Comparator.comparing(LottoResult::getCount).reversed())
//				.filter(entry -> entry.getCount() == 24)
//				.limit(5)	// 상위 n개만 출력
				.collect(Collectors.toList());
//		System.out.printf("%-20s %-5s %-5s%n", "Numbers", "Sum", "Count");
//		System.out.println("------------------------------------------");
//		lottoResults.forEach(r ->
//			System.out.printf("%-20s %-5d %-5d%n", r.getNumbers(), r.getSum(), r.getCount())
//		);

		return lottoResults;
	}
	
	/**
	 * 멀티스레드 사용하여 대량 추첨. 상위 중복건수 리턴
	 * @param cost
	 * @return
	 * @throws InterruptedException
	 */
	public List<LottoResult> collectLottoNumbers(int cost) throws InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		
		// thread-safe 누적용 맵
		ConcurrentMap<List<Integer>, AtomicLong> countMap = new ConcurrentHashMap<>();
		
		long totalBatches = (DRAW_COUNT + BATCH_SIZE - 1) / BATCH_SIZE;
		
		System.out.printf("총 %,d회 추첨을 %,d회 배치로 처리합니다.%n", DRAW_COUNT, totalBatches);
		
		for (long batch = 0; batch < totalBatches; batch++) {
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			
			for (int i = 0; i < BATCH_SIZE && (batch * BATCH_SIZE + i) < DRAW_COUNT; i++) {
				futures.add(CompletableFuture.runAsync(() -> {
					List<Integer> numbers = drawLottoNumbers();
					countMap.computeIfAbsent(numbers, k -> new AtomicLong(0)).incrementAndGet();
				}, executor));
			}
			
			// 배치 완료 대기
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			
			if ((batch + 1) % 100 == 0) {
				System.out.printf("진행률: %,d / %,d 배치 완료%n", batch + 1, totalBatches);
			}
		}
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
		
		// LottoResult 리스트 생성
		List<LottoResult> lottoResults = countMap.entrySet().stream()
				.map(entry -> new LottoResult(
						entry.getKey(),
						entry.getKey().stream().mapToInt(Integer::intValue).sum(),
						entry.getValue().get()
						))
				.sorted(Comparator.comparing(LottoResult::getCount).reversed())
//				.filter(entry -> entry.getCount() == 24)
				.limit(cost)	// 상위 n개만 출력
				.collect(Collectors.toList());
		
		System.out.printf("%-20s %-5s %-5s%n", "Numbers", "Sum", "Count");
		System.out.println("------------------------------------------");
		lottoResults.forEach(r ->
			System.out.printf("%-20s %-5d %-5d%n", r.getNumbers(), r.getSum(), r.getCount())
		);
		
		return lottoResults;
	}
	
	public List<Integer> drawLottoNumbers() {
		List<Integer> numbers = IntStream.rangeClosed(START_LOTTO_NUMBER, END_LOTTO_NUMBER)
				.boxed()
				.collect(Collectors.toList());
		
		Collections.shuffle(numbers);
		
		return numbers.stream()
				.limit(LOTTO_NUMBER_COUNT)
				.sorted()
				.collect(Collectors.toList());
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
}