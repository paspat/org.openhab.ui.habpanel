/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel.internal;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.openhab.ui.habpanel.WidgetPackage;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Patrick Spiss
 *
 */
public class WidgetService {

    private final Logger logger = LoggerFactory.getLogger(WidgetService.class);

    protected HttpService httpService;
    private List<WidgetPackage> packages = new ArrayList<WidgetPackage>();

    final private static String SERVLET_PATH = "/habpanel/configwidgets";

    public void activate() {
        try {
            this.httpService.registerServlet(SERVLET_PATH, new WidgetServlet(this), new Hashtable<>(),
                    httpService.createDefaultHttpContext());
            logger.info("Started HABPanel widget service at '{}'", SERVLET_PATH);
        } catch (NamespaceException | ServletException e) {
            logger.error("Error during config widget provider startup: {}", e.getMessage());
        }
    }

    public List<WidgetPackage> getPackages() {
        return this.packages;
    }

    public void deactivate() {
        this.httpService.unregister(SERVLET_PATH);
    }

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    protected void addWidgetPackage(WidgetPackage pkg) {
        boolean packageFound = false;
        Iterator<WidgetPackage> it = this.packages.iterator();
        while (it.hasNext()) {
            if (it.next().getPackageId().equals(pkg.getPackageId())) {
                packageFound = true;
            }
        }

        if (pkg.getPackageId() == null || pkg.getPackageId().trim().equals("")) {
            logger.error("Could not add widget package '{}': no package id provided by the package", pkg.toString());
        } else if (pkg.getName() == null || pkg.getName().trim().equals("")) {
            logger.error("Could not add widget package '{}': no name provided by the package", pkg.toString());
        } else if (packageFound) {
            logger.error("Could not add widget package '{}': another widget package with id '{}' is already registered",
                    pkg.toString(), pkg.getPackageId());
        } else {
            logger.debug("Add widget package '{}'", pkg.toString());
            this.packages.add(pkg);
        }
    }

    protected void removeWidgetPackage(WidgetPackage pkg) {
        logger.debug("Remove widget package '{}'", pkg.toString());
        this.packages.remove(pkg);
    }
}
