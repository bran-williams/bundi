package com.branwilliams.bundi.engine.ecs;

/**
 * Used to identify entities which conform to some requirements. (having some specific components as the class name may
 * imply)
 * <br/> <br/>
 *
 * e.g. <br/>
 *  - Containing the required components <br/>
 *  - Having some certain properties <br/>
 *
 *  <br/>
 *  This is primarily used to group entities within {@link EntitySystemManager}.
 *
 * Created by Brandon Williams on 6/24/2018.
 */
public interface IComponentMatcher {

    /**
     * @return True if the provided entity meets the requirements of this matcher.
     * */
    boolean matches(IEntity entity);

}
