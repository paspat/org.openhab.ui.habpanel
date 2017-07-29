package org.openhab.ui.habpanel.internal.widgets;

import java.lang.reflect.Type;
import java.util.Collection;

import org.openhab.ui.habpanel.Widget;
import org.openhab.ui.habpanel.WidgetImpl;
import org.openhab.ui.habpanel.WidgetSetting;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WidgetJsonSerializer implements JsonDeserializer<Widget>, JsonSerializer<Widget> {

    private String id;

    public WidgetJsonSerializer() {

    }

    public WidgetJsonSerializer(String id) {
        this.id = id;
    }

    @Override
    public JsonElement serialize(Widget widget, Type typeOf, JsonSerializationContext context)
            throws JsonParseException {

        JsonObject ret = new JsonObject();

        ret.addProperty("widgetId", widget.getWidgetId());
        ret.addProperty("name", widget.getName());
        ret.addProperty("description", widget.getDescription());
        ret.addProperty("author", widget.getAuthor());
        ret.addProperty("dontwrap", widget.getDontWrap());
        ret.addProperty("template", widget.getTemplate());

        ret.add("settings", context.serialize(widget.getSettings()));

        return ret;

    }

    @SuppressWarnings("unchecked")
    @Override
    public Widget deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject widgetObject = json.getAsJsonObject();

        Widget widget = new WidgetImpl(this.id);

        if (widgetObject.has("name")) {
            widget.setName(widgetObject.get("name").getAsString());
        }

        if (widgetObject.has("author")) {
            widget.setAuthor(widgetObject.get("author").getAsString());
        }

        if (widgetObject.has("description")) {
            widget.setAuthor(widgetObject.get("description").getAsString());
        }

        if (widgetObject.has("dontwrap")) {
            widget.setDontWrap(widgetObject.get("dontwrap").getAsBoolean());
        }

        if (widgetObject.has("template")) {
            widget.setTemplate(widgetObject.get("template").getAsString());
        }

        if (widgetObject.has("settings")) {
            widget.getSettings().addAll((Collection<? extends WidgetSetting>) context
                    .deserialize(widgetObject.getAsJsonArray("settings"), WidgetSetting.class));
        }

        return widget;
    }
}
