package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.domain.Member;
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
    void shouldThrowMemberNotFoundException_whenMemberNotFound() {
        // GIVEN
        given(memberRepository.findById("member1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> memberService.findMemberById("member1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("member1");
    }

    @Test
    void shouldThrowMemberNotFoundException_whenUpdatingNonExistentMember() {
        // GIVEN
        Member member = new Member("member1", "John Doe", "john@example.com");
        given(memberRepository.findById("member1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> memberService.updateMember(member));

        // THEN
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("member1");
    }

    @Test
    void shouldDeleteMember_whenMemberExists() {
        // GIVEN
        Member member = new Member("member1", "John Doe", "john@example.com");
        given(memberRepository.findById("member1")).willReturn(Optional.of(member));

        // WHEN/THEN
        assertThatCode(() -> memberService.deleteMember("member1")).doesNotThrowAnyException();
        then(memberRepository).should().deleteById("member1");
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
