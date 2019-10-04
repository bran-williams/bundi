package com.branwilliams.bundi.gui.screen;

import com.branwilliams.bundi.gui.api.ContainerManager;

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
