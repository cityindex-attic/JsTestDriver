package com.google.jstestdriver.eclipse.ui.icon;

import org.eclipse.jface.resource.ImageDescriptor;

public class Icons {

    public ImageDescriptor startServerIcon() {
        return ImageDescriptor.createFromFile(getClass(), "StartServer.png");
    }

    public ImageDescriptor stopServerIcon() {
        return ImageDescriptor.createFromFile(getClass(), "StopServer.png");
    }

}
