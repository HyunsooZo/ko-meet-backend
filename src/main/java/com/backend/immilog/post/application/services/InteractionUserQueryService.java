package com.backend.immilog.post.application.services;

import com.backend.immilog.post.domain.model.interaction.InteractionType;
import com.backend.immilog.post.domain.model.interaction.InteractionUser;
import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.repositories.InteractionUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InteractionUserQueryService {
    private final InteractionUserRepository interactionUserRepository;

    public InteractionUserQueryService(InteractionUserRepository interactionUserRepository) {
        this.interactionUserRepository = interactionUserRepository;
    }

    @Transactional(readOnly = true)
    public Optional<InteractionUser> getInteraction(
            Long postSeq,
            Long userSeq,
            PostType postType,
            InteractionType interactionType
    ) {
        return interactionUserRepository.getByPostSeqAndUserSeqAndPostTypeAndInteractionType(
                postSeq,
                userSeq,
                postType,
                interactionType
        );
    }

    @Transactional(readOnly = true)
    public List<InteractionUser> getInteractionUsersByPostSeqList(
            List<Long> resultSeqList,
            PostType postType
    ) {
        return interactionUserRepository.getInteractionsByPostSeqList(
                resultSeqList,
                postType
        );
    }

    public List<InteractionUser> getBookmarkInteractions(
            Long userSeq,
            PostType postType
    ) {
        return interactionUserRepository.getInteractions(userSeq, postType, InteractionType.BOOKMARK);
    }
}
