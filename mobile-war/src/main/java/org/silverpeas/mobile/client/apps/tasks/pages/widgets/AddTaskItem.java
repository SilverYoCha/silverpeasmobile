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

package org.silverpeas.mobile.client.apps.tasks.pages.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.silverpeas.mobile.client.apps.tasks.pages.TaskPage;
import org.silverpeas.mobile.client.apps.tasks.resources.TasksMessages;

/**
 * @author: svu
 */
public class AddTaskItem  extends Composite {

  interface AddTaskItemUiBinder extends UiBinder<Widget, AddTaskItem> {
  }

  @UiField Anchor link;
  @UiField(provided = true) protected TasksMessages msg = null;

  private static AddTaskItemUiBinder uiBinder = GWT.create(AddTaskItemUiBinder.class);

  public AddTaskItem() {
    msg = GWT.create(TasksMessages.class);
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("link")
  void createTask(ClickEvent event) {
    TaskPage page = new TaskPage();
    page.setPageTitle(msg.create());
    page.show();
  }
}