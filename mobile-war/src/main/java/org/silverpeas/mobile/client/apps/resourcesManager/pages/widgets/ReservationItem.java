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

package org.silverpeas.mobile.client.apps.resourcesManager.pages.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.silverpeas.mobile.client.apps.resourcesManager.pages.ReservationDetailPage;
import org.silverpeas.mobile.client.resources.ApplicationMessages;
import org.silverpeas.mobile.shared.dto.reservations.ReservationDTO;
import org.silverpeas.mobile.shared.dto.reservations.ReservationStatus;
import org.silverpeas.mobile.shared.dto.reservations.ResourceDTO;

public class ReservationItem extends Composite implements ClickHandler {

  private ReservationDTO data;
  private static FavoriteItemUiBinder uiBinder = GWT.create(FavoriteItemUiBinder.class);

  @UiField
  HTML content;
  @UiField
  HTMLPanel container;
  protected ApplicationMessages msg = null;

  interface FavoriteItemUiBinder extends UiBinder<Widget, ReservationItem> {}

  public ReservationItem() {
    initWidget(uiBinder.createAndBindUi(this));
    msg = GWT.create(ApplicationMessages.class);
  }

  public ReservationDTO getData() {
    return data;
  }

  public void setData(ReservationDTO data) {
    this.data = data;
    String html = "";
    container.setStylePrimaryName("reservation");
    html += "<div class='reservationDate'>" + data.getStartDate() + "&nbsp;&rarr;&nbsp;" + data.getEndDate() + "</div>";
    html += "<div class='reservationEvent'>" + data.getEvenement() + "</div>";
    html += "<div><ul class='reservationRessources'>";
    for (ResourceDTO res : data.getResources()) {
      String status = res.getReservationStatus();
      if (status.equals(ReservationStatus.A.toString())) status = "waitingForValidation";
      if (status.equals(ReservationStatus.V.toString())) status = "validated";
      html += "<li class='"+status+"'>" + res.getName() + "</li>";
    }
    html += "</div></ul>";
    content.addClickHandler(this);

    content.setStylePrimaryName("reservation");
    content.setHTML(html);

  }

  @Override
  public void onClick(final ClickEvent clickEvent) {
    ReservationDetailPage page = new ReservationDetailPage();
    page.setPageTitle("todo");
    page.setData(data);
    page.show();
  }
}