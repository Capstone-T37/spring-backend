package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Conversation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ConversationRepositoryWithBagRelationshipsImpl implements ConversationRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Conversation> fetchBagRelationships(Optional<Conversation> conversation) {
        return conversation.map(this::fetchUsers);
    }

    @Override
    public Page<Conversation> fetchBagRelationships(Page<Conversation> conversations) {
        return new PageImpl<>(
            fetchBagRelationships(conversations.getContent()),
            conversations.getPageable(),
            conversations.getTotalElements()
        );
    }

    @Override
    public List<Conversation> fetchBagRelationships(List<Conversation> conversations) {
        return Optional.of(conversations).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Conversation fetchUsers(Conversation result) {
        return entityManager
            .createQuery(
                "select conversation from Conversation conversation left join fetch conversation.users where conversation is :conversation",
                Conversation.class
            )
            .setParameter("conversation", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Conversation> fetchUsers(List<Conversation> conversations) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, conversations.size()).forEach(index -> order.put(conversations.get(index).getId(), index));
        List<Conversation> result = entityManager
            .createQuery(
                "select distinct conversation from Conversation conversation left join fetch conversation.users where conversation in :conversations",
                Conversation.class
            )
            .setParameter("conversations", conversations)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
