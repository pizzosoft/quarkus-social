package br.com.pizzosoft.quarkussocial.rest;

import br.com.pizzosoft.quarkussocial.domain.repository.FollowerRepository;
import br.com.pizzosoft.quarkussocial.domain.repository.PostsRepository;
import br.com.pizzosoft.quarkussocial.domain.repository.UserRepository;
import br.com.pizzosoft.quarkussocial.domain.social.Follower;
import br.com.pizzosoft.quarkussocial.domain.social.User;
import br.com.pizzosoft.quarkussocial.rest.dto.FollowerRequest;
import br.com.pizzosoft.quarkussocial.rest.dto.FollowerResponse;
import br.com.pizzosoft.quarkussocial.rest.dto.FollowersPerUserResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    @Inject
    private UserRepository userRepository;
    @Inject
    private PostsRepository postsRepository;
    @Inject
    private FollowerRepository followerRepository;

    @PUT
    @Transactional
    public Response follwUser(@PathParam("userId") Long userId, FollowerRequest request) {
        User user = userRepository.findById(userId);

        if(userId.equals(request.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("you can't followe yourself").build();
        }

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());
        boolean follows = followerRepository.follows(follower, user);
        if(!follows) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = followerRepository.findByUser(userId);
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowerCount(list.size());
        var followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());
        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
