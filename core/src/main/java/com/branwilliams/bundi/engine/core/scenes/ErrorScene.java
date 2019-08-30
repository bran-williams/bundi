package com.branwilliams.bundi.engine.core.scenes;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.context.Ignore;

/**
 * Created by Brandon Williams on 2/2/2018.
 */
@Ignore
public class ErrorScene extends AbstractScene {


    public ErrorScene(Exception exception) {
        super("error_scene");
        this.setRenderer(new ErrorSceneRenderer(exception));
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
    }

    @Override
    public void play(Engine engine) {
        engine.getWindow().showCursor();
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public  boolean destroyUponReplacement() {
        return true;
    }
}
