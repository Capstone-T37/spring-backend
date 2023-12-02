package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.dto.CreateActivityDto;
import com.mycompany.myapp.dto.GetActivityDetailsDto;
import com.mycompany.myapp.dto.GetActivityDto;
import com.mycompany.myapp.repository.ActivityRepository;
import com.mycompany.myapp.repository.ActivityTagRepository;
import com.mycompany.myapp.repository.ParticipantRepository;
import com.mycompany.myapp.repository.TagRepository;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.mapper.ActivityMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Activity}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActivityResource {

    private final Logger log = LoggerFactory.getLogger(ActivityResource.class);

    private static final String ENTITY_NAME = "activity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActivityRepository activityRepository;

    private final ParticipantRepository participantRepository;

    private final ActivityTagRepository activityTagRepository;

    private final UserService userService;

    public ActivityResource(
        ActivityRepository activityRepository,
        UserService userService,
        ParticipantRepository participantRepository,
        ActivityTagRepository activityTagRepository
    ) {
        this.activityRepository = activityRepository;
        this.userService = userService;
        this.participantRepository = participantRepository;
        this.activityTagRepository = activityTagRepository;
    }

    /**
     * {@code POST  /activities} : Create a new activity.
     *
     * @param activityDto the activity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new activity, or with status {@code 400 (Bad Request)} if the activity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Transactional
    @PostMapping("/activities")
    public ResponseEntity createActivity(@Valid @RequestBody CreateActivityDto activityDto) throws URISyntaxException {
        log.debug("REST request to save Activity : {}", activityDto);

        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Activity result = activityRepository.save(
            Activity
                .builder()
                .title(activityDto.getTitle())
                .date(activityDto.getDate())
                .user(user.get())
                .description(activityDto.getDescription())
                .build()
        );

        activityDto
            .getTags()
            .forEach(tag -> {
                log.debug("Processing tag: {}", tag);
                activityTagRepository.save(
                    ActivityTag.builder().activity(result).tag(Tag.builder().id(tag.getId()).title(tag.getTitle()).build()).build()
                );
                log.debug("Tag saved: {}", tag);
            });

        return ResponseEntity
            .created(new URI("/api/activities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .build();
    }

    /**
     * {@code PUT  /activities/:id} : Updates an existing activity.
     *
     * @param id the id of the activity to save.
     * @param activity the activity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated activity,
     * or with status {@code 400 (Bad Request)} if the activity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the activity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Activity activity
    ) throws URISyntaxException {
        log.debug("REST request to update Activity : {}, {}", id, activity);
        if (activity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, activity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!activityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Activity result = activityRepository.save(activity);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, activity.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /activities/:id} : Partial updates given fields of an existing activity, field will ignore if it is null
     *
     * @param id the id of the activity to save.
     * @param activity the activity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated activity,
     * or with status {@code 400 (Bad Request)} if the activity is not valid,
     * or with status {@code 404 (Not Found)} if the activity is not found,
     * or with status {@code 500 (Internal Server Error)} if the activity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/activities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Activity> partialUpdateActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Activity activity
    ) throws URISyntaxException {
        log.debug("REST request to partial update Activity partially : {}, {}", id, activity);
        if (activity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, activity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!activityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Activity> result = activityRepository
            .findById(activity.getId())
            .map(existingActivity -> {
                if (activity.getTitle() != null) {
                    existingActivity.setTitle(activity.getTitle());
                }
                if (activity.getDescription() != null) {
                    existingActivity.setDescription(activity.getDescription());
                }
                if (activity.getDate() != null) {
                    existingActivity.setDate(activity.getDate());
                }

                return existingActivity;
            })
            .map(activityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, activity.getId().toString())
        );
    }

    /**
     * {@code GET  /activities} : get all the activities.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of activities in body.
     */
    @GetMapping("/activities")
    public ResponseEntity<List<GetActivityDto>> getAllActivities(@org.springdoc.api.annotations.ParameterObject Pageable pageable)
        throws Exception {
        log.debug("REST request to get a page of Activities");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Page<GetActivityDto> page;
        page = activityRepository.findByUserNot(pageable, user.get()).map(ActivityMapper::fromEntity);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/activities/filter")
    public ResponseEntity<List<GetActivityDto>> getAllActivitiesWithFilter(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestBody List<Tag> tags
    ) throws Exception {
        log.debug("REST request to get a page of Activities");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Page<GetActivityDto> page;
        if (tags != null && !tags.isEmpty()) {
            page = activityTagRepository.findByTagIn(pageable, tags).map(ActivityTag::getActivity).map(ActivityMapper::fromEntity);
        } else {
            page = activityRepository.findByUserNot(pageable, user.get()).map(ActivityMapper::fromEntity);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /activities/:id} : get the "id" activity.
     *
     * @param id the id of the activity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the activity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/activities/{id}")
    public ResponseEntity<GetActivityDetailsDto> getActivity(@PathVariable Long id) {
        log.debug("REST request to get Activity : {}", id);
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Optional<Activity> activity = activityRepository.findOneWithEagerRelationships(id);
        if (activity.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }
        Activity acti = activity.get();
        List<User> participants = participantRepository.findAllUsersByActivityId(acti.getId());
        List<Tag> tags = activityTagRepository.findAllByActivity(acti).stream().map(ActivityTag::getTag).collect(Collectors.toList());
        Optional<Participant> isParticipant = participantRepository.findByActivityAndUser(acti, user.get());
        return ResponseEntity
            .ok()
            .body(
                GetActivityDetailsDto
                    .builder()
                    .id(acti.getId())
                    .title(acti.getTitle())
                    .date(acti.getDate())
                    .isParticipating(isParticipant.isPresent())
                    .description(acti.getDescription())
                    .userName(acti.getUser().getLogin())
                    .participants(participants.stream().map(User::getLogin).collect(Collectors.toList()))
                    .tags(tags.stream().map(Tag::getTitle).collect(Collectors.toList()))
                    .build()
            );
    }

    /**
     * {@code DELETE  /activities/:id} : delete the "id" activity.
     *
     * @param id the id of the activity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        log.debug("REST request to delete Activity : {}", id);
        activityRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
