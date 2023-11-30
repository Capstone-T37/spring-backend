package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.ActivityTag;
import com.mycompany.myapp.domain.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ActivityTag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityTagRepository extends JpaRepository<ActivityTag, Long> {
    List<ActivityTag> findAllByActivity(Activity activity);
}
