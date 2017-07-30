/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel;

import java.util.List;

/**
 *
 * @author Patrick Spiss
 *
 */
public interface Widget {

    public String getWidgetId();

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getAuthor();

    public void setAuthor(String author);

    public boolean getDontWrap();

    public void setDontWrap(boolean dontwrap);

    public void setTemplate(String template);

    public String getTemplate();

    public List<WidgetSetting> getSettings();

}
