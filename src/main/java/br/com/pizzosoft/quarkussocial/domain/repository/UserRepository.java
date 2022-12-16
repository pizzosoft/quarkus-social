package br.com.pizzosoft.quarkussocial.domain.repository;

import br.com.pizzosoft.quarkussocial.domain.social.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {



}
