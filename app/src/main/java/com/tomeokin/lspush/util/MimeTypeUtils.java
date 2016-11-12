/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.lspush.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.Arrays;

import timber.log.Timber;

/**
 * @author Peli
 * @author paulburke (ipaulpro)
 * @author TomeOkin
 */
public class MimeTypeUtils {
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String[] IMAGE_PREFIX = new String[] {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp"
    };

    static {
        Arrays.sort(IMAGE_PREFIX);
    }

    private static final boolean DEBUG = false; // Set to true to enable logging
    private static final String TAG = "MimeTypeUtils";

    private MimeTypeUtils() { }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(@NonNull File file) {
        String extension = getExtension(file.getName());
        if (!TextUtils.isEmpty(extension)) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }

        return APPLICATION_OCTET_STREAM;
    }

    /**
     * @return The MIME type for the give Uri.
     */
    public static String getMimeType(Context context, @NonNull Uri uri) {
        final String file = getFilePath(context, uri);
        if (file != null) {
            return getMimeType(new File(file));
        }

        // we unknown the file type
        return APPLICATION_OCTET_STREAM;
    }

    public static boolean isImage(@NonNull File file) {
        String extension = getExtension(file.getName());
        if (!TextUtils.isEmpty(extension)) {
            return Arrays.binarySearch(IMAGE_PREFIX, extension) >= 0;
        }

        // not support file type
        return false;
    }

    public static boolean isImage(Context context, @NonNull Uri uri) {
        final String file = getFilePath(context, uri);
        if (file != null) {
            return isImage(new File(file));
        }

        // when using uri, we could not confirm it is not a image, so assume that true
        return true;
    }

    /**
     * Get the extension of a file name, like ".png" or ".jpg".
     *
     * @return Extension including the dot("."); "" if there is no extension; null if filename was null.
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int dot = filename.lastIndexOf(".");
        if (dot >= 0) {
            return filename.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = "_data";
        final String[] projection = {
            column
        };

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) {
                    DatabaseUtils.dumpCursor(cursor);
                }
                final int columnIndex = cursor.getColumnIndex(column);
                return columnIndex < 0 ? null : cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     */
    public static String getFilePath(final Context context, final Uri uri) {
        if (DEBUG) {
            Timber.tag(TAG)
                .d("Authority: %s, Fragment: %s, Port: %d, Query: %s, Scheme: %s, Host: %s, Segments: %s",
                    uri.getAuthority(), uri.getFragment(), uri.getPort(), uri.getQuery(), uri.getScheme(),
                    uri.getScheme(), uri.getPathSegments());
        }

        // DocumentProvider
        final boolean hasDocumentProvider = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (hasDocumentProvider && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    // TODO handle non-primary volumes
                    return null;
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri =
                    ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            // content://com.android.providers.media.documents/document/image%3A57
            // content://com.android.providers.media.documents/document/image:57
            else if (isMediaDocument(uri)) {
                // image:57
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                    split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        // content://media/external/images/media/13
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        // file:///d/gpio
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
