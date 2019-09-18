package com.branwilliams.bundi.voxel.render.gui;

import com.branwilliams.bundi.gui.ContainerManager;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since August 17, 2019
 */
public class AbstractContainerScreen extends ContainerScreen {

    public AbstractContainerScreen(Supplier<ContainerManager> containerManagerSupplier) {
        super(containerManagerSupplier.get());

    }

    public AbstractContainerScreen(ContainerManager containerManager) {
        super(containerManager);
    }

}
