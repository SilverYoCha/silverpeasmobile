/*
 * Copyright (C) 2000 - 2021 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.silverpeas.mobile.client.apps.agenda;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import org.fusesource.restygwt.client.Method;
import org.silverpeas.mobile.client.SpMobil;
import org.silverpeas.mobile.client.apps.agenda.events.TimeRange;
import org.silverpeas.mobile.client.apps.agenda.events.app.AbstractAgendaAppEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.AgendaAppEventHandler;
import org.silverpeas.mobile.client.apps.agenda.events.app.AttachmentsLoadEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.CalendarLoadEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.ParticipationEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.ReminderCreateEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.ReminderDeleteEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.ReminderUpdateEvent;
import org.silverpeas.mobile.client.apps.agenda.events.app.RemindersLoadEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.AbstractAgendaPagesEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.AgendaPagesEventHandler;
import org.silverpeas.mobile.client.apps.agenda.events.pages.AttachmentsLoadedEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.CalendarLoadedEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.ParticipationUpdatedEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.ReminderAddedEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.ReminderDeletedEvent;
import org.silverpeas.mobile.client.apps.agenda.events.pages.RemindersLoadedEvent;
import org.silverpeas.mobile.client.apps.agenda.pages.AgendaPage;
import org.silverpeas.mobile.client.apps.agenda.pages.EventPage;
import org.silverpeas.mobile.client.apps.agenda.resources.AgendaMessages;
import org.silverpeas.mobile.client.apps.navigation.events.app.external.AbstractNavigationEvent;
import org.silverpeas.mobile.client.apps.navigation.events.app.external.NavigationAppInstanceChangedEvent;
import org.silverpeas.mobile.client.apps.navigation.events.app.external.NavigationEventHandler;
import org.silverpeas.mobile.client.apps.navigation.events.app.external.NavigationShowContentEvent;
import org.silverpeas.mobile.client.common.EventBus;
import org.silverpeas.mobile.client.common.Notification;
import org.silverpeas.mobile.client.common.ServicesLocator;
import org.silverpeas.mobile.client.common.app.App;
import org.silverpeas.mobile.client.common.network.AsyncCallbackOnlineOnly;
import org.silverpeas.mobile.client.common.network.MethodCallbackOnlineOnly;
import org.silverpeas.mobile.client.common.network.MethodCallbackOnlineOrOffline;
import org.silverpeas.mobile.client.common.storage.LocalStorageHelper;
import org.silverpeas.mobile.shared.dto.ContentsTypes;
import org.silverpeas.mobile.shared.dto.almanach.CalendarDTO;
import org.silverpeas.mobile.shared.dto.almanach.CalendarEventAttendeeDTO;
import org.silverpeas.mobile.shared.dto.almanach.CalendarEventDTO;
import org.silverpeas.mobile.shared.dto.documents.DocumentType;
import org.silverpeas.mobile.shared.dto.documents.SimpleDocumentDTO;
import org.silverpeas.mobile.shared.dto.navigation.ApplicationInstanceDTO;
import org.silverpeas.mobile.shared.dto.navigation.Apps;
import org.silverpeas.mobile.shared.dto.reminder.ReminderDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgendaApp extends App implements AgendaAppEventHandler, NavigationEventHandler {

  class ShowContent implements AgendaPagesEventHandler {

    private String contributionId;

    public ShowContent(String contributionId) {
      EventBus.getInstance().addHandler(AbstractAgendaPagesEvent.TYPE, this);
      this.contributionId = contributionId;
    }

    @Override
    public void onCalendarEventsLoaded(final CalendarLoadedEvent event) {
      contributionId = contributionId.substring(0,contributionId.indexOf("@"));
      for (CalendarEventDTO ev : getAppEvents()) {
        if (ev.getEventId().equals(contributionId)) {
          CalendarDTO cal = null;
          for (CalendarDTO ca : calendars) {
            if (ca.getId().equals(ev.getCalendarId())) {
              cal = ca;
              break;
            }
          }
          EventPage page = new EventPage();
          page.setData(getApplicationInstance(), ev, cal);
          page.show();
          break;
        }
      }
    }
  }


  private AgendaMessages msg;
  private List<CalendarDTO> calendars = null;

  private List<CalendarEventDTO> appEvents = new ArrayList<>();

  public static final String EVENT_REMINDER_TYPE = "CalendarEvent";

  public AgendaApp(){
    super();
    msg = GWT.create(AgendaMessages.class);
    EventBus.getInstance().addHandler(AbstractAgendaAppEvent.TYPE, this);
    EventBus.getInstance().addHandler(AbstractNavigationEvent.TYPE, this);
  }

  public List<CalendarEventDTO> getAppEvents() {
    return appEvents;
  }

  public void start(){
    // always start
  }

  @Override
  public void stop() {
    // nevers stop
  }

  @Override
  public void appInstanceChanged(final NavigationAppInstanceChangedEvent event) {
    if (event.getInstance().getType().equals(Apps.almanach.name()) || event.getInstance().getType().equals(Apps.userCalendar.name())) {
      setApplicationInstance(event.getInstance());

      AgendaPage page = new AgendaPage();
      page.setPageTitle(event.getInstance().getLabel());

      MethodCallbackOnlineOrOffline action = new MethodCallbackOnlineOrOffline<List<CalendarDTO>>(null) {
        @Override
        public void attempt() {
          super.attempt();
          if (getApplicationInstance().getType().equals(Apps.userCalendar.name())) {
            ServicesLocator.getServiceUserCalendar().getCalendars(getApplicationInstance().getId(), this);
          } else {
            ServicesLocator.getServiceAlmanach().getCalendars(getApplicationInstance().getId(), this);
          }
        }

        @Override
        public void onSuccess(final Method method, final List<CalendarDTO> calendars) {
          super.onSuccess(method, calendars);
          AgendaApp.this.calendars = calendars;
          page.setApp(AgendaApp.this);
          page.setCalendars(calendars);
          page.show();
        }
      };
      action.attempt();
    }
  }

  @Override
  public void showContent(final NavigationShowContentEvent event) {
    if (event.getContent().getType().equals("Component") &&
        event.getContent().getInstanceId().startsWith(Apps.almanach.name())) {
      super.showContent(event);
    } else if (event.getContent().getType().equals(ContentsTypes.Event.name())) {
      final String contributionId = event.getContent().getContributionId();
      AsyncCallbackOnlineOnly action = new AsyncCallbackOnlineOnly<ApplicationInstanceDTO>() {

        @Override
        public void attempt() {
          ServicesLocator.getServiceNavigation()
              .getApp(null, contributionId, ContentsTypes.Event.name(), this);
        }

        @Override
        public void onSuccess(final ApplicationInstanceDTO app) {
          super.onSuccess(app);
          setApplicationInstance(app);
          NavigationAppInstanceChangedEvent e1 = new NavigationAppInstanceChangedEvent(getApplicationInstance());
          appInstanceChanged(e1);
          ShowContent sc = new ShowContent(getApplicationInstance().getExtraId());
        }
      };
      action.attempt();
    }
  }

  @Override
  public void loadCalendarEvents(final CalendarLoadEvent event) {
    getAppEvents().clear();

    MethodCallbackOnlineOrOffline action = new MethodCallbackOnlineOrOffline<List<CalendarEventDTO>>(null) {

      private int retainUntil = 1;
      private int callNumber = 0;
      private String currentAppId;
      private List<CalendarEventDTO> allEvents = new ArrayList<>();

      @Override
      public void attempt() {
        super.attempt();
        // Date format sample : 2019-03-24T23:00:00.000Z
        String startDateOfWindowTime;
        String endDateOfWindowTime;

        Date start = new Date();
        if (event.getRange().equals(TimeRange.weeks)) {
          DateTimeFormat formatter = DateTimeFormat.getFormat("EEE");
          String day = formatter.format(start);
          while (!day.equalsIgnoreCase("mon")) {
            CalendarUtil.addDaysToDate(start, -1);
            day = formatter.format(start);
          }
        } else if (event.getRange().equals(TimeRange.months)) {
          CalendarUtil.setToFirstDayOfMonth(start);
        }
        DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        startDateOfWindowTime = dtf.format(start);

        Date end = new Date();
        if (event.getRange().equals(TimeRange.weeks)) {
          CalendarUtil.addDaysToDate(end, 7*4+1); // for include last day
        } else if (event.getRange().equals(TimeRange.months)) {
          CalendarUtil.addMonthsToDate(end,12);
          CalendarUtil.addDaysToDate(end,1); // for include last day
        }
        endDateOfWindowTime = dtf.format(end);
        if (event.getCalendar() != null) {
          currentAppId = getCalendarInstanceId(event.getCalendar());
          if (getApplicationInstance().getType().equals(Apps.userCalendar.name())) {
            ServicesLocator.getServiceUserCalendar()
                .getOccurences(currentAppId, event.getCalendar().getId(), startDateOfWindowTime,
                    endDateOfWindowTime, SpMobil.getUser().getZone(), this);
          } else {
            ServicesLocator.getServiceAlmanach()
                .getOccurences(currentAppId, event.getCalendar().getId(), startDateOfWindowTime,
                    endDateOfWindowTime, SpMobil.getUser().getZone(), this);
          }
        } else {
          //request all calendars on instance
          retainUntil = calendars.size();
          for (CalendarDTO cal : calendars) {
            currentAppId = getCalendarInstanceId(cal);
            if (getApplicationInstance().getType().equals(Apps.userCalendar.name())) {
              ServicesLocator.getServiceUserCalendar()
                  .getOccurences(currentAppId, cal.getId(), startDateOfWindowTime,
                      endDateOfWindowTime, SpMobil.getUser().getZone(), this);
            } else {
              ServicesLocator.getServiceAlmanach()
                  .getOccurences(currentAppId, cal.getId(), startDateOfWindowTime,
                      endDateOfWindowTime, SpMobil.getUser().getZone(), this);
            }
          }
        }
      }

      @Override
      public void onSuccess(final Method method, final List<CalendarEventDTO> events) {
        super.onSuccess(method, events);

        allEvents.addAll(events);
        getAppEvents().addAll(events);
        callNumber++;
        if (callNumber == retainUntil) {
          EventBus.getInstance().fireEvent(new CalendarLoadedEvent(getApplicationInstance(), allEvents));
        }
      }
    };
    action.attempt();

  }

  private String getCalendarInstanceId(CalendarDTO cal) {
    String appId = cal.getUri();
    if (getApplicationInstance().getType().equals(Apps.userCalendar.name())) {
      appId = appId.replace("/silverpeas/services/usercalendar/", "");
      appId = appId.substring(0, appId.indexOf("/"));
    } else {
      appId = appId.replace("/silverpeas/services/almanach/", "");
      appId = appId.substring(0, appId.indexOf("/"));
    }
    return appId;
  }

  private String getCalendarInstanceId(String calendarId) {
    for (CalendarDTO cal : calendars) {
      if (cal.getId().equals(calendarId)) {
        return getCalendarInstanceId(cal);
      }
    }
    return null;
  }

  @Override
  public void loadReminders(final RemindersLoadEvent event) {
    String currentAppId = getCalendarInstanceId(event.getEvent().getCalendarId());

    MethodCallbackOnlineOrOffline action = new MethodCallbackOnlineOrOffline<List<ReminderDTO>>(null) {
      @Override
      public void attempt() {
        super.attempt();
        ServicesLocator.getServiceReminder()
            .getReminders(currentAppId, EVENT_REMINDER_TYPE, event.getEvent().getEventId(), this);
      }

      @Override
      public void onSuccess(final Method method, final List<ReminderDTO> reminders) {
        super.onSuccess(method, reminders);
        LocalStorageHelper.store("reminders" + event.getEvent().getEventId(), List.class, reminders);

        MethodCallbackOnlineOrOffline action2 = new MethodCallbackOnlineOrOffline<List<String>>(null) {
          @Override
          public void attempt() {
            super.attempt();
            ServicesLocator.getServiceReminder().getPossibleDurations(currentAppId,
                EVENT_REMINDER_TYPE, event.getEvent().getEventId(), "NEXT_START_DATE_TIME", this);
          }

          @Override
          public void onSuccess(final Method method, final List<String> durations) {
            super.onSuccess(method, durations);
            EventBus.getInstance().fireEvent(new RemindersLoadedEvent(reminders, durations));
          }
        };
        action2.attempt();
      }
    };
    action.attempt();
  }

  @Override
  public void updateReminder(final ReminderUpdateEvent event) {
    Notification.activityStart();
    String currentAppId = getCalendarInstanceId(event.getEvent().getCalendarId());
    MethodCallbackOnlineOnly action = new MethodCallbackOnlineOnly<ReminderDTO>() {
      @Override
      public void attempt() {
        super.attempt();
        ServicesLocator.getServiceReminder().updateReminder(currentAppId, EVENT_REMINDER_TYPE,
            event.getEvent().getEventId(), event.getReminder().getId(), event.getReminder(), this);
      }

      @Override
      public void onSuccess(final Method method, final ReminderDTO result) {
        super.onSuccess(method, result);
      }
    };
    action.attempt();
  }

  @Override
  public void createReminder(final ReminderCreateEvent event) {
    Notification.activityStart();
    String currentAppId = getCalendarInstanceId(event.getEvent().getCalendarId());
    MethodCallbackOnlineOnly action = new MethodCallbackOnlineOnly<ReminderDTO>() {
      @Override
      public void attempt() {
        super.attempt();
        ServicesLocator.getServiceReminder().createReminder(currentAppId, EVENT_REMINDER_TYPE,
            event.getEvent().getEventId(), event.getReminder(), this);
      }

      @Override
      public void onSuccess(final Method method, final ReminderDTO result) {
        super.onSuccess(method, result);
        EventBus.getInstance().fireEvent(new ReminderAddedEvent(result));
      }
    };
    action.attempt();
  }

  @Override
  public void deleteReminder(final ReminderDeleteEvent event) {
    Notification.activityStart();
    String currentAppId = getCalendarInstanceId(event.getEvent().getCalendarId());
    MethodCallbackOnlineOnly action = new MethodCallbackOnlineOnly<Void>() {
      @Override
      public void attempt() {
        super.attempt();
        ServicesLocator.getServiceReminder().deleteReminder(currentAppId, EVENT_REMINDER_TYPE,
            event.getEvent().getEventId(), event.getReminder().getId(), this);
      }

      @Override
      public void onSuccess(final Method method, final Void result) {
        super.onSuccess(method, result);
        EventBus.getInstance().fireEvent(new ReminderDeletedEvent());
      }
    };
    action.attempt();
  }

  @Override
  public void loadAttachments(final AttachmentsLoadEvent event) {
    String currentAppId = getCalendarInstanceId(event.getEvent().getCalendarId());

    MethodCallbackOnlineOrOffline action = new MethodCallbackOnlineOrOffline<List<SimpleDocumentDTO>>(null) {
      @Override
      public void attempt() {
        super.attempt();
        ServicesLocator.getRestServiceDocuments().getDocumentsByType(currentAppId, event.getEvent().getEventId(),
            DocumentType.attachment.name(), SpMobil.getUser().getLanguage(), this);
      }

      @Override
      public void onSuccess(final Method method, final List<SimpleDocumentDTO> attachments) {
        super.onSuccess(method, attachments);
        EventBus.getInstance().fireEvent(new AttachmentsLoadedEvent(attachments));
      }
    };
    action.attempt();
  }

  @Override
  public void participation(final ParticipationEvent event) {

    String currentAppId = getCalendarInstanceId(event.getEvent().getCalendarId());
    MethodCallbackOnlineOnly action = new MethodCallbackOnlineOnly<CalendarEventDTO>() {
      @Override
      public void onSuccess(final Method method, final CalendarEventDTO dto) {
        super.onSuccess(method, dto);
        EventBus.getInstance().fireEvent(new ParticipationUpdatedEvent(event.getStatus()));
      }

      @Override
      public void attempt() {
        super.attempt();
        CalendarEventAttendeeDTO attendee = getUserAttendee(event.getEvent().getAttendees());
        attendee.setParticipationStatus(event.getStatus());
        ServicesLocator.getServiceAlmanach().updateParticipation(currentAppId, event.getEvent().getCalendarId(), event.getEvent().getEventId(), event.getEvent().getOccurrenceId(), attendee.getId(), SpMobil.getUser().getZone(), attendee, this);
      }
    };
    action.attempt();
  }

  private CalendarEventAttendeeDTO getUserAttendee(final List<CalendarEventAttendeeDTO> attendees) {
    CalendarEventAttendeeDTO attendee = null;
    for (CalendarEventAttendeeDTO attendeeDTO : attendees) {
      if (attendeeDTO.getId().equals(SpMobil.getUser().getId())) {
        attendee = attendeeDTO;
        break;
      }
    }
    return attendee;
  }
}

