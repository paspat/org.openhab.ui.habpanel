package org.openhab.ui.habpanel.internal.widgets;

import java.lang.reflect.Type;

import org.openhab.ui.habpanel.WidgetPackage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WidgetPackageJsonSerializer implements JsonSerializer<WidgetPackage> {

    @Override
    public JsonElement serialize(WidgetPackage pkg, Type typeOf, JsonSerializationContext context)
            throws JsonParseException {

        JsonObject ret = new JsonObject();
        ret.addProperty("packageId", pkg.getPackageId());
        ret.addProperty("name", pkg.getName());
        ret.add("widgets", context.serialize(pkg.getWidgets()));

        return ret;

    }

}
