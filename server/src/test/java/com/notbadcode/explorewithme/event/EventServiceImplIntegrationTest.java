package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.EwmPostgresqlContainer;
import com.notbadcode.explorewithme.category.CategoryRepository;
import com.notbadcode.explorewithme.category.EventCategory;
import com.notbadcode.explorewithme.event.dto.*;
import com.notbadcode.explorewithme.locations.dto.LocationShortDto;
import com.notbadcode.explorewithme.stats.StatsClient;
import com.notbadcode.explorewithme.user.User;
import com.notbadcode.explorewithme.user.UserRepository;
import com.notbadcode.explorewithme.util.CommonDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;


@SpringBootTest
@Testcontainers
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public
class EventServiceImplIntegrationTest {
    private final EventService eventService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    @MockBean
    HttpServletRequest request;
    @MockBean
    StatsClient client;
    NewEventDto newEventDto;
    EventCategory category;
    User initiator;

    @Container
    public static PostgreSQLContainer<EwmPostgresqlContainer> container = EwmPostgresqlContainer.getInstance();

    @BeforeEach
    void setUp() {
        initiator = userRepository.save(User.builder()
                .name("Initiator")
                .email("initiator@email.com")
                .build());
        category = categoryRepository.save(EventCategory.builder()
                .id(1L)
                .name("Category")
                .build());
        newEventDto = NewEventDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10))
                .category(category.getId())
                .annotation("Annotation")
                .description("Description")
                .participantLimit(0)
                .location(LocationShortDto.builder()
                        .lat(51.1234)
                        .lon(15.5432)
                        .build())
                .title("title")
                .paid(Boolean.FALSE)
                .build();
        Mockito.doNothing().when(client).sendHit(anyString(), anyString());
    }

    @Test
    @DisplayName("Private: создание события инициатором")
    void whenCreateEvent_thenReturnEventFullDto() {
        // Act
        EventFullDto eventFullDto = eventService.createEvent(newEventDto, initiator.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getId()).isNotNull();
        assertThat(eventFullDto.getEventDate()).isEqualTo(newEventDto.getEventDate());
        assertThat(eventFullDto.getCategory().getName()).isEqualTo(category.getName());
        assertThat(eventFullDto.getAnnotation()).isEqualTo(newEventDto.getAnnotation());
        assertThat(eventFullDto.getDescription()).isEqualTo(newEventDto.getDescription());
        assertThat(eventFullDto.getAnnotation()).isEqualTo(newEventDto.getAnnotation());
        assertThat(eventFullDto.getRequestModeration()).isEqualTo(newEventDto.isRequestModeration());
        assertThat(eventFullDto.getParticipantLimit()).isEqualTo(newEventDto.getParticipantLimit());
        assertThat(eventFullDto.getInitiator().getName()).isEqualTo(initiator.getName());
        assertThat(eventFullDto.getState()).isEqualTo(EventState.PENDING);
        assertThat(eventFullDto.getLocation().getLat()).isEqualTo(newEventDto.getLocation().getLat());
        assertThat(eventFullDto.getLocation().getLon()).isEqualTo(newEventDto.getLocation().getLon());
        assertThat(eventFullDto.getTitle()).isEqualTo(newEventDto.getTitle());
        assertThat(eventFullDto.getPaid()).isEqualTo(newEventDto.getPaid());
    }

    @Test
    @DisplayName("Private: получения события по id")
    void whenGetEventById_thenReturnEventFullDto() {
        // Arrange
        EventFullDto eventDto = eventService.createEvent(newEventDto, initiator.getId());

        // Act
        Event event = eventService.getEventById(eventDto.getId());

        // Asserts
        assertThat(eventDto).isNotNull();
        assertThat(eventDto.getId()).isEqualTo(event.getId());
    }

    @Test
    @DisplayName("Private: получение списка событий по id пользователя")
    void whenFindEventsByUserId_thenReturnListOfEventShortDto() {
        // Arrange
        int eventsCount = 5;
        for (int i = 0; i < eventsCount; i++) {
            eventService.createEvent(newEventDto, initiator.getId());
        }

        // Act
        List<EventShortDto> events = eventService.findEventsByUserId(initiator.getId(), 0, 20);

        // Asserts
        assertThat(events).isNotNull();
        assertThat(events.size()).isEqualTo(eventsCount);
    }

    @Test
    @DisplayName("Private: обновление события инициатором")
    void updateEventByUser() {
        // Arrange
        EventFullDto eventFullDto = eventService.createEvent(newEventDto, initiator.getId());
        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .eventId(eventFullDto.getId())
                .title("new title")
                .build();

        // Act
        EventFullDto event = eventService.updateEventByUser(updateEventDto, initiator.getId());

        // Asserts
        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(eventFullDto.getId());
        assertThat(event.getTitle()).isEqualTo(updateEventDto.getTitle());
    }

    @Test
    @DisplayName("Admin: поиск событий")
    void findEventsByParams() {
        // Arrange
        List<Long> usersIds = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            usersIds.add(userRepository.save(User.builder()
                    .name("User #" + i)
                    .email("user-" + i + "@email.com")
                    .build()).getId());
        }
        EventCategory secondCategory = categoryRepository.save(EventCategory.builder()
                .name("Category #2")
                .build());
        List<Long> eventsIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            long userId = usersIds.get(0);
            if (i % 5 == 0) {
                userId = usersIds.get(3);
            } else if (i % 4 == 0) {
                userId = usersIds.get(2);
            } else if (i % 3 == 0) {
                userId = usersIds.get(1);
            }
            EventFullDto event = eventService.createEvent(NewEventDto.builder()
                    .eventDate(LocalDateTime.now().plusDays(3 * i).minusHours(2 * i))
                    .category((i % 2 == 0) ? category.getId() : secondCategory.getId())
                    .annotation("Annotation")
                    .description("Description")
                    .participantLimit(0)
                    .location(LocationShortDto.builder()
                            .lat(51.1234)
                            .lon(15.5432)
                            .build())
                    .title("title")
                    .paid(Boolean.FALSE)
                    .build(), userId);
            eventsIds.add(event.getId());
            if (i == 3) {
                eventService.publishEventById(eventsIds.get((int) i - 1));
            } else if (i == 8) {
                eventService.publishEventById(eventsIds.get((int) i - 1));
            } else if (i == 7) {
                eventService.cancelEventById(eventsIds.get((int) i - 1), userId);
            }
        }
        int from = 0;
        int size = 20;

        // Act
        List<EventFullDto> findEventsByUsersAndStatuses = eventService.findEventsByParams(
                Optional.of(usersIds.subList(0,2)),
                Optional.of(List.of("pubLiSHED", "cancelEd")),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                from, size
        );
        String start = CommonDateTime.ofLocalDateTime(eventService.getEventById(eventsIds.get(1)).getEventDate());
        String end = CommonDateTime.ofLocalDateTime(eventService.getEventById(eventsIds.get(7)).getEventDate());
        List<EventFullDto> findEventsByDateAndStatuses = eventService.findEventsByParams(
                Optional.empty(),
                Optional.of(List.of("pending")),
                Optional.empty(),
                Optional.of(start),
                Optional.of(end),
                from, size
        );
        start = CommonDateTime.ofLocalDateTime(eventService.getEventById(eventsIds.get(0)).getEventDate());
        List<EventFullDto> findEventsByUsersCategory = eventService.findEventsByParams(
                Optional.of(List.of(usersIds.get(2), usersIds.get(3))),
                Optional.empty(),
                Optional.of(List.of(category.getId())),
                Optional.of(start),
                Optional.empty(),
                from, size
        );

        // Asserts
        assertThat(findEventsByUsersAndStatuses.size()).isEqualTo(2);
        assertThat(findEventsByDateAndStatuses.size()).isEqualTo(4);
        assertThat(findEventsByUsersCategory.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Public: поиск событий")
    void findPublishedEventsByParams() {
        // Arrange
        EventCategory secondCategory = categoryRepository.save(EventCategory.builder()
                .name("Category #2")
                .build());
        List<Long> eventsIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            EventFullDto event = eventService.createEvent(NewEventDto.builder()
                    .eventDate(LocalDateTime.now().plusDays(3 * i).minusHours(2 * i))
                    .category((i % 2 == 0) ? category.getId() : secondCategory.getId())
                    .annotation((i % 3 == 0) ? "Annotation" : "Summarize")
                    .description((i % 4 == 0) ? "Description" : "Summarize")
                    .participantLimit((int)i)
                    .location(LocationShortDto.builder()
                            .lat(51.1234)
                            .lon(15.5432)
                            .build())
                    .title("title")
                    .paid((i == 3) ? Boolean.TRUE : Boolean.FALSE)
                    .build(), initiator.getId());
            eventsIds.add(event.getId());
            if (i == 3) {
                eventService.publishEventById(eventsIds.get((int) i - 1));
            } else if (i == 8) {
                eventService.publishEventById(eventsIds.get((int) i - 1));
            } else if (i == 7) {
                eventService.publishEventById(eventsIds.get((int) i - 1));
            }
        }
        int from = 0;
        int size = 20;

        // Act
        List<EventShortDto> findEventsByTextAndCategory = eventService.findEventsByParams(
                request,
                Optional.of("suMMa"),
                Optional.of(List.of(secondCategory.getId())),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                from, size
        );
        String start = CommonDateTime.ofLocalDateTime(eventService.getEventById(eventsIds.get(2)).getEventDate());
        String end = CommonDateTime.ofLocalDateTime(eventService.getEventById(eventsIds.get(7)).getEventDate());
        List<EventShortDto> findEventsByDateAndPaid = eventService.findEventsByParams(
                request,
                Optional.empty(),
                Optional.empty(),
                Optional.of(Boolean.TRUE),
                Optional.of(start),
                Optional.of(end),
                Optional.empty(),
                Optional.empty(),
                from, size
        );
        start = CommonDateTime.ofLocalDateTime(eventService.getEventById(eventsIds.get(0)).getEventDate());
        List<EventShortDto> findEventsDateSort = eventService.findEventsByParams(
                request,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(start),
                Optional.empty(),
                Optional.empty(),
                Optional.of("event_date"),
                from, size
        );

        // Asserts
        assertThat(findEventsByTextAndCategory.size()).isEqualTo(2);
        assertThat(findEventsByDateAndPaid.size()).isEqualTo(1);
        assertThat(findEventsDateSort.get(0).getId()).isEqualTo(eventService.getEventById(eventsIds.get(7)).getId());
    }

    @Test
    @DisplayName("Public: поиск событий по ids")
    void findEventsByIds() {
        // Arrange
        List<Long> eventsIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            EventFullDto eventDto = eventService.createEvent(NewEventDto.builder()
                    .eventDate(LocalDateTime.now().plusDays(3 * i))
                    .category(category.getId())
                    .annotation("Annotation")
                    .description("Description")
                    .participantLimit(0)
                    .location(LocationShortDto.builder()
                            .lat(51.1234)
                            .lon(15.5432)
                            .build())
                    .title("title")
                    .paid(Boolean.FALSE)
                    .build(), initiator.getId());
            eventsIds.add(eventDto.getId());
        }

        // Act
        List<Event> findEventsByIds = eventService.findEventsByIds(eventsIds.stream()
                .filter(e -> e % 2 == 0)
                .collect(Collectors.toList()));

        // Asserts
        assertThat(findEventsByIds.size()).isEqualTo(5);
    }
}