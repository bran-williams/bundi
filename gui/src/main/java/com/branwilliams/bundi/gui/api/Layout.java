package com.branwilliams.bundi.gui.api;

import java.util.List;

/**
 * Lays out components in a specific organization. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public interface Layout <Container extends Widget, Component extends Widget> {

    /**
     * Invoked by a container to assemble each component into the layout this layout specifies. <br/>
     * @return An int array representing the width and height which the given container's components occupy.
     * */
    int[] layout(Container container, List<Component> components);

    static <ContainerType extends Widget, ComponentType extends Widget> Layout<ContainerType, ComponentType> empty() {
        return  (container, components) -> new int[] { container.getWidth(), container.getHeight() };
    }

}
