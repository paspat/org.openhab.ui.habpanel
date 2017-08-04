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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.openhab.ui.habpanel.Widget;
import org.openhab.ui.habpanel.WidgetPackage;
import org.openhab.ui.habpanel.WidgetSetting;
import org.openhab.ui.habpanel.internal.widgets.WidgetJsonSerializer;
import org.openhab.ui.habpanel.internal.widgets.WidgetSettingJsonSerializer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Patrick Spiss
 *
 */
public abstract class AbstractWidgetPackage implements WidgetPackage {

    private final static Logger logger = LoggerFactory.getLogger(AbstractWidgetPackage.class);
    private final static String PACKAGE_CONFIG_FILE = "widget-package.json";

    protected BundleContext context;
    protected ServiceRegistration<WidgetPackage> service;
    protected transient File path;
    protected List<Widget> widgets = new ArrayList<Widget>();
    protected List<String> css = new ArrayList<String>();
    protected List<String> scripts = new ArrayList<String>();

    @Override
    public String getPackageId() {
        return this.path.getName();
    }

    @Override
    public String getName() {
        return this.path.getName();
    }

    public boolean load(BundleContext context, File path) {
        this.context = context;
        this.path = path;
        this.loadPackageDescriptor();

        if (this.widgets.size() > 0) {
            this.service = context.registerService(WidgetPackage.class, this, new Hashtable<>());
            return true;
        }

        return false;
    }

    public void refresh() {
        loadPackageDescriptor();
    }

    public void unload() {
        if (this.service != null) {
            this.service.unregister();
            this.service = null;
        }
    }

    protected void loadPackageDescriptor() {
        logger.debug("Process package descriptor from '{}'", path);

        this.widgets = new ArrayList<Widget>();
        this.css = new ArrayList<String>();
        this.scripts = new ArrayList<String>();

        InputStream configIn = this.getResource(PACKAGE_CONFIG_FILE);
        if (configIn == null) {
            logger.error("Could not find {}", PACKAGE_CONFIG_FILE);
        } else {
            Gson gson = new Gson();
            JsonObject config = gson.fromJson(new InputStreamReader(configIn), JsonObject.class);
            if (config != null) {
                Iterator<Entry<String, JsonElement>> itConfig = config.entrySet().iterator();
                while (itConfig.hasNext()) {
                    Entry<String, JsonElement> configEntry = itConfig.next();

                    if (configEntry.getKey().equals("resources") && configEntry.getValue().isJsonObject()) {
                        JsonObject resources = configEntry.getValue().getAsJsonObject();
                        loadResources(resources, "css", this.css);
                        loadResources(resources, "scripts", this.scripts);

                    } else if (configEntry.getKey().equals("widgets") && configEntry.getValue().isJsonObject()) {
                        Iterator<Entry<String, JsonElement>> itWidgets = configEntry.getValue().getAsJsonObject()
                                .entrySet().iterator();

                        while (itWidgets.hasNext()) {
                            Entry<String, JsonElement> widgetEntry = itWidgets.next();

                            JsonObject widgetJson = null;

                            if (widgetEntry.getValue().isJsonObject()) {
                                logger.debug("Load widget config for '{}' from json object", widgetEntry.getKey());
                                widgetJson = widgetEntry.getValue().getAsJsonObject();
                            } else if (widgetEntry.getValue().isJsonPrimitive()) {
                                String value = widgetEntry.getValue().getAsString();
                                if (value.length() > 11 && value.startsWith("resource://")) {
                                    logger.debug("Load widget config for '{}' from resource '{}'", widgetEntry.getKey(),
                                            value.substring(11));
                                    InputStream widgetDefIn = this.getResource(value.substring(11));
                                    if (widgetDefIn != null) {
                                        widgetJson = gson.fromJson(new InputStreamReader(widgetDefIn),
                                                JsonObject.class);
                                        try {
                                            widgetDefIn.close();
                                        } catch (IOException e) {
                                        }
                                    }
                                }
                            }

                            if (widgetJson != null) {
                                GsonBuilder builder = new GsonBuilder();
                                builder.registerTypeAdapter(Widget.class,
                                        new WidgetJsonSerializer(widgetEntry.getKey()));
                                builder.registerTypeAdapter(WidgetSetting.class, new WidgetSettingJsonSerializer());

                                Widget widget = builder.create().fromJson(widgetJson, Widget.class);
                                if (widget.getTemplate().length() > 11
                                        && widget.getTemplate().startsWith("resource://")) {
                                    logger.debug("Load widget template from resource '{}'",
                                            widget.getTemplate().substring(11));
                                    InputStream widgetTemplateIn = this.getResource(widget.getTemplate().substring(11));
                                    if (widgetTemplateIn != null) {
                                        try {
                                            widget.setTemplate(IOUtils.toString(widgetTemplateIn));
                                        } catch (IOException e) {
                                            logger.error("Could not load template resource '{}'",
                                                    widget.getTemplate().substring(11), e);
                                        } finally {
                                            try {
                                                widgetTemplateIn.close();
                                            } catch (IOException e) {
                                            }
                                        }
                                    } else {
                                        widget.setTemplate("");
                                    }
                                }
                                this.widgets.add(widget);
                            }
                        }
                    }
                }
            }

            try {
                configIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public InputStream getResource(String path) {
        return null;
    }

    @Override
    public InputStream getStaticResource(String path) {
        logger.debug("Get static resource {}", path);

        return this.getResource("/static/" + path);
    }

    @Override
    public List<Widget> getWidgets() {
        return this.widgets;
    }

    @Override
    public List<String> getCSSResources() {
        return this.css;
    }

    @Override
    public List<String> getScriptResources() {
        return this.scripts;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [path=" + this.path + "]";
    }

    private void loadResources(JsonObject resources, String name, List<String> list) {
        if (resources.has(name) && resources.get(name).isJsonArray()) {
            Iterator<JsonElement> it = resources.get(name).getAsJsonArray().iterator();
            while (it.hasNext()) {
                JsonElement elem = it.next();
                if (elem.isJsonPrimitive()) {
                    list.add(elem.getAsString());
                }
            }
        }
    }

}
