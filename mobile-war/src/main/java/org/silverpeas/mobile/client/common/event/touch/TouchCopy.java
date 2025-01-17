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

package org.silverpeas.mobile.client.common.event.touch;

/**
 * @author: svu
 */
import com.google.gwt.dom.client.Touch;

public class TouchCopy {

  public static TouchCopy copy(Touch touch) {
    return new TouchCopy(touch.getPageX(), touch.getPageY(), touch.getIdentifier());
  }

  private final int x;
  private final int y;
  private final int id;

  public TouchCopy(int x, int y, int id) {
    this.x = x;
    this.y = y;
    this.id = id;
  }

  public int getPageX() {
    return x;
  }

  public int getPageY() {
    return y;
  }

  public int getIdentifier() {
    return id;
  }
}
