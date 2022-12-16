package br.com.pizzosoft.quarkussocial.rest;

import br.com.pizzosoft.quarkussocial.domain.repository.FollowerRepository;
import br.com.pizzosoft.quarkussocial.domain.repository.PostsRepository;
import br.com.pizzosoft.quarkussocial.domain.repository.UserRepository;
import br.com.pizzosoft.quarkussocial.domain.social.Posts;
import br.com.pizzosoft.quarkussocial.domain.social.User;
import br.com.pizzosoft.quarkussocial.rest.dto.CreatePostRequest;
import br.com.pizzosoft.quarkussocial.rest.dto.PostsResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostsResource {

    @Inject
    private UserRepository userRepository;
    @Inject
    private PostsRepository postsRepository;
    @Inject
    private FollowerRepository followerRepository;

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Posts posts = new Posts();
        posts.setText(request.getText());
        posts.setUser(user);

        postsRepository.persist(posts);
        return Response
                .status(Response.Status.CREATED)
                .build();
    }

    @GET
    public Response listPost(@PathParam("userId") Long userId,
                             @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("U need to send followerId in Heard Param")
                    .build();
        }

        User follower = userRepository.findById(followerId);
        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        PanacheQuery<Posts> query = postsRepository
                .find(
                        "user",
                        Sort.by("dateTime", Sort.Direction.Descending),
                        user);

        var list = query.list();
        var postsResponseList = list.stream()
                //as duas formas de map vão funcionar corretamente neste tipo de caso mais simples
                .map(PostsResponse::fromEntity)
                //.map(posts -> PostsResponse.fromEntity(posts))
                .collect(Collectors.toList());
        return Response
                .ok(postsResponseList)
                .build();
    }
}
