package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.ActivityTag;
import com.mycompany.myapp.domain.Tag;
import com.mycompany.myapp.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ActivityTag entity.
 */
@Repository
public interface ActivityTagRepository extends JpaRepository<ActivityTag, Long> {
    List<ActivityTag> findAllByActivity(Activity activity);
    Page<ActivityTag> findDistinctByTagInAndUserNot(Pageable pageable, List<Tag> tags, User user);

    @Query("select activityTag from ActivityTag activityTag where activityTag.user.login = ?#{principal.username}")
    List<ActivityTag> findByUserIsCurrentUser();

    default Optional<ActivityTag> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ActivityTag> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ActivityTag> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct activityTag from ActivityTag activityTag left join fetch activityTag.user",
        countQuery = "select count(distinct activityTag) from ActivityTag activityTag"
    )
    Page<ActivityTag> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct activityTag from ActivityTag activityTag left join fetch activityTag.user")
    List<ActivityTag> findAllWithToOneRelationships();

    @Query("select activityTag from ActivityTag activityTag left join fetch activityTag.user where activityTag.id =:id")
    Optional<ActivityTag> findOneWithToOneRelationships(@Param("id") Long id);
}
