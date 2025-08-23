package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import java.util.Optional;

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
        // GIVEN
        given(memberRepository.findById("member1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> memberService.deleteMember("member1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("member1");
    }
}
