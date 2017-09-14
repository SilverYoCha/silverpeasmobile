/*
 * Copyright (C) 2000 - 2017 Silverpeas
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
 *
 */

package com.silverpeas.mobile.client.apps.workflow.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.silverpeas.mobile.client.apps.favorites.pages.widgets.AddToFavoritesButton;
import com.silverpeas.mobile.client.apps.workflow.events.app.WorkflowLoadInstancesEvent;
import com.silverpeas.mobile.client.apps.workflow.events.pages.AbstractWorkflowPagesEvent;
import com.silverpeas.mobile.client.apps.workflow.events.pages.WorkflowPagesEventHandler;
import com.silverpeas.mobile.client.apps.workflow.events.pages.WorkflowLoadedInstancesEvent;
import com.silverpeas.mobile.client.common.EventBus;
import com.silverpeas.mobile.client.components.base.ActionsMenu;
import com.silverpeas.mobile.client.components.base.PageContent;
import com.silverpeas.mobile.shared.dto.workflow.WorkflowInstanceDTO;
import com.silverpeas.mobile.shared.dto.workflow.WorkflowInstancesDTO;
import org.apache.poi.hssf.record.WindowOneRecord;

import java.util.Map;

public class WorkflowPage extends PageContent implements WorkflowPagesEventHandler, ChangeHandler {

  private static WorkflowPageUiBinder uiBinder = GWT.create(WorkflowPageUiBinder.class);

  @UiField FlexTable instances;
  @UiField ActionsMenu actionsMenu;
  @UiField ListBox roles;

  private AddToFavoritesButton favorite = new AddToFavoritesButton();
  private WorkflowInstancesDTO data = null;

  private ClickHandler clickOnInstance = new ClickHandler() {
    @Override
    public void onClick(final ClickEvent event) {
      Anchor link = (Anchor) event.getSource();
      String id = link.getElement().getAttribute("data");
      //TODO : display presentation form
    }
  };

  @Override
  public void onChange(final ChangeEvent changeEvent) {
    WorkflowLoadInstancesEvent event = new WorkflowLoadInstancesEvent();
    event.setRole(roles.getSelectedValue());
    EventBus.getInstance().fireEvent(event);
  }

  @Override
  public void loadInstances(final WorkflowLoadedInstancesEvent event) {
    if (data == null) {
      this.data = event.getData();
      for (Map.Entry<String,String> role : data.getRoles().entrySet()) {
        roles.addItem(role.getValue(), role.getKey());
      }
    } else {
      instances.clear();
      this.data = event.getData();
    }

    int c = 0;
    for (String label : data.getHeaderLabels()) {
      instances.setHTML(0, c, label);
      c++;
    }

    int r = 1;
    for (WorkflowInstanceDTO d : data.getInstances()) {
      c = 0;
      for (String value : d.getHeaderFieldsValues()) {
        Anchor link = new Anchor();
        link.setHref("javaScript:;");
        link.setText(value);
        link.setStylePrimaryName("workflow-anchor");
        link.getElement().setId("inst" + r + c);
        link.getElement().setAttribute("data", d.getId());
        link.addClickHandler(clickOnInstance);
        instances.setWidget(r,c,link);
        c++;
      }
      r++;
    }
  }

  interface WorkflowPageUiBinder extends UiBinder<Widget, WorkflowPage> {
  }

  public WorkflowPage() {
    initWidget(uiBinder.createAndBindUi(this));
    EventBus.getInstance().addHandler(AbstractWorkflowPagesEvent.TYPE, this);
    roles.addChangeHandler(this);
  }

  @Override
  public void stop() {
    super.stop();
    EventBus.getInstance().removeHandler(AbstractWorkflowPagesEvent.TYPE, this);
  }


}