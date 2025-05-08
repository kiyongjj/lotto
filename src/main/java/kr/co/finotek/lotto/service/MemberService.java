package kr.co.finotek.lotto.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.finotek.lotto.domain.member.Member;
import kr.co.finotek.lotto.dto.LoginDto;
import kr.co.finotek.lotto.dto.MemberCreateRequestDto;
import kr.co.finotek.lotto.dto.MemberResponseDto;
import kr.co.finotek.lotto.mapper.MemberMapper;
import kr.co.finotek.lotto.repo.MemberRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private MemberMapper memberMapper;

	@Transactional
	public Member registMember(MemberCreateRequestDto memberDto) {

		return memberRepository.save(memberDto.toEntity());
	}
	@Transactional(readOnly = true)
	public Boolean loginChk(LoginDto memberDto) {

		boolean rtn = false;

		List<LoginDto> member = memberMapper.selectUserForLogin();
		System.out.println("member : " + member);
		if(member.size() > 0) {
			rtn = true;
		}
		
		return rtn;
	}
}
