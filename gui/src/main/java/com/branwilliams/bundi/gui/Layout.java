package com.branwilliams.bundi.gui;

import java.util.List;

/**
 * Lays out components in a specific organization. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public interface Layout <C extends Widget> {

    /**
     * Invoked by a container to assemble each component into the layout this layout specifies. <br/>
     * @return An int array representing the width and height which the given container's components occupy.
     * */
    int[] layout(C container, List<Widget> components);

}
