package com.robertsmieja.jhipster.web.rest;
import com.robertsmieja.jhipster.domain.BlogPost;
import com.robertsmieja.jhipster.repository.BlogPostRepository;
import com.robertsmieja.jhipster.web.rest.errors.BadRequestAlertException;
import com.robertsmieja.jhipster.web.rest.util.HeaderUtil;
import com.robertsmieja.jhipster.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing BlogPost.
 */
@RestController
@RequestMapping("/api")
public class BlogPostResource {

    private final Logger log = LoggerFactory.getLogger(BlogPostResource.class);

    private static final String ENTITY_NAME = "blogPost";

    private final BlogPostRepository blogPostRepository;

    public BlogPostResource(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    /**
     * POST  /blog-posts : Create a new blogPost.
     *
     * @param blogPost the blogPost to create
     * @return the ResponseEntity with status 201 (Created) and with body the new blogPost, or with status 400 (Bad Request) if the blogPost has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/blog-posts")
    public ResponseEntity<BlogPost> createBlogPost(@Valid @RequestBody BlogPost blogPost) throws URISyntaxException {
        log.debug("REST request to save BlogPost : {}", blogPost);
        if (blogPost.getId() != null) {
            throw new BadRequestAlertException("A new blogPost cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BlogPost result = blogPostRepository.save(blogPost);
        return ResponseEntity.created(new URI("/api/blog-posts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /blog-posts : Updates an existing blogPost.
     *
     * @param blogPost the blogPost to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated blogPost,
     * or with status 400 (Bad Request) if the blogPost is not valid,
     * or with status 500 (Internal Server Error) if the blogPost couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/blog-posts")
    public ResponseEntity<BlogPost> updateBlogPost(@Valid @RequestBody BlogPost blogPost) throws URISyntaxException {
        log.debug("REST request to update BlogPost : {}", blogPost);
        if (blogPost.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BlogPost result = blogPostRepository.save(blogPost);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, blogPost.getId().toString()))
            .body(result);
    }

    /**
     * GET  /blog-posts : get all the blogPosts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of blogPosts in body
     */
    @GetMapping("/blog-posts")
    public ResponseEntity<List<BlogPost>> getAllBlogPosts(Pageable pageable) {
        log.debug("REST request to get a page of BlogPosts");
        Page<BlogPost> page = blogPostRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/blog-posts");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /blog-posts/:id : get the "id" blogPost.
     *
     * @param id the id of the blogPost to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the blogPost, or with status 404 (Not Found)
     */
    @GetMapping("/blog-posts/{id}")
    public ResponseEntity<BlogPost> getBlogPost(@PathVariable Long id) {
        log.debug("REST request to get BlogPost : {}", id);
        Optional<BlogPost> blogPost = blogPostRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(blogPost);
    }

    /**
     * DELETE  /blog-posts/:id : delete the "id" blogPost.
     *
     * @param id the id of the blogPost to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/blog-posts/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
        log.debug("REST request to delete BlogPost : {}", id);
        blogPostRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
