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
        // GIVEN: simulate that member does not exist
        given(memberRepository.findById("member1")).willReturn(Optional.empty());

        // WHEN: attempt to delete the member
        Throwable thrown = catchThrowable(() -> memberService.deleteMember("member1"));

        // THEN: verify the correct exception is thrown with correct message
        assertThat(thrown)
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("Member with ID member1 not found");
    }

    // You can add more tests here for successful deletion, etc.
}
