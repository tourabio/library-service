package com.tuto.library.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import com.tuto.library.repository.MemberRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

@QuarkusTest
class MemberResourceIT {

    @Inject
    MemberRepository memberRepository;

    @Test
    void shouldReturnNotFoundException_whenDeletingNonExistentMember() {
        //TODO 1.0 : @Mohamed Sayed, implement this test
    }
}
