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

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

public class ClipboardUtil {
    @SuppressWarnings("deprecation")
    public static void setText(Context context, CharSequence sequence) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipData clip = ClipData.newPlainText("ClipboardManagerUtil", sequence);
            clipboardManager.setPrimaryClip(clip);
        } else {
            clipboardManager.setText(sequence);
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasText(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            return hasText(clipboardManager, clipData);
        } else {
            return clipboardManager.hasText();
        }
    }

    @SuppressWarnings("deprecation")
    public static CharSequence getText(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (hasText(clipboardManager, clipData)) {
                return clipData.getItemAt(0).getText();
            } else {
                return null;
            }
        } else {
            return clipboardManager.getText();
        }
    }

    private static boolean hasText(ClipboardManager clipboardManager, ClipData clipData) {
        final ClipDescription description = clipboardManager.getPrimaryClipDescription();
        return clipData != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
    }
}
