package com.tuto.library.repository;

import com.tuto.library.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    
    Optional<Member> findById(String memberId);
    
    Member save(Member member);
    
    void deleteById(String memberId);
}