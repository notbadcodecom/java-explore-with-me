package com.notbadcode.explorewithme.participation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByEvent_Participants_Id(Long id);

    boolean existsByEvent_IdAndParticipant_Id(Long eventId, Long userId);

    List<Participation> findByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);

    List<Participation> findByEvent_IdAndStatus(Long id, ParticipationStatus status);
}
