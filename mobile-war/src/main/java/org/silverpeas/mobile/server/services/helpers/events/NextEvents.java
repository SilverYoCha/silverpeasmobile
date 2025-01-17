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

package org.silverpeas.mobile.server.services.helpers.events;

import org.silverpeas.core.web.look.Shortcut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicolas on 19/07/2017.
 */
public class NextEvents extends ListOfContributions {

  private List<NextEventsDate> nextEventsDates;

  public NextEvents(final List<NextEventsDate> nextEventsDates) {
    this.nextEventsDates = nextEventsDates;
    processShortcuts();
  }

  public List<NextEventsDate> getNextEventsDates() {
    return nextEventsDates;
  }

  private void processShortcuts() {
    Map<String, Shortcut> map = new HashMap<>();
    for (NextEventsDate date : nextEventsDates) {
      for (Event event : date.getEvents()) {
        String componentId = event.getDetail().getInstanceId();
        Shortcut appShortcut = map.computeIfAbsent(componentId, this::getAppShortcut);
        event.setAppShortcut(appShortcut);
      }
    }
    setAppShortcuts(new ArrayList<>(map.values()));
  }

}
