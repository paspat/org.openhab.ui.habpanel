package org.openhab.ui.habpanel;

import java.util.List;

public interface Widget {

    public String getWidgetId();

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getAuthor();

    public void setAuthor(String author);

    public boolean getDontWrap();

    public void setDontWrap(boolean dontwrap);

    public void setTemplate(String template);

    public String getTemplate();

    public List<WidgetSetting> getSettings();

}
