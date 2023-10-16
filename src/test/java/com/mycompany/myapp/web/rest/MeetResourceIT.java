package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.MeetRepository;
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
 * Integration tests for the {@link MeetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MeetResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ENABLED = false;
    private static final Boolean UPDATED_IS_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/meets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MeetRepository meetRepository;

    @Mock
    private MeetRepository meetRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMeetMockMvc;

    private Meet meet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Meet createEntity(EntityManager em) {
        Meet meet = new Meet().description(DEFAULT_DESCRIPTION).isEnabled(DEFAULT_IS_ENABLED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        meet.setUser(user);
        return meet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Meet createUpdatedEntity(EntityManager em) {
        Meet meet = new Meet().description(UPDATED_DESCRIPTION).isEnabled(UPDATED_IS_ENABLED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        meet.setUser(user);
        return meet;
    }

    @BeforeEach
    public void initTest() {
        meet = createEntity(em);
    }

    @Test
    @Transactional
    void createMeet() throws Exception {
        int databaseSizeBeforeCreate = meetRepository.findAll().size();
        // Create the Meet
        restMeetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(meet)))
            .andExpect(status().isCreated());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeCreate + 1);
        Meet testMeet = meetList.get(meetList.size() - 1);
        assertThat(testMeet.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMeet.getIsEnabled()).isEqualTo(UPDATED_IS_ENABLED);
    }

    @SuppressWarnings({ "unchecked" })
    void createSecondMeetShouldThrowException() throws Exception {
        when(meetRepositoryMock.findByUser(any())).thenReturn(List.of(meet));
        // An entity with an existing ID cannot be created, so this API call must fail
        restMeetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(meet)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = meetRepository.findAll().size();
        // set the field null
        meet.setDescription(null);

        // Create the Meet, which fails.

        restMeetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(meet)))
            .andExpect(status().isBadRequest());

        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMeets() throws Exception {
        // Initialize the database
        meetRepository.saveAndFlush(meet);

        // Get all the meetList
        restMeetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(meet.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isEnabled").value(hasItem(DEFAULT_IS_ENABLED.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMeetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(meetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMeetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(meetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMeetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(meetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMeetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(meetRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMeet() throws Exception {
        // Initialize the database
        meetRepository.saveAndFlush(meet);

        // Get the meet
        restMeetMockMvc
            .perform(get(ENTITY_API_URL_ID, meet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(meet.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isEnabled").value(DEFAULT_IS_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingMeet() throws Exception {
        // Get the meet
        restMeetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMeet() throws Exception {
        // Initialize the database
        meetRepository.saveAndFlush(meet);

        int databaseSizeBeforeUpdate = meetRepository.findAll().size();

        // Update the meet
        Meet updatedMeet = meetRepository.findById(meet.getId()).get();
        // Disconnect from session so that the updates on updatedMeet are not directly saved in db
        em.detach(updatedMeet);
        updatedMeet.description(UPDATED_DESCRIPTION).isEnabled(UPDATED_IS_ENABLED);

        restMeetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMeet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMeet))
            )
            .andExpect(status().isOk());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
        Meet testMeet = meetList.get(meetList.size() - 1);
        assertThat(testMeet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMeet.getIsEnabled()).isEqualTo(UPDATED_IS_ENABLED);
    }

    @Test
    @Transactional
    void putNonExistingMeet() throws Exception {
        int databaseSizeBeforeUpdate = meetRepository.findAll().size();
        meet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, meet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(meet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMeet() throws Exception {
        int databaseSizeBeforeUpdate = meetRepository.findAll().size();
        meet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(meet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMeet() throws Exception {
        int databaseSizeBeforeUpdate = meetRepository.findAll().size();
        meet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(meet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMeetWithPatch() throws Exception {
        // Initialize the database
        meetRepository.saveAndFlush(meet);

        int databaseSizeBeforeUpdate = meetRepository.findAll().size();

        // Update the meet using partial update
        Meet partialUpdatedMeet = new Meet();
        partialUpdatedMeet.setId(meet.getId());

        partialUpdatedMeet.description(UPDATED_DESCRIPTION);

        restMeetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMeet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMeet))
            )
            .andExpect(status().isOk());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
        Meet testMeet = meetList.get(meetList.size() - 1);
        assertThat(testMeet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMeet.getIsEnabled()).isEqualTo(DEFAULT_IS_ENABLED);
    }

    @Test
    @Transactional
    void fullUpdateMeetWithPatch() throws Exception {
        // Initialize the database
        meetRepository.saveAndFlush(meet);

        int databaseSizeBeforeUpdate = meetRepository.findAll().size();

        // Update the meet using partial update
        Meet partialUpdatedMeet = new Meet();
        partialUpdatedMeet.setId(meet.getId());

        partialUpdatedMeet.description(UPDATED_DESCRIPTION).isEnabled(UPDATED_IS_ENABLED);

        restMeetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMeet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMeet))
            )
            .andExpect(status().isOk());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
        Meet testMeet = meetList.get(meetList.size() - 1);
        assertThat(testMeet.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMeet.getIsEnabled()).isEqualTo(UPDATED_IS_ENABLED);
    }

    @Test
    @Transactional
    void patchNonExistingMeet() throws Exception {
        int databaseSizeBeforeUpdate = meetRepository.findAll().size();
        meet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, meet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(meet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMeet() throws Exception {
        int databaseSizeBeforeUpdate = meetRepository.findAll().size();
        meet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(meet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMeet() throws Exception {
        int databaseSizeBeforeUpdate = meetRepository.findAll().size();
        meet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(meet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Meet in the database
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMeet() throws Exception {
        // Initialize the database
        meetRepository.saveAndFlush(meet);

        int databaseSizeBeforeDelete = meetRepository.findAll().size();

        // Delete the meet
        restMeetMockMvc
            .perform(delete(ENTITY_API_URL_ID, meet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Meet> meetList = meetRepository.findAll();
        assertThat(meetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
