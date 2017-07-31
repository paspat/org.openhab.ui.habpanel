/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel;

import java.io.InputStream;
import java.util.List;

/**
 *
 * @author Patrick Spiss
 *
 */
public interface WidgetPackage {

    String getPackageId();

    String getName();

    InputStream getStaticResource(String path);

    List<Widget> getWidgets();

    List<String> getCSSResources();

    List<String> getScriptResources();

}
