/*
 * Copyright (C) 2000 - 2018 Silverpeas
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
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.silverpeas.mobile.client.apps.status.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.silverpeas.mobile.client.apps.status.StatusApp;
import org.silverpeas.mobile.client.apps.status.events.StatusEvents;
import org.silverpeas.mobile.client.apps.status.resources.StatusMessages;
import org.silverpeas.mobile.client.common.EventBus;
import org.silverpeas.mobile.client.components.base.PageContent;
import org.silverpeas.mobile.client.components.base.events.apps.AppEvent;
import org.silverpeas.mobile.client.components.base.events.page.PageEvent;

public class StatusPage extends PageContent {

  private static StatusPageUiBinder uiBinder = GWT.create(StatusPageUiBinder.class);

  @UiField(provided = true) protected StatusMessages msg = null;
  @UiField protected HTMLPanel container;
  @UiField protected TextArea status;
  @UiField protected Anchor publish;

  interface StatusPageUiBinder extends UiBinder<Widget, StatusPage> {
  }

  public StatusPage() {
    msg = GWT.create(StatusMessages.class);
    setPageTitle(msg.title().asString());
    initWidget(uiBinder.createAndBindUi(this));
    container.getElement().setId("update-statut");
    status.getElement().setAttribute("x-webkit-speech", "x-webkit-speech");
    status.getElement().setAttribute("speech", "speech");
  }

  @UiHandler("publish")
  void publish(ClickEvent event) {
	// send event to apps
    EventBus.getInstance().fireEvent(new AppEvent(this, StatusEvents.POST.name(), status.getText()));
  }

  @Override
  public void receiveEvent(PageEvent event) {
    if (event.getSender() instanceof StatusApp && event.getName().equals(StatusEvents.POSTED.toString())) {
      back();
    }
  }
}