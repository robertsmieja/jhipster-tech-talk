package com.robertsmieja.jhipster.repository;

import com.robertsmieja.jhipster.domain.BlogPost;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the BlogPost entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    @Query("select blog_post from BlogPost blog_post where blog_post.user.login = ?#{principal.username}")
    List<BlogPost> findByUserIsCurrentUser();

}
