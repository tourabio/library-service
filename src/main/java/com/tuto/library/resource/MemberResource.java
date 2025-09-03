package com.tuto.library.resource;

import com.tuto.library.service.MemberService;
import com.tuto.library.transferobjetcs.MemberTO;
import com.tuto.library.transferobjetcs.AuthenticationTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MemberResource {

    private final MemberService memberService;

    @Inject
    public MemberResource(MemberService memberService) {
        this.memberService = memberService;
    }

    @GET
    public List<MemberTO> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GET
    @Path("/{id}")
    public Response getMemberById(@PathParam("id") Long id) {
        MemberTO memberTO = memberService.getMemberById(id);
        if (memberTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(memberTO).build();
    }

    @POST
    public Response createMember(MemberTO memberTO) {
        if (memberTO.name() == null || memberTO.name().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Member name is required")
                    .build();
        }
        if (memberTO.email() == null || memberTO.email().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Member email is required")
                    .build();
        }
        MemberTO created = memberService.createMember(memberTO);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateMember(@PathParam("id") Long id, MemberTO memberTO) {
        try {
            MemberTO updatedMember = memberService.updateMember(id, memberTO);
            return Response.ok(updatedMember).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMember(@PathParam("id") Long id) {
        try {
            memberService.deleteMember(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/authenticate")
    public Response authenticateMember(@QueryParam("name") String name, @QueryParam("email") String email) {
        try {
            AuthenticationTO authTO = memberService.authenticateMember(name, email);
            return Response.ok(authTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Invalid credentials: " + e.getMessage())
                    .build();
        }
    }
}