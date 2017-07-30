/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel;

/**
 *
 * @author Patrick Spiss
 *
 */
public interface WidgetSetting {

    public String getType();

    public String getId();

    public String getLabel();

    public String getGroup();

    public String getDescription();

    public String getDefault();

    public String getChoices();

}
