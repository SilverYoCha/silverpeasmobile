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

package org.silverpeas.mobile.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.silverpeas.mobile.shared.dto.BaseDTO;
import org.silverpeas.mobile.shared.dto.notifications.NotificationBoxDTO;
import org.silverpeas.mobile.shared.dto.notifications.NotificationDTO;
import org.silverpeas.mobile.shared.dto.notifications.NotificationReceivedDTO;
import org.silverpeas.mobile.shared.dto.notifications.NotificationSendedDTO;
import org.silverpeas.mobile.shared.exceptions.AuthenticationException;
import org.silverpeas.mobile.shared.exceptions.NotificationsException;

import java.util.List;

@RemoteServiceRelativePath("Notifications")
public interface ServiceNotifications extends RemoteService {
  List<BaseDTO> getAllowedUsersAndGroups(String componentId, String contentId) throws NotificationsException, AuthenticationException;

  void markAsReaden(long id) throws NotificationsException, AuthenticationException;

  void markAsRead(List<NotificationBoxDTO> selection) throws NotificationsException, AuthenticationException;

  void delete(List<NotificationBoxDTO> selection) throws NotificationsException, AuthenticationException;

  void send(NotificationDTO notification, List<BaseDTO> receivers, String subject) throws NotificationsException, AuthenticationException;

  List<NotificationSendedDTO> getUserSendedNotifications() throws NotificationsException, AuthenticationException;

  List<NotificationReceivedDTO> getUserNotifications() throws NotificationsException, AuthenticationException;
}
