package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ActivityTag;
import com.mycompany.myapp.repository.ActivityTagRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ActivityTag}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActivityTagResource {

    private final Logger log = LoggerFactory.getLogger(ActivityTagResource.class);

    private static final String ENTITY_NAME = "activityTag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActivityTagRepository activityTagRepository;

    public ActivityTagResource(ActivityTagRepository activityTagRepository) {
        this.activityTagRepository = activityTagRepository;
    }

    /**
     * {@code POST  /activity-tags} : Create a new activityTag.
     *
     * @param activityTag the activityTag to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new activityTag, or with status {@code 400 (Bad Request)} if the activityTag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/activity-tags")
    public ResponseEntity<ActivityTag> createActivityTag(@Valid @RequestBody ActivityTag activityTag) throws URISyntaxException {
        log.debug("REST request to save ActivityTag : {}", activityTag);
        if (activityTag.getId() != null) {
            throw new BadRequestAlertException("A new activityTag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActivityTag result = activityTagRepository.save(activityTag);
        return ResponseEntity
            .created(new URI("/api/activity-tags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /activity-tags/:id} : Updates an existing activityTag.
     *
     * @param id the id of the activityTag to save.
     * @param activityTag the activityTag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated activityTag,
     * or with status {@code 400 (Bad Request)} if the activityTag is not valid,
     * or with status {@code 500 (Internal Server Error)} if the activityTag couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/activity-tags/{id}")
    public ResponseEntity<ActivityTag> updateActivityTag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ActivityTag activityTag
    ) throws URISyntaxException {
        log.debug("REST request to update ActivityTag : {}, {}", id, activityTag);
        if (activityTag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, activityTag.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!activityTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActivityTag result = activityTagRepository.save(activityTag);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, activityTag.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /activity-tags/:id} : Partial updates given fields of an existing activityTag, field will ignore if it is null
     *
     * @param id the id of the activityTag to save.
     * @param activityTag the activityTag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated activityTag,
     * or with status {@code 400 (Bad Request)} if the activityTag is not valid,
     * or with status {@code 404 (Not Found)} if the activityTag is not found,
     * or with status {@code 500 (Internal Server Error)} if the activityTag couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/activity-tags/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ActivityTag> partialUpdateActivityTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ActivityTag activityTag
    ) throws URISyntaxException {
        log.debug("REST request to partial update ActivityTag partially : {}, {}", id, activityTag);
        if (activityTag.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, activityTag.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!activityTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActivityTag> result = activityTagRepository
            .findById(activityTag.getId())
            .map(existingActivityTag -> {
                return existingActivityTag;
            })
            .map(activityTagRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, activityTag.getId().toString())
        );
    }

    /**
     * {@code GET  /activity-tags} : get all the activityTags.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of activityTags in body.
     */
    @GetMapping("/activity-tags")
    public List<ActivityTag> getAllActivityTags() {
        log.debug("REST request to get all ActivityTags");
        return activityTagRepository.findAll();
    }

    /**
     * {@code GET  /activity-tags/:id} : get the "id" activityTag.
     *
     * @param id the id of the activityTag to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the activityTag, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/activity-tags/{id}")
    public ResponseEntity<ActivityTag> getActivityTag(@PathVariable Long id) {
        log.debug("REST request to get ActivityTag : {}", id);
        Optional<ActivityTag> activityTag = activityTagRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(activityTag);
    }

    /**
     * {@code DELETE  /activity-tags/:id} : delete the "id" activityTag.
     *
     * @param id the id of the activityTag to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/activity-tags/{id}")
    public ResponseEntity<Void> deleteActivityTag(@PathVariable Long id) {
        log.debug("REST request to delete ActivityTag : {}", id);
        activityTagRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
