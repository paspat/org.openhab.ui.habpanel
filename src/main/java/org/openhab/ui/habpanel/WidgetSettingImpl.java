package org.openhab.ui.habpanel;

public class WidgetSettingImpl implements WidgetSetting {

    private String type;
    private String id;
    private String label;
    private String group;
    private String description;

    private String defaultValue;
    private String choices;

    public WidgetSettingImpl(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public WidgetSettingImpl(String type, String id, String label, String group, String description,
            String defaultValue, String choices) {
        this.type = type;
        this.id = id;
        this.label = label;
        this.group = group;
        this.description = description;
        this.defaultValue = defaultValue;
        this.choices = choices;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDefault() {
        return defaultValue;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }
}
