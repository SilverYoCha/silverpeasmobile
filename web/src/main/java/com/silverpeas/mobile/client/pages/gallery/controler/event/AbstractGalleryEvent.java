package com.silverpeas.mobile.client.pages.gallery.controler.event;

import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractGalleryEvent extends GwtEvent<GalleryEventHandler> {

	public static Type<GalleryEventHandler> TYPE = new Type<GalleryEventHandler>();
	
	public AbstractGalleryEvent() {
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GalleryEventHandler> getAssociatedType() {
		return TYPE;
	}
}
