package com.notbadcode.explorewithme.event;

import com.notbadcode.explorewithme.category.CategoryService;
import com.notbadcode.explorewithme.category.EventCategory;
import com.notbadcode.explorewithme.error.BadRequestException;
import com.notbadcode.explorewithme.error.ForbiddenException;
import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.event.dto.AdminUpdateEventDto;
import com.notbadcode.explorewithme.event.dto.EventFullDto;
import com.notbadcode.explorewithme.event.dto.UpdateEventDto;
import com.notbadcode.explorewithme.locations.LocationService;
import com.notbadcode.explorewithme.stats.StatsClient;
import com.notbadcode.explorewithme.user.User;
import com.notbadcode.explorewithme.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EventServiceImplUnitTest {
    @Mock private EventRepository eventRepository;
    @Mock private UserService userService;
    @Mock private CategoryService categoryService;
    @Mock private StatsClient client;
    @Mock private LocationService locationService;
    private EventService eventService;
    @Value("${ewm-config.event.service.hoursBeforePublication}") int minHoursBeforePublication;
    Event event;
    User eventInitiator;
    User notEventInitiator;
    HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        EventCategory category = new EventCategory();
        category.setName("Category");
        eventInitiator = User.builder()
                .id(1L)
                .name("Initiator")
                .email("initiator@email.com")
                .build();
        event = Event.builder()
                .id(1L)
                .eventDate(LocalDateTime.now().plusHours(10))
                .category(category)
                .annotation("Annotation")
                .description("Description")
                .moderation(Boolean.FALSE)
                .participantLimit(0)
                .initiator(eventInitiator)
                .state(EventState.PENDING)
                .lat(51.1234)
                .lon(15.5432)
                .title("title")
                .createdOn(LocalDateTime.now().minusDays(1))
                .paid(Boolean.FALSE)
                .build();
        notEventInitiator = User.builder()
                .id(2L)
                .name("Not initiator")
                .email("not-initiator@email.com")
                .build();
        eventService = new EventServiceImpl(
                eventRepository, userService, categoryService, locationService, client, minHoursBeforePublication
        );
        Mockito.doReturn(Optional.of(event)).when(eventRepository).findById(1L);
    }

    @Test
    @DisplayName("Private: получение события инициатором")
    void whenFindEventByIdIfCorrectInitiator_thenReturnEventDto() {
        // Arrange
        Mockito.when(userService.getUserById(eventInitiator.getId())).thenReturn(eventInitiator);

        // Act
        EventFullDto eventFullDto = eventService.findEventByInitiatorById(event.getId(), eventInitiator.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getId()).isEqualTo(event.getId());
        assertThat(eventFullDto.getEventDate()).isEqualTo(event.getEventDate());
        assertThat(eventFullDto.getCategory().getName()).isEqualTo(event.getCategory().getName());
        assertThat(eventFullDto.getAnnotation()).isEqualTo(event.getAnnotation());
        assertThat(eventFullDto.getDescription()).isEqualTo(event.getDescription());
        assertThat(eventFullDto.getAnnotation()).isEqualTo(event.getAnnotation());
        assertThat(eventFullDto.getRequestModeration()).isEqualTo(event.getModeration());
        assertThat(eventFullDto.getParticipantLimit()).isEqualTo(event.getParticipantLimit());
        assertThat(eventFullDto.getInitiator().getName()).isEqualTo(event.getInitiator().getName());
        assertThat(eventFullDto.getState()).isEqualTo(event.getState());
        assertThat(eventFullDto.getLocation().getLat()).isEqualTo(event.getLat());
        assertThat(eventFullDto.getLocation().getLon()).isEqualTo(event.getLon());
        assertThat(eventFullDto.getTitle()).isEqualTo(event.getTitle());
        assertThat(eventFullDto.getPaid()).isEqualTo(event.getPaid());
        assertThat(eventFullDto.getCreatedOn()).isEqualTo(event.getCreatedOn());
    }

    @Test
    @DisplayName("Private: получение события не инициатором")
    void whenFindEventByIdIfWrongInitiator_thenThrowForbiddenException() {
        // Arrange
        Mockito.when(userService.getUserById(notEventInitiator.getId())).thenReturn(notEventInitiator);

        // Act
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> eventService.findEventByInitiatorById(event.getId(), notEventInitiator.getId()),
                "Requesting another user's event"
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("Requesting another user's event");
    }

    @Test
    @DisplayName("Public: получение неопубликованного события")
    void whenFindEventById_thenThrowNotFoundException() {
        // Act
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> eventService.findEventById(request, event.getId()),
                "Event with id=" + event.getId() + " was not published"
        );

        // Asserts
        assertThat(exception.getMessage()).isEqualTo("Event with id=" + event.getId() + " was not published");
    }

    @Test
    @DisplayName("Public: получение опубликованного события")
    void whenFindEventById_thenReturnEventDto() {
        //Arrange
        event.setState(EventState.PUBLISHED);

        // Act
        EventFullDto eventFullDto = eventService.findEventById(request, event.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getId()).isEqualTo(event.getId());
        assertThat(eventFullDto.getEventDate()).isEqualTo(event.getEventDate());
        assertThat(eventFullDto.getCategory().getName()).isEqualTo(event.getCategory().getName());
        assertThat(eventFullDto.getAnnotation()).isEqualTo(event.getAnnotation());
        assertThat(eventFullDto.getDescription()).isEqualTo(event.getDescription());
        assertThat(eventFullDto.getAnnotation()).isEqualTo(event.getAnnotation());
        assertThat(eventFullDto.getRequestModeration()).isEqualTo(event.getModeration());
        assertThat(eventFullDto.getParticipantLimit()).isEqualTo(event.getParticipantLimit());
        assertThat(eventFullDto.getInitiator().getName()).isEqualTo(event.getInitiator().getName());
        assertThat(eventFullDto.getState()).isEqualTo(event.getState());
        assertThat(eventFullDto.getLocation().getLat()).isEqualTo(event.getLat());
        assertThat(eventFullDto.getLocation().getLon()).isEqualTo(event.getLon());
        assertThat(eventFullDto.getTitle()).isEqualTo(event.getTitle());
        assertThat(eventFullDto.getPaid()).isEqualTo(event.getPaid());
        assertThat(eventFullDto.getCreatedOn()).isEqualTo(event.getCreatedOn());
    }

    @Test
    @DisplayName("Private: обновление опубликованного события")
    void whenUpdatePublishedEventByUser_thenThrowBadRequestException() {
        //Arrange
        Mockito.when(userService.getUserById(eventInitiator.getId())).thenReturn(eventInitiator);
        event.setState(EventState.PUBLISHED);
        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .eventId(event.getId())
                .title("new title")
                .paid(Boolean.TRUE)
                .build();

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.updateEventByUser(updateEventDto, eventInitiator.getId()),
                "Event id={" + event.getId() + "} is not pending moderation"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Event id={" + event.getId() + "} is not pending moderation");
    }

    @Test
    @DisplayName("Private: обновление события другого пользователя")
    void whenUpdateEventByOtherUser_thenThrowForbiddenException() {
        //Arrange
        Mockito.when(userService.getUserById(notEventInitiator.getId())).thenReturn(notEventInitiator);
        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .eventId(event.getId())
                .title("new title")
                .paid(Boolean.TRUE)
                .build();

        // Act
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> eventService.updateEventByUser(updateEventDto, notEventInitiator.getId()),
                "Updating another user's event"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Updating another user's event");
    }

    @Test
    @DisplayName("Private: обновление события")
    void whenUpdatePublishedEventByUser_thenReturnNewEventDto() {
        //Arrange
        Mockito.when(userService.getUserById(eventInitiator.getId())).thenReturn(eventInitiator);
        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .eventId(event.getId())
                .title("new title")
                .paid(Boolean.TRUE)
                .build();

        // Act
        EventFullDto eventFullDto = eventService.updateEventByUser(updateEventDto, eventInitiator.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getId()).isEqualTo(event.getId());
        assertThat(eventFullDto.getTitle()).isEqualTo(event.getTitle());
        assertThat(eventFullDto.getPaid()).isEqualTo(event.getPaid());
    }

    @Test
    @DisplayName("Admin: обновление события")
    void whenUpdateEventByAdmin_thenReturnNewEventDto() {
        //Arrange
        event.setState(EventState.PUBLISHED);
        AdminUpdateEventDto updateEventDto = AdminUpdateEventDto.builder()
                .title("new title")
                .paid(Boolean.TRUE)
                .build();

        // Act
        EventFullDto eventFullDto = eventService.updateEventByAdmin(event.getId(), updateEventDto);

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getId()).isEqualTo(event.getId());
        assertThat(eventFullDto.getTitle()).isEqualTo(event.getTitle());
        assertThat(eventFullDto.getPaid()).isEqualTo(event.getPaid());
        assertThat(eventFullDto.getState()).isEqualTo(EventState.PUBLISHED);
    }

    @Test
    @DisplayName("Private: отмена события")
    void whenCancelEventById_thenReturnNewEventDto() {
        //Arrange
        Mockito.when(userService.getUserById(eventInitiator.getId())).thenReturn(eventInitiator);
        Mockito.when(eventRepository.save(any(Event.class))).then(invocation -> invocation.getArgument(0));

        // Act
        EventFullDto eventFullDto = eventService.cancelEventById(event.getId(), eventInitiator.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getState()).isEqualTo(EventState.CANCELED);
    }

    @Test
    @DisplayName("Private: отмена события другого пользователя")
    void whenCancelEventOfOtherInitiatorById_thenReturnNewEventDto() {
        //Arrange
        Mockito.when(userService.getUserById(notEventInitiator.getId())).thenReturn(notEventInitiator);

        // Act
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> eventService.cancelEventById(event.getId(), notEventInitiator.getId()),
                "Canceling another user's event"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Canceling another user's event");
    }

    @Test
    @DisplayName("Private: отмена события не ожидающего модерации")
    void whenCancelEventNotInPendingStatus_thenThrowBadRequestException() {
        //Arrange
        Mockito.when(userService.getUserById(eventInitiator.getId())).thenReturn(eventInitiator);
        event.setState(EventState.CANCELED);

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.cancelEventById(event.getId(), eventInitiator.getId()),
                "Event id={" + event.getId() + "} is not pending moderation"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Event id={" + event.getId() + "} is not pending moderation");
    }

    @Test
    @DisplayName("Private: публикация события")
    void whenPublishEventById_thenReturnNewEventDto() {
        //Arrange
        event.setEventDate(LocalDateTime.now().plusDays(30));
        Mockito.when(eventRepository.save(any(Event.class))).then(invocation -> invocation.getArgument(0));

        // Act
        EventFullDto eventFullDto = eventService.publishEventById(event.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getState()).isEqualTo(EventState.PUBLISHED);
    }

    @Test
    @DisplayName("Private: публикация события перед его началом")
    void whenPublishEventLessThanEstablishedTime_thenThrowBadRequestException() {
        //Arrange
        event.setEventDate(LocalDateTime.now().plusHours(minHoursBeforePublication).minusMinutes(10));

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.publishEventById(event.getId()),
                "Event datetime does not meet the conditions for publication"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Event datetime does not meet the conditions for publication");
    }

    @Test
    @DisplayName("Private: публикация события не ожидающего модерации")
    void whenPublishEventNotInPendingStatus_thenThrowBadRequestException() {
        //Arrange
        event.setState(EventState.REJECTED);

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.publishEventById(event.getId()),
                "Event id={" + event.getId() + "} is not pending moderation"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Event id={" + event.getId() + "} is not pending moderation");
    }

    @Test
    @DisplayName("Private: отклонение события")
    void whenRejectEventById_thenReturnNewEventDto() {
        //Arrange
        Mockito.when(eventRepository.save(any(Event.class))).then(invocation -> invocation.getArgument(0));
        event.setEventDate(LocalDateTime.now().plusHours(minHoursBeforePublication * 2L));

        // Act
        EventFullDto eventFullDto = eventService.rejectEventById(event.getId());

        // Asserts
        assertThat(eventFullDto).isNotNull();
        assertThat(eventFullDto.getState()).isEqualTo(EventState.CANCELED);
    }

    @Test
    @DisplayName("Private: отклонение опубликованного события")
    void whenRejectPublishedEvent_thenThrowBadRequestException() {
        //Arrange
        event.setState(EventState.PUBLISHED);

        // Act
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.rejectEventById(event.getId()),
                "Event id={" + event.getId() + "} already published"
        );

        // Asserts
        assertThat(exception.getMessage())
                .isEqualTo("Event id={" + event.getId() + "} already published");
    }
}
