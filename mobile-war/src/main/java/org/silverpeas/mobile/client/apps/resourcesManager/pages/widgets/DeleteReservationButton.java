package org.silverpeas.mobile.client.apps.resourcesManager.pages.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.silverpeas.mobile.client.apps.resourcesManager.pages.ReservationPage;
import org.silverpeas.mobile.client.apps.resourcesManager.resources.ResourcesManagerMessages;
import org.silverpeas.mobile.client.components.Popin;
import org.silverpeas.mobile.client.components.PopinConfirmation;
import org.silverpeas.mobile.client.components.base.ActionItem;

/**
 * @author svu
 */
public class DeleteReservationButton extends ActionItem {
  interface DeleteReservationButtonUiBinder extends UiBinder<HTMLPanel, DeleteReservationButton> {
  }

  private static DeleteReservationButtonUiBinder
      uiBinder = GWT.create(DeleteReservationButtonUiBinder.class);

  @UiField
  HTMLPanel container;
  @UiField
  Anchor deleteReservation;

  @UiField(provided = true) protected ResourcesManagerMessages msg = null;
  private String instanceId, contentId, contentType, title;


  public DeleteReservationButton() {
    msg = GWT.create(ResourcesManagerMessages.class);
    initWidget(uiBinder.createAndBindUi(this));
    setId("deleteReservation");
  }

  @UiHandler("deleteReservation")
  void displayReservationPage(ClickEvent event){
    PopinConfirmation conf = new PopinConfirmation("Voulez-vous supprimer cette réservation ?");
    conf.setYesCallback(new Command() {
      @Override
      public void execute() {
        //TODO : fireevent to app to delete reservation

      }
    });
    conf.show();

    // hide menu
    getElement().getParentElement().removeAttribute("style");
  }
}
