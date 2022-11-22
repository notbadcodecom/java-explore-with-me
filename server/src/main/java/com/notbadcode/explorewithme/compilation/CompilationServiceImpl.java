package com.notbadcode.explorewithme.compilation;

import com.notbadcode.explorewithme.compilation.dto.CompilationDto;
import com.notbadcode.explorewithme.compilation.dto.NewCompilationDto;
import com.notbadcode.explorewithme.error.BadRequestException;
import com.notbadcode.explorewithme.error.ForbiddenException;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.event.Event;
import com.notbadcode.explorewithme.event.EventService;
import com.notbadcode.explorewithme.util.SizeRequest;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    public Compilation getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        log.debug("Load compilation id={}", compilation.getId());
        return compilation;
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        List<Event> events = eventService.findEventsByIds(compilationDto.getEvents());
        Compilation compilation = compilationRepository
                .save(CompilationMapper.toCompilation(compilationDto, events));
        log.debug("Compilation id={} has been created", compilation.getId());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        compilationRepository.delete(compilation);
        log.debug("Compilation id={} has been deleted", compId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = eventService.getEventById(eventId);
        List<Event> events = compilation.getEvents();
        if (!events.contains(event)) {
            throw new BadRequestException("Event not in compilation");
        }
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.debug("Event id={} has been deleted in compilation id={}", compId, eventId);
    }

    @Override
    @Transactional
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = eventService.getEventById(eventId);
        List<Event> events = compilation.getEvents();
        if (events.contains(event)) {
            throw new BadRequestException("Event already in compilation");
        }
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.debug("Event id={} has been added in compilation id={}", compId, eventId);
    }

    @Override
    @Transactional
    public void deletePinOfCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        if (!compilation.getPinned()) {
            throw new BadRequestException("Compilation already unpinned");
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.debug("Set pinned to compilation id={}", compId);
    }

    @Override
    @Transactional
    public void setPinOfCompilation(Long compId) {
        Compilation compilation = getCompilationById(compId);
        if (compilation.getPinned()) {
            throw new ForbiddenException("Compilation already pinned");
        }
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.debug("Unpinned compilation id={}", compId);
    }

    @Override
    public List<CompilationDto> findCompilationByParams(
            Optional<Boolean> pinned,
            int from,
            int size
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        pinned.ifPresent(p -> builder.and(QCompilation.compilation.pinned.eq(p)));
        log.debug("Compilations found by params");
        return CompilationMapper
                .toCompilationDto(compilationRepository.findAll(builder, SizeRequest.of(from, size)));
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation compilation = getCompilationById(compId);
        log.debug("Found compilation id={}", compilation.getId());
        return CompilationMapper.toCompilationDto(compilation);
    }
}
