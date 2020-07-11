package com.branwilliams.bundi.engine.core.screenshot;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL11.*;

public class ScreenshotCapturer {

    private final Path screenshots;

    public ScreenshotCapturer(EngineContext context) {
        this(context.getScreenshotDirectory());
    }

    public ScreenshotCapturer(Path directory) {
        this.screenshots = directory;
    }

    public boolean screenshot() {
        return screenshot(this.screenshots);
    }

    /**
     * TODO move this into the Window class and make the screenshots directory be configurable.
     * */
    public static boolean screenshot(Path screenshotsDirectory) {
        String filename =  new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss'.png'").format(new Date());
        return screenshot(new File(screenshotsDirectory.toFile(), filename));
    }

    public static boolean screenshot(File output) {
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport);

        ByteBuffer buffer = MemoryUtil.memAlloc(3 * viewport[2] * viewport[3]);

        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glReadPixels(viewport[0], viewport[1], viewport[2], viewport[3], GL_RGB, GL_UNSIGNED_BYTE, buffer);

        STBImageWrite.stbi_flip_vertically_on_write(true);
        boolean result = STBImageWrite.stbi_write_png(output.getPath(), viewport[2], viewport[3], 3, buffer, 0);
        MemoryUtil.memFree(buffer);

        return result;
    }

    public ScreenshotCaptureListener screenshotOnKeyPress(int keyCode) {
        return new ScreenshotCaptureListener(this, keyCode);
    }

}
