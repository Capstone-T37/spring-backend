package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.CreateMeetDto;
import com.mycompany.myapp.dto.GetMeetDto;
import com.mycompany.myapp.repository.MeetRepository;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.mapper.ActivityMapper;
import com.mycompany.myapp.service.mapper.MeetMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Meet}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MeetResource {

    private final Logger log = LoggerFactory.getLogger(MeetResource.class);

    private static final String ENTITY_NAME = "meet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeetRepository meetRepository;
    private final UserService userService;

    public MeetResource(MeetRepository meetRepository, UserService userService) {
        this.meetRepository = meetRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /meets} : Create a new meet.
     *
     * @param meet the meet to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new meet, or with status {@code 400 (Bad Request)} if the meet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/meets")
    public ResponseEntity<Meet> createMeet(@Valid @RequestBody CreateMeetDto meet) throws URISyntaxException {
        log.debug("REST request to save Meet : {}", meet);
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }

        if (meetRepository.findByUser(user.get()).size() > 0) {
            throw new BadRequestAlertException("Only one meet can be active at all times", ENTITY_NAME, "entityExists");
        }

        Meet result = meetRepository.save(Meet.builder().description(meet.getDescription()).user(user.get()).build());
        return ResponseEntity
            .created(new URI("/api/meets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /meets/:id} : Updates an existing meet.
     *
     * @param id the id of the meet to save.
     * @param meet the meet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meet,
     * or with status {@code 400 (Bad Request)} if the meet is not valid,
     * or with status {@code 500 (Internal Server Error)} if the meet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/meets/{id}")
    public ResponseEntity<Meet> updateMeet(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Meet meet)
        throws URISyntaxException {
        log.debug("REST request to update Meet : {}, {}", id, meet);
        if (meet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meet.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Meet result = meetRepository.save(meet);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meet.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /meets/:id} : Partial updates given fields of an existing meet, field will ignore if it is null
     *
     * @param id the id of the meet to save.
     * @param meet the meet to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated meet,
     * or with status {@code 400 (Bad Request)} if the meet is not valid,
     * or with status {@code 404 (Not Found)} if the meet is not found,
     * or with status {@code 500 (Internal Server Error)} if the meet couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/meets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Meet> partialUpdateMeet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Meet meet
    ) throws URISyntaxException {
        log.debug("REST request to partial update Meet partially : {}, {}", id, meet);
        if (meet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, meet.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!meetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Meet> result = meetRepository
            .findById(meet.getId())
            .map(existingMeet -> {
                if (meet.getDescription() != null) {
                    existingMeet.setDescription(meet.getDescription());
                }

                return existingMeet;
            })
            .map(meetRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, meet.getId().toString())
        );
    }

    /**
     * {@code GET  /meets} : get all the meets.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meets in body.
     */
    @GetMapping("/meets")
    public ResponseEntity<List<GetMeetDto>> getAllMeets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Meets");
        Page<GetMeetDto> page;
        if (eagerload) {
            page = meetRepository.findAllWithEagerRelationships(pageable).map(MeetMapper::fromEntity);
        } else {
            page = meetRepository.findAll(pageable).map(MeetMapper::fromEntity);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /meets} : get all the meets.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of meets in body.
     */
    @GetMapping("/meets/exclude-user-meets")
    public ResponseEntity<List<GetMeetDto>> getAllMeetsUserExcluded(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Meets excluding user's meets.");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Page<GetMeetDto> page;
        page = meetRepository.findByUserNot(pageable, user.get()).map(MeetMapper::fromEntity);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /meets/:id} : get the "id" meet.
     *
     * @param id the id of the meet to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the meet, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/meets/{id}")
    public ResponseEntity<Meet> getMeet(@PathVariable Long id) {
        log.debug("REST request to get Meet : {}", id);
        Optional<Meet> meet = meetRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(meet);
    }

    /**
     * {@code DELETE  /meets/:id} : delete the "id" meet.
     *
     * @param id the id of the meet to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/meets/{id}")
    public ResponseEntity<Void> deleteMeet(@PathVariable Long id) {
        log.debug("REST request to delete Meet : {}", id);
        meetRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
