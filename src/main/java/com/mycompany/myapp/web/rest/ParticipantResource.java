package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.Participant;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.CreateParticipantDto;
import com.mycompany.myapp.dto.GetParticipantDto;
import com.mycompany.myapp.repository.ActivityRepository;
import com.mycompany.myapp.repository.ParticipantRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.UserService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Participant}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(ParticipantResource.class);

    private static final String ENTITY_NAME = "participant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipantRepository participantRepository;
    private final UserService userService;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ParticipantResource(
        ParticipantRepository participantRepository,
        UserService userService,
        ActivityRepository activityRepository,
        UserRepository userRepository
    ) {
        this.participantRepository = participantRepository;
        this.userService = userService;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /participants} : Create a new participant.
     *
     * @param participant the participant to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participant, or with status {@code 400 (Bad Request)} if the participant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/participants")
    public ResponseEntity createParticipant(@Valid @RequestBody CreateParticipantDto participant) throws URISyntaxException {
        log.debug("REST request to save Participant : {}", participant);
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Optional<Activity> activity = activityRepository.findById(participant.getActivityId());
        if (activity.isEmpty()) {
            throw new BadRequestAlertException("Activity doesnt exist", "Activity", "id doesnt exist");
        }
        Optional<Participant> participant2 = participantRepository.findByActivityAndUser(activity.get(), user.get());

        if (participant2.isPresent()) {
            throw new BadRequestAlertException("User already a participant", ENTITY_NAME, "already exists");
        }

        if (activity.get().getUser().equals(user.get())) {
            throw new BadRequestAlertException("User owns the activity", "", "");
        }

        Participant result = participantRepository.save(Participant.builder().activity(activity.get()).user(user.get()).build());
        return ResponseEntity
            .created(new URI("/api/participants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .build();
    }

    /**
     * {@code GET  /participants/activity/:id} : get all the participants of activity.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participants in body.
     */
    @GetMapping("/participants/activity/{id}")
    public ResponseEntity<List<GetParticipantDto>> getAllParticipantsOfActivity(
        @PathVariable Long id,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of Participants for an activity");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isEmpty()) {
            throw new BadRequestAlertException("Activity doesnt exist", "Activity", "id doesnt exist");
        }
        Page<GetParticipantDto> page;

        page =
            participantRepository
                .findByActivity(pageable, activity.get())
                .map(e -> {
                    return GetParticipantDto
                        .builder()
                        .userName(userRepository.findById((e.getUser().getId())).get().getLogin())
                        .id(e.getId())
                        .build();
                });

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code PUT  /participants/:id} : Updates an existing participant.
     *
     * @param id the id of the participant to save.
     * @param participant the participant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participant,
     * or with status {@code 400 (Bad Request)} if the participant is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/participants/{id}")
    public ResponseEntity<Participant> updateParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Participant participant
    ) throws URISyntaxException {
        log.debug("REST request to update Participant : {}, {}", id, participant);
        if (participant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Participant result = participantRepository.save(participant);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participant.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /participants/:id} : Partial updates given fields of an existing participant, field will ignore if it is null
     *
     * @param id the id of the participant to save.
     * @param participant the participant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participant,
     * or with status {@code 400 (Bad Request)} if the participant is not valid,
     * or with status {@code 404 (Not Found)} if the participant is not found,
     * or with status {@code 500 (Internal Server Error)} if the participant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/participants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Participant> partialUpdateParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Participant participant
    ) throws URISyntaxException {
        log.debug("REST request to partial update Participant partially : {}, {}", id, participant);
        if (participant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Participant> result = participantRepository
            .findById(participant.getId())
            .map(existingParticipant -> {
                return existingParticipant;
            })
            .map(participantRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participant.getId().toString())
        );
    }

    /**
     * {@code GET  /participants} : get all the participants.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participants in body.
     */
    @GetMapping("/participants")
    public ResponseEntity<List<Participant>> getAllParticipants(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Participants");
        Page<Participant> page;
        if (eagerload) {
            page = participantRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = participantRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /participants/:id} : get the "id" participant.
     *
     * @param id the id of the participant to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participant, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/participants/{id}")
    public ResponseEntity<Participant> getParticipant(@PathVariable Long id) {
        log.debug("REST request to get Participant : {}", id);
        Optional<Participant> participant = participantRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(participant);
    }

    /**
     * {@code DELETE  /participants/:id} : delete the "id" participant.
     *
     * @param id the id of the participant to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/participants/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        log.debug("REST request to delete Participant : {}", id);
        participantRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
