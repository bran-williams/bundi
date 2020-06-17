package com.branwilliams.bundi.gui.screen;

import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.gui.api.ContainerManager;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since August 17, 2019
 */
public class AbstractContainerScreen <SceneType extends Scene> extends ContainerScreen<SceneType> {

    public AbstractContainerScreen(Supplier<ContainerManager> containerManagerSupplier) {
        super(containerManagerSupplier.get());

    }

    public AbstractContainerScreen(ContainerManager containerManager) {
        super(containerManager);
    }

}
