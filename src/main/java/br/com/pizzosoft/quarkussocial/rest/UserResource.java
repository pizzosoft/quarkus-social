package br.com.pizzosoft.quarkussocial.rest;

import br.com.pizzosoft.quarkussocial.domain.repository.UserRepository;
import br.com.pizzosoft.quarkussocial.domain.social.User;
import br.com.pizzosoft.quarkussocial.rest.dto.CreateUserRequest;
import br.com.pizzosoft.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserRepository repository;
    @Inject
    private Validator validator;

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userResRequest) {

        Set<ConstraintViolation<CreateUserRequest>> violations = validator
                .validate(userResRequest);

        if(!violations.isEmpty()) {
            return ResponseError
                    .createFromValitation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userResRequest.getAge());
        user.setName(userResRequest.getName());

        repository.persist(user);
        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = repository.findById(id);

        if(user != null) {
            repository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userDAta) {
        User user = repository.findById(id);

        if(user != null) {
            user.setName(userDAta.getName());
            user.setAge(userDAta.getAge());
            return Response.noContent().build();
        }
        return Response.ok(Response.Status.NOT_FOUND).build();
    }
}
