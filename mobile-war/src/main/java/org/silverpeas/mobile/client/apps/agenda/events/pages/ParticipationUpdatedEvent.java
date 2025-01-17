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

package org.silverpeas.mobile.client.apps.agenda.events.pages;

import org.silverpeas.mobile.shared.dto.almanach.ParticipationStatusDTO;
import org.silverpeas.mobile.shared.dto.reminder.ReminderDTO;

import java.util.List;

public class ParticipationUpdatedEvent extends AbstractEventPagesEvent {

  private ParticipationStatusDTO status;

  public ParticipationUpdatedEvent(ParticipationStatusDTO status){
    super();
    this.status = status;
  }

  @Override
  protected void dispatch(EventPagesEventHandler handler) {
    handler.onParticipationUpdated(this);
  }

  public ParticipationStatusDTO getStatus() {
    return status;
  }
}
