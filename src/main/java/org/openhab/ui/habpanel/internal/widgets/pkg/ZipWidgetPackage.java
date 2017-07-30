/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel.internal.widgets.pkg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Patrick Spiss
 *
 */
public class ZipWidgetPackage extends AbstractWidgetPackage {

    private final static Logger logger = LoggerFactory.getLogger(ZipWidgetPackage.class);

    @Override
    public boolean load(BundleContext context, File path) {
        if (path.isFile() && (path.getName().endsWith(".jar") || path.getName().endsWith(".zip"))) {
            return super.load(context, path);
        }
        return false;
    }

    @Override
    public InputStream getResource(String path) {
        logger.debug("Get resource '{}'", path);
        ZipFile zip = null;

        try {
            zip = new ZipFile(this.path);
            ZipEntry entry = zip.getEntry(path);
            if (entry != null) {
                return new ZipWidgetInputStream(zip, zip.getInputStream(entry));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

/**
 *
 * @author Patrick Spiss
 *
 */
class ZipWidgetInputStream extends BufferedInputStream {

    private ZipFile zip;

    public ZipWidgetInputStream(ZipFile zip, InputStream in) {
        super(in);
        this.zip = zip;
    }

    @Override
    public void close() throws IOException {
        super.close();

        this.zip.close();
    }

}
