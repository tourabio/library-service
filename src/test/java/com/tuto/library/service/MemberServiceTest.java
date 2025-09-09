package com.tuto.library.service;

import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    void shouldThrowMemberNotFoundException_whenDeletingNonExistentMember() {
        // GIVEN
        String memberId = "member1";
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> memberService.deleteMember(memberId));

        // THEN
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("Member with ID member1 not found.");


    }
}
