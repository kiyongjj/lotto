package kr.co.finotek.lotto.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.finotek.lotto.domain.member.Member;
import kr.co.finotek.lotto.dto.LoginDto;

@Mapper
public interface MemberMapper {

	List<LoginDto> selectMembers();

	List<LoginDto> selectUserForLogin();

}
