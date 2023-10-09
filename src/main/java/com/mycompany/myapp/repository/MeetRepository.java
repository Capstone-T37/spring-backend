package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Meet entity.
 */
@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {
    @Query("select meet from Meet meet where meet.user.login = ?#{principal.username}")
    List<Meet> findByUserIsCurrentUser();

    List<Meet> findByUser(User user);

    default Optional<Meet> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Meet> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Meet> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    Page<Meet> findByUserNot(Pageable pageable, User user);

    @Query(
        value = "select distinct meet from Meet meet left join fetch meet.user",
        countQuery = "select count(distinct meet) from Meet meet"
    )
    Page<Meet> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct meet from Meet meet left join fetch meet.user")
    List<Meet> findAllWithToOneRelationships();

    @Query("select meet from Meet meet left join fetch meet.user where meet.id =:id")
    Optional<Meet> findOneWithToOneRelationships(@Param("id") Long id);
}
