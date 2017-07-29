package org.openhab.ui.habpanel;

public interface WidgetSetting {

    public String getType();

    public String getId();

    public String getLabel();

    public String getGroup();

    public String getDescription();

    public String getDefault();

    public String getChoices();

}
