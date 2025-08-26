package com.tuto.library.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import com.tuto.library.domain.Member;
import com.tuto.library.repository.MemberRepository;
import com.tuto.library.transferobjetcs.MemberTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class MemberResourceIT {

    @Inject
    MemberRepository memberRepository;

    @Transactional
    Member createMember(String name, String email) {
        Member member = new Member(name, email);
        memberRepository.persist(member);
        return member;
    }

    @Test
    void testGetAllMembers() {
        given()
                .when().get("/members")
                .then()
                .statusCode(200);
    }

    @Test
    void testCreateMember() {
        MemberTO member = new MemberTO("John Doe", "john.doe@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(member)
                .when().post("/members")
                .then()
                .statusCode(201)
                .body("name", is("John Doe"))
                .body("email", is("john.doe@example.com"));
    }

    @Test
    void testCreateMemberWithMissingName() {
        MemberTO member = new MemberTO(null, "test@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(member)
                .when().post("/members")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetMemberById() {
        Member persistedMember = createMember("Jane Smith", "jane.smith@example.com");

        given()
                .when().get("/members/{id}", persistedMember.getId())
                .then()
                .statusCode(200)
                .body("name", is("Jane Smith"))
                .body("email", is("jane.smith@example.com"));
    }

    @Test
    void testGetMemberByIdNotFound() {
        given()
                .when().get("/members/999")
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateMember() {
        Member persistedMember = createMember("Original Name", "original@example.com");

        MemberTO updatedMember = new MemberTO("Updated Name", "updated@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(updatedMember)
                .when().put("/members/{id}", persistedMember.getId())
                .then()
                .statusCode(200)
                .body("name", is("Updated Name"))
                .body("email", is("updated@example.com"));
    }

    @Test
    void testDeleteMember() {
        Member persistedMember = createMember("Member to Delete", "delete@example.com");

        given()
                .when().delete("/members/{id}", persistedMember.getId())
                .then()
                .statusCode(204);

        given()
                .when().get("/members/{id}", persistedMember.getId())
                .then()
                .statusCode(404);
    }
}
