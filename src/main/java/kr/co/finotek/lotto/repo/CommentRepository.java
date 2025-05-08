package kr.co.finotek.lotto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.finotek.lotto.domain.board.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
