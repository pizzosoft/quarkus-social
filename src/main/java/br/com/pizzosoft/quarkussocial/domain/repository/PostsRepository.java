package br.com.pizzosoft.quarkussocial.domain.repository;

import br.com.pizzosoft.quarkussocial.domain.social.Posts;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostsRepository implements PanacheRepository<Posts> {

}
