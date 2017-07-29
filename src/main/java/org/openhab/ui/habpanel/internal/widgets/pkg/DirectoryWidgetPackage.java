package org.openhab.ui.habpanel.internal.widgets.pkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.osgi.framework.BundleContext;

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
