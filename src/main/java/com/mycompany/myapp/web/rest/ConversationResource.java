package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Conversation;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.CreateConversationDto;
import com.mycompany.myapp.dto.GetConversationDto;
import com.mycompany.myapp.dto.GetParticipantDto;
import com.mycompany.myapp.repository.ConversationRepository;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Conversation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ConversationResource {

    private final Logger log = LoggerFactory.getLogger(ConversationResource.class);

    private static final String ENTITY_NAME = "conversation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConversationRepository conversationRepository;
    private final UserService userService;

    public ConversationResource(ConversationRepository conversationRepository, UserService userService) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /conversations} : Create a new conversation.
     *
     * @param dto the conversation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conversation, or with status {@code 400 (Bad Request)} if the conversation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/conversations")
    public ResponseEntity createConversation(@Valid @RequestBody CreateConversationDto dto) throws URISyntaxException {
        log.debug("REST request to save Conversation : {}", dto);
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Optional<User> otherUser = userService.getUserWithAuthoritiesByLogin(dto.getUserName());
        if (otherUser.isEmpty()) {
            throw new BadRequestAlertException("invalid userName", ENTITY_NAME, "userName doesnt exist");
        }
        if (conversationRepository.findByBothUsers(otherUser.get(), user.get()).isPresent()) {
            return ResponseEntity.ok().build();
        }
        Conversation result = conversationRepository.save(Conversation.builder().users(Set.of(user.get(), otherUser.get())).build());
        return ResponseEntity.created(new URI("/api/conversations/" + result.getId())).build();
    }

    /**
     * {@code PUT  /conversations/:id} : Updates an existing conversation.
     *
     * @param id the id of the conversation to save.
     * @param conversation the conversation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversation,
     * or with status {@code 400 (Bad Request)} if the conversation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conversation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/conversations/{id}")
    public ResponseEntity<Conversation> updateConversation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Conversation conversation
    ) throws URISyntaxException {
        log.debug("REST request to update Conversation : {}, {}", id, conversation);
        if (conversation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conversation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conversationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Conversation result = conversationRepository.save(conversation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, conversation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /conversations/:id} : Partial updates given fields of an existing conversation, field will ignore if it is null
     *
     * @param id the id of the conversation to save.
     * @param conversation the conversation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversation,
     * or with status {@code 400 (Bad Request)} if the conversation is not valid,
     * or with status {@code 404 (Not Found)} if the conversation is not found,
     * or with status {@code 500 (Internal Server Error)} if the conversation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/conversations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Conversation> partialUpdateConversation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Conversation conversation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Conversation partially : {}, {}", id, conversation);
        if (conversation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conversation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conversationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Conversation> result = conversationRepository
            .findById(conversation.getId())
            .map(existingConversation -> {
                return existingConversation;
            })
            .map(conversationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, conversation.getId().toString())
        );
    }

    /**
     * {@code GET  /conversations} : get all the conversations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conversations in body.
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getAllConversations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Conversations");
        Page<Conversation> page;
        if (eagerload) {
            page = conversationRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = conversationRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/conversations/users")
    public ResponseEntity<List<GetConversationDto>> getAllUsersWithConversations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Conversations");
        Optional<User> user = userService.getUserWithAuthorities();
        if (user.isEmpty()) {
            throw new IllegalCallerException("No user is logged in");
        }
        Page<GetConversationDto> page;
        page =
            conversationRepository
                .findAllByUsersContains(pageable, user.get())
                .map(e -> {
                    User other = findOtherUser(e.getUsers(), user.get());
                    return GetConversationDto.builder().id(e.getId()).userName(other.getLogin()).imageUrl(other.getImageUrl()).build();
                });

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /conversations/:id} : get the "id" conversation.
     *
     * @param id the id of the conversation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conversation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/conversations/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        log.debug("REST request to get Conversation : {}", id);
        Optional<Conversation> conversation = conversationRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(conversation);
    }

    /**
     * {@code DELETE  /conversations/:id} : delete the "id" conversation.
     *
     * @param id the id of the conversation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        log.debug("REST request to delete Conversation : {}", id);
        conversationRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private User findOtherUser(Set<User> users, User user) {
        if (users == null || user == null) {
            return null;
        }

        for (User otherUser : users) {
            if (!otherUser.equals(user)) {
                return otherUser;
            }
        }

        return null; // Return null if no other user is found
    }
}
