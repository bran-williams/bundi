package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.HttpUtils;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Image;
import com.branwilliams.bundi.gui.api.loader.ComponentFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ImageFactory extends ComponentFactory<Image> {

    private static final String TEMP_IMAGE_NAME = "tempImg";

    private final EngineContext context;

    public ImageFactory(EngineContext context) {
        this.context = context;
    }

    @Override
    public Image createComponent(Toolbox toolbox, Node node, NamedNodeMap attributes) {
        String altText = XmlUtils.getAttributeText(attributes, "altText", "None");

        String imgUrl = XmlUtils.getAttributeText(attributes, "url", null);
        String imgAssetPath = XmlUtils.getAttributeText(attributes, "asset", null);

        // Default set to image asset path.
        String imgFile = imgAssetPath;

        // if URL is specified, try to download it.
        if (imgUrl != null) {
            String fileName;
            try {
                fileName = getFilenameFromURL(imgUrl);
            } catch (MalformedURLException e) {
                fileName =  getNextTempFilename(context.getTempDirectory(), TEMP_IMAGE_NAME, 0);
            }

            File outputFile = new File(context.getTempDirectory().toFile(), fileName);

            if (outputFile.exists()) {
                imgFile = outputFile.getPath();
            } else {
                imgFile =
                        HttpUtils.downloadImage(context.getTempDirectory(), fileName, imgUrl, false);
            }
        }

        Texture texture = null;

        if (imgFile != null) {
            TextureLoader textureLoader = new TextureLoader(context);
            try {
                TextureData textureData = textureLoader.loadTexture(imgFile);
                texture = new Texture(textureData, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Image image = new Image(altText, texture);
        return image;
    }

    private String getFilenameFromURL(String url) throws MalformedURLException {
        url = new URL(url).getPath();

        String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );

        if (url.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        return fileName;
    }

    private String getNextTempFilename(Path directory, String fileName, int count) {
        String result = fileName + "_" + count;

        File file = new File(directory.toFile(), result);

        if (file.exists()) {
            return getNextTempFilename(directory, fileName, count + 1);
        }

        return result;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.COMPONENT;
    }

    @Override
    public String getName() {
        return "image";
    }
}
