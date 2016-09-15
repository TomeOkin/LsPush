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
package com.tomeokin.lspush.common;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;

public class ImageIntentUtils {
    private ImageIntentUtils() {}

    public static final String[] PERMISSION_PICK_IMAGE = new String[] {
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final String[] PERMISSION_TAKE_PHOTO = new String[] {
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * With Build.VERSION_CODES.KITKAT and above, we can see a document and we can select another app in drawer.
     * For other, they won't see the document activity.
     */
    public static Intent createSelectJPEGIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    /**
     * Using with Intent.ACTION_OPEN_DOCUMENT mean you will not see other suitable app. But it limit what user selected
     * is compat with image.
     */
    public static Intent createSelectImageIntentWithDocumentCompat() {
        Intent intent = new Intent().setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        return intent;
    }

    public static Intent createTakePhotoIntent(File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        return intent;
    }
}
