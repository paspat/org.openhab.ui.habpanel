/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel.internal.widgets.pkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.osgi.framework.BundleContext;

/**
 *
 * @author Patrick Spiss
 *
 */
public class DirectoryWidgetPackage extends AbstractWidgetPackage {

    @Override
    public boolean load(BundleContext context, File path) {
        if (path.isDirectory()) {
            return super.load(context, path);
        }

        return false;
    }

    @Override
    public InputStream getResource(String path) {
        File f = new File(this.path, path);
        if (f.exists() && f.isFile()) {
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException e) {
            }
        }

        return null;
    }

}
