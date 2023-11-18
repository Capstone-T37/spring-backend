package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Request entity.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select request from Request request where request.user.login = ?#{principal.username}")
    List<Request> findByUserIsCurrentUser();

    default Optional<Request> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Request> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Request> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    boolean existsByUserAndMeet(User user, Meet meet);

    @Query(
        value = "select r from Request r where r.meet.isEnabled = true and  r.meet.user.login = :login",
        countQuery = "select count(r) from Request r where r.meet.isEnabled = true and r.meet.user.login = :login"
    )
    Page<Request> findRequestsWithMatchingUserLoginInMeet(Pageable pageable, @Param("login") String login);

    @Query("select count(r) from Request r where r.meet.isEnabled = true and  r.meet.user.login = :login")
    Long countRequestsWithMatchingUserLoginInMeet(@Param("login") String login);

    @Query(
        value = "select distinct request from Request request left join fetch request.user",
        countQuery = "select count(distinct request) from Request request"
    )
    Page<Request> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct request from Request request left join fetch request.user")
    List<Request> findAllWithToOneRelationships();

    @Query("select request from Request request left join fetch request.user where request.id =:id")
    Optional<Request> findOneWithToOneRelationships(@Param("id") Long id);
}
