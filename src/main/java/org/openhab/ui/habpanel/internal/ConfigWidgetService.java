/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel.internal;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.core.ConfigConstants;
import org.eclipse.smarthome.core.service.AbstractWatchService;
import org.openhab.ui.habpanel.internal.widgets.pkg.AbstractWidgetPackage;
import org.openhab.ui.habpanel.internal.widgets.pkg.DirectoryWidgetPackage;
import org.openhab.ui.habpanel.internal.widgets.pkg.ZipWidgetPackage;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Patrick Spiss
 *
 */
public class ConfigWidgetService extends AbstractWatchService {

    private final Logger logger = LoggerFactory.getLogger(ConfigWidgetService.class);
    private final Class<?>[] packageProvider = { DirectoryWidgetPackage.class, ZipWidgetPackage.class };

    protected BundleContext context;
    protected Map<String, AbstractWidgetPackage> packages = new HashMap<String, AbstractWidgetPackage>();

    final private static String WIDGETS_CONFIG_DIR = "habpanel-widgets";

    public ConfigWidgetService() {
        super(ConfigConstants.getConfigFolder() + File.separator + WIDGETS_CONFIG_DIR);
    }

    public void activate(BundleContext context) {
        super.activate();

        this.context = context;

        logger.info("Start to watch for habpanel widgets in {}", this.getSourcePath());

        File[] files = this.getSourcePath().toFile().listFiles();
        for (int i = 0; i < files.length; i++) {
            this.addPackage(files[i]);
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        logger.info("Stop to watch for habpanel widgets in {}", this.getSourcePath());
    }

    @Override
    protected boolean watchSubDirectories() {
        return true;
    }

    @Override
    protected Kind<?>[] getWatchEventKinds(Path subDir) {
        return new Kind<?>[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY };
    }

    @Override
    protected void processWatchEvent(WatchEvent<?> event, Kind<?> kind, Path path) {
        path = this.getSourcePath().relativize(path);
        File rootPath = this.getSourcePath().resolve(path.getName(0)).toFile();

        if (kind.equals(ENTRY_DELETE)) {
            this.removePackage(rootPath);
        } else {
            if (this.packages.containsKey(rootPath.getName())) {
                this.packages.get(rootPath.getName()).refresh();
            } else {
                this.addPackage(rootPath);
            }
        }
    }

    protected void addPackage(File path) {
        boolean loaded = false;
        int n = 0;

        logger.debug("Try to load package '{}'", path.getName());

        while (n < this.packageProvider.length && !loaded) {
            try {
                AbstractWidgetPackage widgetPackage = (AbstractWidgetPackage) this.packageProvider[n].newInstance();
                if (widgetPackage.load(this.context, path)) {
                    loaded = true;
                    packages.put(path.getName(), widgetPackage);

                    logger.info("Package '{}' added as '{}' - {} widget(s) loaded", path.getName(),
                            widgetPackage.getClass().getSimpleName(), widgetPackage.getWidgets().size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            n++;
        }
    }

    public void removePackage(File path) {
        if (this.packages.containsKey(path.getName())) {

            logger.debug("Remove package '{}'", path.getName());

            this.packages.get(path.getName()).unload();
            this.packages.remove(path.getName());
        }
    }

}
