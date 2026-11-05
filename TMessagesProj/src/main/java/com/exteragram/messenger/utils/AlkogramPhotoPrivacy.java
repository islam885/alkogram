package com.exteragram.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/** Local outgoing JPEG copies only. Never writes metadata back to the source. */
public final class AlkogramPhotoPrivacy {
    private AlkogramPhotoPrivacy() { }

    private static InputStream open(String path, Uri uri) throws IOException {
        InputStream stream = path != null ? new FileInputStream(path)
                : ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
        if (stream == null) {
            throw new IOException("Cannot open photo");
        }
        return stream;
    }

    /** Returns null for unsupported formats, a new private cache path for JPEG, or throws. */
    public static String cleanJpeg(String path, Uri uri) throws IOException {
        if (path == null && uri == null) {
            return null;
        }
        try (InputStream input = open(path, uri)) {
            if (input.read() != 0xff || input.read() != 0xd8) {
                return null;
            }
        }
        String source = path;
        if (source == null) {
            source = MediaController.copyFileToCache(uri, "jpg");
            if (source == null) {
                throw new IOException("Cannot copy JPEG");
            }
        }
        Bitmap bitmap = null;
        Bitmap upright = null;
        File output = null;
        File directory = null;
        try {
            // Read orientation before dropping EXIF; mirror orientations are supported too.
            int orientation = new ExifInterface(source).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            bitmap = BitmapFactory.decodeFile(source);
            if (bitmap == null) {
                throw new IOException("Cannot decode JPEG");
            }
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: matrix.setScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_180: matrix.setRotate(180); break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL: matrix.setScale(1, -1); break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90); matrix.postScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_90: matrix.setRotate(90); break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90); matrix.postScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_270: matrix.setRotate(-90); break;
            }
            upright = matrix.isIdentity() ? bitmap : Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            // Use the same app-scoped cache root as the existing upload pipeline.
            directory = new File(AndroidUtilities.getCacheDir(),
                    "alkogram_clean_" + UUID.randomUUID());
            if (!directory.mkdir()) {
                throw new IOException("Cannot create private photo cache");
            }
            String name = new File(source).getName();
            if (!name.toLowerCase(java.util.Locale.US).endsWith(".jpg")
                    && !name.toLowerCase(java.util.Locale.US).endsWith(".jpeg")) {
                name += ".jpg";
            }
            output = new File(directory, name);
            // A fresh pixel encoding carries no source EXIF, GPS, XMP, timestamps or thumbnails.
            try (FileOutputStream stream = new FileOutputStream(output)) {
                if (!upright.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw new IOException("Cannot encode clean JPEG");
                }
                stream.getFD().sync();
            }
            if (output.length() == 0) {
                throw new IOException("Empty clean JPEG");
            }
            return output.getAbsolutePath();
        } catch (IOException | RuntimeException | OutOfMemoryError error) {
            if (output != null) output.delete();
            if (directory != null) directory.delete();
            throw new IOException("Photo metadata cleanup failed", error);
        } finally {
            if (upright != null && upright != bitmap) upright.recycle();
            if (bitmap != null) bitmap.recycle();
            if (path == null) new File(source).delete();
        }
    }

    public static void showFailure(Exception error) {
        FileLog.e(error);
        AndroidUtilities.runOnUIThread(() -> Toast.makeText(ApplicationLoader.applicationContext,
                LocaleController.getString(R.string.AlkoStripMetadataFailed), Toast.LENGTH_LONG).show());
    }
}
