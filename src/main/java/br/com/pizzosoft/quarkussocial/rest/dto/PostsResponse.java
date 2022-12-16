package br.com.pizzosoft.quarkussocial.rest.dto;

import br.com.pizzosoft.quarkussocial.domain.social.Posts;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostsResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostsResponse fromEntity(Posts posts) {
        var response = new PostsResponse();
        response.setText(posts.getText());
        response.setDateTime(posts.getDateTime());
        return response;
    }
}
