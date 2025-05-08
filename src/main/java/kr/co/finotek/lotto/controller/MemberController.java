package kr.co.finotek.lotto.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.finotek.lotto.domain.member.Member;
import kr.co.finotek.lotto.dto.LoginDto;
import kr.co.finotek.lotto.dto.MemberCreateRequestDto;
import kr.co.finotek.lotto.dto.MemberResponseDto;
import kr.co.finotek.lotto.service.MemberService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

	private final MemberService memberService;
	
	@PostMapping("/regist")
	public Member registMember(@RequestBody MemberCreateRequestDto memberDto) {
		
		return memberService.registMember(memberDto);
	}
	
//	@PostMapping("/login")
//	public MemberResponseDto memberLogin(@RequestBody LoginDto memberDto) {
//		
//		System.out.println("memberDto -1- >> " + memberDto.getMember_id());
//		System.out.println("memberDto -2- >> " + memberDto.getPassword());
//		
//		return memberService.loginChk(memberDto);
//	}
	
	
	@PostMapping("/login")
	public Boolean memberLogin(@RequestBody LoginDto memberDto) {
		
		System.out.println("memberDto -1- >> " + memberDto.getMember_id());
		System.out.println("memberDto -2- >> " + memberDto.getPassword());
		boolean result = memberService.loginChk(memberDto);
		
		return result;
	}
}
