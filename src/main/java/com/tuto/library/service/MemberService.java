package com.tuto.library.service;

import com.tuto.library.domain.Member;
import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member updateMember(Member member) {
        checkMemberExists(member.getId());
        validateMemberInput(member.getId(), member.getName(), member.getEmail());
        return memberRepository.save(member);
    }

    private void checkMemberExists(final String member) {
        if (memberRepository.findById(member).isEmpty()) {
            throw new MemberNotFoundException("Member with ID " + member + " not found.");
        }
    }

    public void deleteMember(String id) {
        checkMemberExists(id);
        memberRepository.deleteById(id);
    }

    private void validateMemberInput(String id, String name, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".") && email.length() > 5;
    }
}
