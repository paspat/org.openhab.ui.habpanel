/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openhab.ui.habpanel.Widget;
import org.openhab.ui.habpanel.WidgetPackage;
import org.openhab.ui.habpanel.WidgetSetting;
import org.openhab.ui.habpanel.internal.widgets.WidgetJsonSerializer;
import org.openhab.ui.habpanel.internal.widgets.WidgetPackageJsonSerializer;
import org.openhab.ui.habpanel.internal.widgets.WidgetSettingJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Patrick Spiss
 *
 */
public class WidgetServlet extends HttpServlet {

    private static final long serialVersionUID = -5202209370115569415L;
    private static final Logger logger = LoggerFactory.getLogger(WidgetServlet.class);

    private WidgetService service;

    public WidgetServlet(WidgetService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeHierarchyAdapter(WidgetPackage.class, new WidgetPackageJsonSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(Widget.class, new WidgetJsonSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(WidgetSetting.class, new WidgetSettingJsonSerializer());
        Gson gson = gsonBuilder.create();

        if (req.getPathInfo() != null && !req.getPathInfo().trim().equals("/")) {

            String path = req.getPathInfo().substring(1);
            logger.debug("Load resource '{}'", path);
            if (path.indexOf("/") == -1) {
                resp.sendError(404);
            } else {
                String packageId = path.substring(0, path.indexOf("/"));
                path = path.substring(path.indexOf("/") + 1);

                WidgetPackage pkg = null;// this.service.getPackages().get(packageId);
                Iterator<WidgetPackage> it = this.service.getPackages().iterator();
                while (it.hasNext() && pkg == null) {
                    WidgetPackage p = it.next();
                    if (p.getPackageId().equals(packageId)) {
                        pkg = p;
                    }
                }

                if (pkg == null) {
                    resp.sendError(404);
                } else {
                    InputStream in = pkg.getStaticResource(path);
                    if (in == null) {
                        resp.sendError(404);
                    } else {
                        IOUtils.copy(in, resp.getOutputStream());
                        in.close();
                    }
                }
            }
        } else {
            gson.toJson(this.service.getPackages(), resp.getWriter());
        }
    }

}
