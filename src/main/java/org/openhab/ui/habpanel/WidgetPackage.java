package org.openhab.ui.habpanel;

import java.io.InputStream;
import java.util.List;

public interface WidgetPackage {

    String getPackageId();

    String getName();

    InputStream getStaticResource(String path);

    List<Widget> getWidgets();

}
