package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.ActivityTag;
import com.mycompany.myapp.domain.Tag;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.ActivityTagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ActivityTagResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActivityTagResourceIT {

    private static final String ENTITY_API_URL = "/api/activity-tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActivityTagRepository activityTagRepository;

    @Mock
    private ActivityTagRepository activityTagRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActivityTagMockMvc;

    private ActivityTag activityTag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActivityTag createEntity(EntityManager em) {
        ActivityTag activityTag = new ActivityTag();
        // Add required entity
        Tag tag;
        if (TestUtil.findAll(em, Tag.class).isEmpty()) {
            tag = TagResourceIT.createEntity(em);
            em.persist(tag);
            em.flush();
        } else {
            tag = TestUtil.findAll(em, Tag.class).get(0);
        }
        activityTag.setTag(tag);
        // Add required entity
        Activity activity;
        if (TestUtil.findAll(em, Activity.class).isEmpty()) {
            activity = ActivityResourceIT.createEntity(em);
            em.persist(activity);
            em.flush();
        } else {
            activity = TestUtil.findAll(em, Activity.class).get(0);
        }
        activityTag.setActivity(activity);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        activityTag.setUser(user);
        return activityTag;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActivityTag createUpdatedEntity(EntityManager em) {
        ActivityTag activityTag = new ActivityTag();
        // Add required entity
        Tag tag;
        if (TestUtil.findAll(em, Tag.class).isEmpty()) {
            tag = TagResourceIT.createUpdatedEntity(em);
            em.persist(tag);
            em.flush();
        } else {
            tag = TestUtil.findAll(em, Tag.class).get(0);
        }
        activityTag.setTag(tag);
        // Add required entity
        Activity activity;
        if (TestUtil.findAll(em, Activity.class).isEmpty()) {
            activity = ActivityResourceIT.createUpdatedEntity(em);
            em.persist(activity);
            em.flush();
        } else {
            activity = TestUtil.findAll(em, Activity.class).get(0);
        }
        activityTag.setActivity(activity);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        activityTag.setUser(user);
        return activityTag;
    }

    @BeforeEach
    public void initTest() {
        activityTag = createEntity(em);
    }

    @Test
    @Transactional
    void createActivityTag() throws Exception {
        int databaseSizeBeforeCreate = activityTagRepository.findAll().size();
        // Create the ActivityTag
        restActivityTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activityTag)))
            .andExpect(status().isCreated());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeCreate + 1);
        ActivityTag testActivityTag = activityTagList.get(activityTagList.size() - 1);
    }

    @Test
    @Transactional
    void createActivityTagWithExistingId() throws Exception {
        // Create the ActivityTag with an existing ID
        activityTag.setId(1L);

        int databaseSizeBeforeCreate = activityTagRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activityTag)))
            .andExpect(status().isBadRequest());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllActivityTags() throws Exception {
        // Initialize the database
        activityTagRepository.saveAndFlush(activityTag);

        // Get all the activityTagList
        restActivityTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activityTag.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllActivityTagsWithEagerRelationshipsIsEnabled() throws Exception {
        when(activityTagRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restActivityTagMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(activityTagRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllActivityTagsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(activityTagRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restActivityTagMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(activityTagRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getActivityTag() throws Exception {
        // Initialize the database
        activityTagRepository.saveAndFlush(activityTag);

        // Get the activityTag
        restActivityTagMockMvc
            .perform(get(ENTITY_API_URL_ID, activityTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(activityTag.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingActivityTag() throws Exception {
        // Get the activityTag
        restActivityTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingActivityTag() throws Exception {
        // Initialize the database
        activityTagRepository.saveAndFlush(activityTag);

        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();

        // Update the activityTag
        ActivityTag updatedActivityTag = activityTagRepository.findById(activityTag.getId()).get();
        // Disconnect from session so that the updates on updatedActivityTag are not directly saved in db
        em.detach(updatedActivityTag);

        restActivityTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActivityTag.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActivityTag))
            )
            .andExpect(status().isOk());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
        ActivityTag testActivityTag = activityTagList.get(activityTagList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingActivityTag() throws Exception {
        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();
        activityTag.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, activityTag.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchActivityTag() throws Exception {
        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();
        activityTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActivityTag() throws Exception {
        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();
        activityTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activityTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActivityTagWithPatch() throws Exception {
        // Initialize the database
        activityTagRepository.saveAndFlush(activityTag);

        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();

        // Update the activityTag using partial update
        ActivityTag partialUpdatedActivityTag = new ActivityTag();
        partialUpdatedActivityTag.setId(activityTag.getId());

        restActivityTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivityTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivityTag))
            )
            .andExpect(status().isOk());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
        ActivityTag testActivityTag = activityTagList.get(activityTagList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateActivityTagWithPatch() throws Exception {
        // Initialize the database
        activityTagRepository.saveAndFlush(activityTag);

        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();

        // Update the activityTag using partial update
        ActivityTag partialUpdatedActivityTag = new ActivityTag();
        partialUpdatedActivityTag.setId(activityTag.getId());

        restActivityTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivityTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivityTag))
            )
            .andExpect(status().isOk());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
        ActivityTag testActivityTag = activityTagList.get(activityTagList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingActivityTag() throws Exception {
        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();
        activityTag.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, activityTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActivityTag() throws Exception {
        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();
        activityTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActivityTag() throws Exception {
        int databaseSizeBeforeUpdate = activityTagRepository.findAll().size();
        activityTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityTagMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(activityTag))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActivityTag in the database
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteActivityTag() throws Exception {
        // Initialize the database
        activityTagRepository.saveAndFlush(activityTag);

        int databaseSizeBeforeDelete = activityTagRepository.findAll().size();

        // Delete the activityTag
        restActivityTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, activityTag.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ActivityTag> activityTagList = activityTagRepository.findAll();
        assertThat(activityTagList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
