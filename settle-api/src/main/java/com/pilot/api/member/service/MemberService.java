package com.pilot.api.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.pilot.api.member.dto.UserAuthDTO;
import com.pilot.api.member.entity.Member;
import com.pilot.api.member.entity.MemberAuth;
import com.pilot.api.member.repository.MemberAuthRepository;
import com.pilot.api.member.repository.MemberRepository;
import com.pilot.common.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
/**
 * ref : https://kimseungjae.tistory.com/11
 * */
@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	private final MemberAuthRepository authRepository;
	
	public Optional<Member> findByMemberId(String memberId){
		return memberRepository.findByMemberId(memberId);
	}

	public Member save(Member member) {
		return memberRepository.save(member);
	}
	
	public List<Member> findByRole(String role){
		return memberRepository.findByRole(role);
	}
	
	public List<Member> findByState(String state){
		return memberRepository.findByState(state);
	}
	
	public List<Member> findAll(){
		return memberRepository.findAll();
	}
	
	public void update(Member dto) {
		Optional<Member> member = memberRepository.findByMemberId(dto.getMemberId());
		member.ifPresent(u->{
			ObjectUtils.copyNotNullProperties(dto, u);
			memberRepository.save(u);
		});
	}
	

	@Transactional
	public MemberAuth saveMemberAuth(String memberId, String authKey) {
		Optional<Member> member = memberRepository.findByMemberId(memberId);
		if(member.isPresent()) {
			MemberAuth memberAuth = MemberAuth.builder()
					.memberKey(member.get())
					.authKey(Long.parseLong(authKey))
					.useYn("Y")
					.build();
			authRepository.save(memberAuth);
			return memberAuth;
		}
		return null;
	}

	@Transactional
	public void updateMemberAuth(UserAuthDTO dto) {
		Optional<Member> member = memberRepository.findByMemberId(dto.getMemberId());
		Optional<MemberAuth> memberAuth = authRepository.findByMemberKeyAndAuthKey(member.get().getMemberKey(), Long.parseLong(dto.getAuthKey()));
		memberAuth.ifPresent(u->{
			ObjectUtils.copyNotNullProperties(dto, u);
			authRepository.save(u);
		});
	}
	
	public List<MemberAuth> findAllMemberAuthList() {
		return authRepository.findAll();
	}
	
	public List<MemberAuth> findByMemberIdAuthList(String memberId) {
		Optional<Member> member = memberRepository.findByMemberId(memberId);
		List<MemberAuth> list = new ArrayList<MemberAuth>();
		if(member.isPresent()) {
			list = authRepository.findByMemberKey(member.get().getMemberKey());
		}
		return list;
	}
	
	public List<MemberAuth> findByAuthKeyMemberAuthList(Long authKey) {
		return authRepository.findByAuthKey(authKey);
	}
}
