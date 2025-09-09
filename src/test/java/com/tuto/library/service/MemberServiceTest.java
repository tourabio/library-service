package com.tuto.library.service;

import com.tuto.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    void shouldThrowMemberNotFoundException_whenDeletingNonExistentMember() {
        // given
        Long nonExistentMemberId = 999L; // un id qui n'existe pas dans la base
        MemberService memberService = new MemberService(memberRepository);
        // (assure-toi que ton repo est un mock ou une base vide)

        // when
        Throwable thrown = catchThrowable(() -> {
            memberService.deleteMember(nonExistentMemberId);
        });

        // then
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("not found");
    }
