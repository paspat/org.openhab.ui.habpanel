/**
 * Copyright (c) 2015-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.habpanel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openhab.ui.habpanel.internal.widgets.WidgetJsonSerializer;
import org.openhab.ui.habpanel.internal.widgets.WidgetSettingJsonSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Patrick Spiss
 *
 */
public class WidgetImpl implements Widget {

    private String id;
    protected String name;
    protected String author;
    protected String description;
    protected boolean dontwrap;
    protected String template;

    private List<WidgetSetting> settings = new ArrayList<WidgetSetting>();

    public WidgetImpl(String id) {
        this.id = id;
    }

    @Override
    public String getWidgetId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean getDontWrap() {
        return dontwrap;
    }

    @Override
    public void setDontWrap(boolean dontwrap) {
        this.dontwrap = dontwrap;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public List<WidgetSetting> getSettings() {
        return this.settings;
    }

    public static Widget loadFromJson(String widgetId, InputStream in) throws IOException {
        return loadFromJson(widgetId, IOUtils.toString(in));
    }

    public static Widget loadFromJson(String widgetId, String json) {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Widget.class, new WidgetJsonSerializer(widgetId))
                .registerTypeHierarchyAdapter(WidgetSetting.class, new WidgetSettingJsonSerializer()).create();
        return gson.fromJson(json, Widget.class);
    }

}