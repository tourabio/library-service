package com.tuto.library.service;

import com.tuto.library.domain.Member;
import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
import com.tuto.library.transferobjetcs.MemberTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberTO updateMember(Long id, MemberTO memberTO) {
        checkMemberExists(id);
        validateMemberInput(id, memberTO.name(), memberTO.email());
        Member member = toEntity(memberTO);
        member.setId(id);
        Member updated = memberRepository.getEntityManager().merge(member);
        return toTO(updated);
    }

    public List<MemberTO> getAllMembers() {
        return memberRepository.listAll().stream().map(this::toTO).toList();
    }

    public MemberTO getMemberById(Long id) {
        return memberRepository.findByIdOptional(id).map(this::toTO).orElse(null);
    }

    public MemberTO createMember(MemberTO memberTO) {
        Member member = toEntity(memberTO);
        validateMemberInput(memberTO.name(), memberTO.email());
        memberRepository.persist(member);
        return toTO(member);
    }

    private void checkMemberExists(final Long memberId) {
        if (memberRepository.findByIdOptional(memberId).isEmpty()) {
            throw new MemberNotFoundException("Member with ID " + memberId + " not found.");
        }
    }

    public void deleteMember(Long id) {
        checkMemberExists(id);
        memberRepository.deleteById(id);
    }

    private void validateMemberInput(Long id, String name, String email) {
        if (id == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty");
        }
        if (isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateMemberInput(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty");
        }
        if (isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isValidEmail(String email) {
        return email == null || !email.contains("@") || !email.contains(".") || email.length() <= 5;
    }

    private MemberTO toTO(Member member) {
        if (member == null) {
            return null;
        }
        return new MemberTO(member.getName(), member.getEmail());
    }

    public Member toEntity(MemberTO memberTO) {
        return new Member(memberTO.name(), memberTO.email());
    }

    public Member findMemberById(Long id) {
        return memberRepository.findByIdOptional(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found."));
    }
}
