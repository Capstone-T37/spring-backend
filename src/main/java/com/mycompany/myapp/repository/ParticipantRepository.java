package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.Participant;
import com.mycompany.myapp.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Participant entity.
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("select participant from Participant participant where participant.user.login = ?#{principal.username}")
    List<Participant> findByUserIsCurrentUser();

    default Optional<Participant> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Participant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Participant> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    Page<Participant> findByActivity(Pageable pageable, Activity activity);

    @Query("select p.user from Participant p where p.activity.id = :activityId")
    List<User> findAllUsersByActivityId(@Param("activityId") Long activityId);

    Optional<Participant> findByActivityAndUser(Activity activity, User user);

    @Query(
        value = "select distinct participant from Participant participant left join fetch participant.user",
        countQuery = "select count(distinct participant) from Participant participant"
    )
    Page<Participant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct participant from Participant participant left join fetch participant.user")
    List<Participant> findAllWithToOneRelationships();

    @Query("select participant from Participant participant left join fetch participant.user where participant.id =:id")
    Optional<Participant> findOneWithToOneRelationships(@Param("id") Long id);
}
