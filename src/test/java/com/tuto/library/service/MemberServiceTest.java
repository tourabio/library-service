package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    void shouldThrowMemberNotFoundException_whenDeletingNonExistentMember() {
        //TODO 1.0 : @Mohamed Sayed, implement this test
        //given
        given(memberRepository.findById("member1")).willReturn(Optional.empty());
        //when
        Throwable thrown = catchThrowable(() ->{ memberService.deleteMember("member1");});

        //then
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("Member with ID member1 not found.");
    }
}
