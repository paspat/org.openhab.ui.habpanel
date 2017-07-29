package org.openhab.ui.habpanel.internal.widgets;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openhab.ui.habpanel.WidgetSetting;
import org.openhab.ui.habpanel.WidgetSettingImpl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WidgetSettingJsonSerializer
        implements JsonDeserializer<List<WidgetSetting>>, JsonSerializer<WidgetSetting> {

    @Override
    public JsonElement serialize(WidgetSetting setting, Type typeOf, JsonSerializationContext context)
            throws JsonParseException {

        JsonObject ret = new JsonObject();

        ret.addProperty("type", setting.getType());
        ret.addProperty("id", setting.getId());
        ret.addProperty("label", setting.getLabel());
        ret.addProperty("group", setting.getGroup());
        ret.addProperty("description", setting.getDescription());
        ret.addProperty("default", setting.getDefault());
        ret.addProperty("choices", setting.getChoices());

        return ret;

    }

    @Override
    public List<WidgetSetting> deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context)
            throws JsonParseException {
        List<WidgetSetting> list = new ArrayList<WidgetSetting>();

        if (json.isJsonArray()) {
            Iterator<JsonElement> it = json.getAsJsonArray().iterator();
            while (it.hasNext()) {
                WidgetSetting st = processSettingElement(it.next());
                if (st != null) {
                    list.add(st);
                }
            }
        } else if (json.isJsonObject()) {
            WidgetSetting st = processSettingElement(json);
            if (st != null) {
                list.add(st);
            }
        }

        return list;
    }

    private WidgetSetting processSettingElement(JsonElement setting) {
        if (setting.isJsonObject()) {
            JsonObject obj = setting.getAsJsonObject();
            if (obj.has("type") && obj.has("id")) {
                WidgetSettingImpl st = new WidgetSettingImpl(obj.get("type").getAsString(),
                        obj.get("id").getAsString());

                if (obj.has("label")) {
                    st.setLabel(obj.get("label").getAsString());
                }

                if (obj.has("group")) {
                    st.setGroup(obj.get("group").getAsString());
                }

                if (obj.has("description")) {
                    st.setDescription(obj.get("description").getAsString());
                }

                if (obj.has("default")) {
                    st.setDefault(obj.get("default").getAsString());
                }

                if (obj.has("choices")) {
                    st.setChoices(obj.get("choices").getAsString());
                }

                return st;
            }
        }

        return null;
    }
}
