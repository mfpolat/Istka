package com.okuu.istkaafet;

/**
 * Created by fatih on 20.5.2015.
 */

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.util.StreamUtils;

import java.io.InputStream;

public class AssetsTileSource extends CustomBitmapTileSourceBase {
    private final AssetManager mAssetManager;

    public AssetsTileSource(final AssetManager assetManager, final String aName, final string aResourceId,
                            final int aZoomMinLevel, final int aZoomMaxLevel, final int aTileSizePixels,
                            final String aImageFilenameEnding) {
        super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding);
        mAssetManager = assetManager;
    }

    @Override
    public Drawable getDrawable(final String aFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = mAssetManager.open(aFilePath);
            if (inputStream != null) {
                final Drawable drawable = getDrawable(inputStream);
                return drawable;
            }
        } catch (final Throwable e) {
            // Tile does not exist in assets folder.
            // Ignore silently
        } finally {
            if (inputStream != null) {
                StreamUtils.closeStream(inputStream);
            }
        }

        return null;
    }
}
