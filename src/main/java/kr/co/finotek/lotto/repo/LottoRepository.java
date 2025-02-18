package kr.co.finotek.lotto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.finotek.lotto.domain.lotto.Lotto;

@Repository
public interface LottoRepository extends JpaRepository<Lotto, String> {

}
