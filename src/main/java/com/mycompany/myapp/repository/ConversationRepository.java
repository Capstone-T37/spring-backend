package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Conversation;
import com.mycompany.myapp.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Conversation entity.
 *
 * When extending this class, extend ConversationRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ConversationRepository extends ConversationRepositoryWithBagRelationships, JpaRepository<Conversation, Long> {
    default Optional<Conversation> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Conversation> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Conversation> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Page<Conversation> findAllByUsersContains(Pageable pageable, User user);

    @Query("SELECT c FROM Conversation c WHERE :user1 MEMBER OF c.users AND :user2 MEMBER OF c.users")
    Optional<Conversation> findByBothUsers(@Param("user1") User user1, @Param("user2") User user2);
}
