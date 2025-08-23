package com.tuto.library.service;

import com.tuto.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

class MemberServiceTest {
    private MemberRepository memberRepository;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = BDDMockito.mock(MemberRepository.class);
        memberService = new MemberService(memberRepository);
    }

    @Test
    void shouldThrowMemberNotFoundException_whenDeletingNonExistentMember() {
    }
}
