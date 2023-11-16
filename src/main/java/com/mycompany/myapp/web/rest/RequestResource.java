package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.CreateRequestDto;
import com.mycompany.myapp.dto.GetRequestDto;
import com.mycompany.myapp.repository.MeetRepository;
import com.mycompany.myapp.repository.RequestRepository;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.mapper.RequestMapper;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Request}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RequestResource {

    private final Logger log = LoggerFactory.getLogger(RequestResource.class);

    private static final String ENTITY_NAME = "request";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RequestRepository requestRepository;
    private final MeetRepository meetRepository;

    private final UserService userService;

    public RequestResource(RequestRepository requestRepository, UserService userService, MeetRepository meetRepository) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.meetRepository = meetRepository;
    }

    /**
     * {@code POST  /requests} : Create a new request.
     *
     * @param request the request to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new request, or with status {@code 400 (Bad Request)} if the request has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/requests")
    public ResponseEntity<Request> createRequest(@Valid @RequestBody CreateRequestDto request) throws URISyntaxException {
        log.debug("REST request to save Request : {}", request);

        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }

        Optional<Meet> meet = meetRepository.findById(request.getMeetId());
        if (meet.isEmpty() || !meet.get().getIsEnabled()) {
            throw new IllegalCallerException("Invalid meet");
        }

        Request result = requestRepository.save(Request.builder().user(user.get()).meet(meet.get()).build());

        return ResponseEntity
            .created(new URI("/api/requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .build();
    }

    /**
     * {@code PUT  /requests/:id} : Updates an existing request.
     *
     * @param id the id of the request to save.
     * @param request the request to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated request,
     * or with status {@code 400 (Bad Request)} if the request is not valid,
     * or with status {@code 500 (Internal Server Error)} if the request couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/requests/{id}")
    public ResponseEntity<Request> updateRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Request request
    ) throws URISyntaxException {
        log.debug("REST request to update Request : {}, {}", id, request);
        if (request.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, request.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Request result = requestRepository.save(request);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, request.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /requests/:id} : Partial updates given fields of an existing request, field will ignore if it is null
     *
     * @param id the id of the request to save.
     * @param request the request to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated request,
     * or with status {@code 400 (Bad Request)} if the request is not valid,
     * or with status {@code 404 (Not Found)} if the request is not found,
     * or with status {@code 500 (Internal Server Error)} if the request couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Request> partialUpdateRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Request request
    ) throws URISyntaxException {
        log.debug("REST request to partial update Request partially : {}, {}", id, request);
        if (request.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, request.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Request> result = requestRepository
            .findById(request.getId())
            .map(existingRequest -> {
                return existingRequest;
            })
            .map(requestRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, request.getId().toString())
        );
    }

    /**
     * {@code GET  /requests} : get all the requests.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requests in body.
     */
    @GetMapping("/requests")
    public ResponseEntity<List<GetRequestDto>> getAllRequests(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Requests");
        Page<GetRequestDto> page = requestRepository.findAll(pageable).map(RequestMapper::fromEntity);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /requests/received} : get all the requests received.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requests in body.
     */
    @GetMapping("/requests/received")
    public ResponseEntity<List<GetRequestDto>> getAllReceivedRequests(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Requests");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Page<GetRequestDto> page = requestRepository
            .findRequestsWithMatchingUserLoginInMeet(pageable, user.get().getLogin())
            .map(RequestMapper::fromEntity);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /requests/count} : get the count of all the requests received.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requests in body.
     */
    @GetMapping("/requests/received/count")
    public ResponseEntity<Long> getAllReceivedRequestsCount() {
        log.debug("REST request to get a page of Requests");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        long count = requestRepository.countRequestsWithMatchingUserLoginInMeet(user.get().getLogin());
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /requests/:id} : get the "id" request.
     *
     * @param id the id of the request to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the request, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/requests/{id}")
    public ResponseEntity<Request> getRequest(@PathVariable Long id) {
        log.debug("REST request to get Request : {}", id);
        Optional<Request> request = requestRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(request);
    }

    /**
     * {@code DELETE  /requests/:id} : delete the "id" request.
     *
     * @param id the id of the request to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/requests/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        log.debug("REST request to delete Request : {}", id);
        requestRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
