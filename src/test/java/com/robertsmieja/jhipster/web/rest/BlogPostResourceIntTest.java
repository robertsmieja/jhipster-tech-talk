package com.robertsmieja.jhipster.web.rest;

import com.robertsmieja.jhipster.JhipsterApp;

import com.robertsmieja.jhipster.domain.BlogPost;
import com.robertsmieja.jhipster.repository.BlogPostRepository;
import com.robertsmieja.jhipster.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static com.robertsmieja.jhipster.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BlogPostResource REST controller.
 *
 * @see BlogPostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterApp.class)
public class BlogPostResourceIntTest {

    private static final String DEFAULT_TEXT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Instant DEFAULT_ENTRY_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENTRY_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBlogPostMockMvc;

    private BlogPost blogPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BlogPostResource blogPostResource = new BlogPostResource(blogPostRepository);
        this.restBlogPostMockMvc = MockMvcBuilders.standaloneSetup(blogPostResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlogPost createEntity(EntityManager em) {
        BlogPost blogPost = new BlogPost()
            .text(DEFAULT_TEXT)
            .entryTimestamp(DEFAULT_ENTRY_TIMESTAMP);
        return blogPost;
    }

    @Before
    public void initTest() {
        blogPost = createEntity(em);
    }

    @Test
    @Transactional
    public void createBlogPost() throws Exception {
        int databaseSizeBeforeCreate = blogPostRepository.findAll().size();

        // Create the BlogPost
        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPost)))
            .andExpect(status().isCreated());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeCreate + 1);
        BlogPost testBlogPost = blogPostList.get(blogPostList.size() - 1);
        assertThat(testBlogPost.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testBlogPost.getEntryTimestamp()).isEqualTo(DEFAULT_ENTRY_TIMESTAMP);
    }

    @Test
    @Transactional
    public void createBlogPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = blogPostRepository.findAll().size();

        // Create the BlogPost with an existing ID
        blogPost.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPost)))
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = blogPostRepository.findAll().size();
        // set the field null
        blogPost.setText(null);

        // Create the BlogPost, which fails.

        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPost)))
            .andExpect(status().isBadRequest());

        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEntryTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = blogPostRepository.findAll().size();
        // set the field null
        blogPost.setEntryTimestamp(null);

        // Create the BlogPost, which fails.

        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPost)))
            .andExpect(status().isBadRequest());

        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBlogPosts() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList
        restBlogPostMockMvc.perform(get("/api/blog-posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].entryTimestamp").value(hasItem(DEFAULT_ENTRY_TIMESTAMP.toString())));
    }
    
    @Test
    @Transactional
    public void getBlogPost() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get the blogPost
        restBlogPostMockMvc.perform(get("/api/blog-posts/{id}", blogPost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(blogPost.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.entryTimestamp").value(DEFAULT_ENTRY_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBlogPost() throws Exception {
        // Get the blogPost
        restBlogPostMockMvc.perform(get("/api/blog-posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlogPost() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        int databaseSizeBeforeUpdate = blogPostRepository.findAll().size();

        // Update the blogPost
        BlogPost updatedBlogPost = blogPostRepository.findById(blogPost.getId()).get();
        // Disconnect from session so that the updates on updatedBlogPost are not directly saved in db
        em.detach(updatedBlogPost);
        updatedBlogPost
            .text(UPDATED_TEXT)
            .entryTimestamp(UPDATED_ENTRY_TIMESTAMP);

        restBlogPostMockMvc.perform(put("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBlogPost)))
            .andExpect(status().isOk());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeUpdate);
        BlogPost testBlogPost = blogPostList.get(blogPostList.size() - 1);
        assertThat(testBlogPost.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testBlogPost.getEntryTimestamp()).isEqualTo(UPDATED_ENTRY_TIMESTAMP);
    }

    @Test
    @Transactional
    public void updateNonExistingBlogPost() throws Exception {
        int databaseSizeBeforeUpdate = blogPostRepository.findAll().size();

        // Create the BlogPost

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogPostMockMvc.perform(put("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPost)))
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBlogPost() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        int databaseSizeBeforeDelete = blogPostRepository.findAll().size();

        // Delete the blogPost
        restBlogPostMockMvc.perform(delete("/api/blog-posts/{id}", blogPost.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlogPost.class);
        BlogPost blogPost1 = new BlogPost();
        blogPost1.setId(1L);
        BlogPost blogPost2 = new BlogPost();
        blogPost2.setId(blogPost1.getId());
        assertThat(blogPost1).isEqualTo(blogPost2);
        blogPost2.setId(2L);
        assertThat(blogPost1).isNotEqualTo(blogPost2);
        blogPost1.setId(null);
        assertThat(blogPost1).isNotEqualTo(blogPost2);
    }
}
