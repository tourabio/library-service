package com.tuto.library.resource;

import com.tuto.library.service.LoanService;
import com.tuto.library.transferobjetcs.LoanRequestTO;
import com.tuto.library.transferobjetcs.LoanTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class LoanResource {

    private final LoanService loanService;

    @Inject
    public LoanResource(LoanService loanService) {
        this.loanService = loanService;
    }

    @GET
    public List<LoanTO> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GET
    @Path("/{id}")
    public Response getLoanById(@PathParam("id") Long id) {
        LoanTO loanTO = loanService.getLoanById(id);
        if (loanTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(loanTO).build();
    }

    @POST
    public Response createLoan(LoanRequestTO loanRequestTO) {
        try {
            LoanTO createdLoan = loanService.createLoan(loanRequestTO);
            return Response.status(Response.Status.CREATED).entity(createdLoan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}/return")
    public Response returnLoan(@PathParam("id") Long id) {
        try {
            LoanTO returnedLoan = loanService.processLoanReturn(id);
            return Response.ok(returnedLoan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/update-overdue")
    public Response updateOverdueLoans() {
        try {
            List<LoanTO> updatedLoans = loanService.updateOverdueLoans();
            return Response.ok(updatedLoans).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
}