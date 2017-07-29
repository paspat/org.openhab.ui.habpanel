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

public abstract class AbstractWidgetPackage implements WidgetPackage {

    private final static Logger logger = LoggerFactory.getLogger(AbstractWidgetPackage.class);
    private final static String WIDGET_CONFIG_FILE = "widgets.json";

    protected BundleContext context;
    protected ServiceRegistration<WidgetPackage> service;
    protected transient File path;
    protected List<Widget> widgets = new ArrayList<Widget>();

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

        InputStream widgetsIn = this.getResource(WIDGET_CONFIG_FILE);
        if (widgetsIn == null) {
            logger.error("Could not find {}", WIDGET_CONFIG_FILE);
        } else {
            Gson gson = new Gson();
            JsonObject widgets = gson.fromJson(new InputStreamReader(widgetsIn), JsonObject.class);
            if (widgets != null) {
                Iterator<Entry<String, JsonElement>> it = widgets.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, JsonElement> widgetEntry = it.next();
                    JsonObject widgetJson = null;

                    if (widgetEntry.getValue().isJsonObject()) {
                        logger.debug("Load widget config for '{}' from json object", widgetEntry.getKey());
                        widgetJson = widgetEntry.getValue().getAsJsonObject();
                    } else if (widgetEntry.getValue().isJsonPrimitive()) {
                        String value = widgetEntry.getValue().getAsString();
                        if (value.length() > 11 && value.substring(0, 11).equals("resource://")) {
                            logger.debug("Load widget config for '{}' from resource '{}'", widgetEntry.getKey(),
                                    value.substring(11));
                            InputStream widgetDefIn = this.getResource(value.substring(11));
                            if (widgetDefIn != null) {
                                widgetJson = gson.fromJson(new InputStreamReader(widgetDefIn), JsonObject.class);
                                try {
                                    widgetDefIn.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    if (widgetJson != null) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.registerTypeAdapter(Widget.class, new WidgetJsonSerializer(widgetEntry.getKey()));
                        builder.registerTypeAdapter(WidgetSetting.class, new WidgetSettingJsonSerializer());
                        this.widgets.add(builder.create().fromJson(widgetJson, Widget.class));
                    }
                }
            }

            try {
                widgetsIn.close();
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
    public String toString() {
        return this.getClass().getSimpleName() + " [path=" + this.path + "]";
    }

}
