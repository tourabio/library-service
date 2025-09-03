package com.tuto.library.repository;

import com.tuto.library.domain.Member;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class MemberRepository implements PanacheRepository<Member> {
    
    public Optional<Member> findByNameAndEmail(String name, String email) {
        return find("name = ?1 and email = ?2", name, email).firstResultOptional();
    }
}