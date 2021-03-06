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
package com.tomeokin.lspush.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.wireless.security.jaq.JAQException;
import com.tomeokin.lspush.data.crypt.BeeCrypto;
import com.tomeokin.lspush.data.model.User;

import rx.functions.Func1;

public class Db {
    private Db() {}

    public static class DatabaseException extends RuntimeException {
        private static final long serialVersionUID = 8091840972021327488L;
        private final int errorCode;

        public DatabaseException(int errorCode) {
            this.errorCode = errorCode;
        }

        public DatabaseException(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public DatabaseException(String message, Throwable cause, int errorCode) {
            super(message, cause);
            this.errorCode = errorCode;
        }

        public DatabaseException(Throwable cause, int errorCode) {
            super(cause);
            this.errorCode = errorCode;
        }
    }

    public abstract static class UserTable {
        public static final String TABLE_NAME = "user_table";

        //public static final String ID = "_id";
        public static final String UID = "uid";
        public static final String NICKNAME = "nickname";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String REGION = "region";
        // don't have password column
        public static final String VALIDATE = "validate";
        public static final String AVATAR = "avatar";

        // @formatter:off
        public static final String CREATE = ""
            + "CREATE TABLE " + TABLE_NAME + "("
            //+ ID + " LONG NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + UID + " TEXT NOT NULL PRIMARY KEY, "
            + NICKNAME + " TEXT NOT NULL, "
            + EMAIL + " TEXT, "
            + PHONE + " TEXT, "
            + REGION + " TEXT, "
            + VALIDATE + " INTEGER NOT NULL, "
            + AVATAR + " TEXT"
            + ")";
        // @formatter:on

        // @formatter:off
        public static final String QUERY_BY_UID = "SELECT * FROM " + TABLE_NAME + " WHERE " + UID + " = ?";
        // @formatter:on

        // @formatter:off
        public static final String QUERY_ALL = "SELECT * FROM " + TABLE_NAME;
        // @formatter:on

        public static ContentValues toContentValues(User user) throws JAQException {
            ContentValues values = new ContentValues();
            values.put(UID, user.getUid());
            values.put(NICKNAME, user.getNickname());
            values.put(EMAIL, BeeCrypto.get().encrypt(user.getEmail()));
            values.put(PHONE, BeeCrypto.get().encrypt(user.getPhone()));
            values.put(REGION, user.getRegion());
            values.put(VALIDATE, user.getValidate());
            values.put(AVATAR, user.getImage());
            return values;
        }

        public static final Func1<Cursor, User> MAPPER = new Func1<Cursor, User>() {
            @Override
            public User call(Cursor cursor) {
                try {
                    return parseCursor(cursor);
                } catch (JAQException e) {
                    throw new DatabaseException(e, e.getErrorCode());
                }
            }
        };

        public static User parseCursor(Cursor cursor) throws JAQException {
            User user = new User();
            user.setUid(cursor.getString(cursor.getColumnIndex(UID)));
            user.setNickname(cursor.getString(cursor.getColumnIndex(NICKNAME)));
            user.setEmail(BeeCrypto.get().decrypt(cursor.getString(cursor.getColumnIndex(EMAIL))));
            user.setPhone(BeeCrypto.get().decrypt(cursor.getString(cursor.getColumnIndex(PHONE))));
            user.setRegion(cursor.getString(cursor.getColumnIndex(REGION)));
            user.setValidate(cursor.getInt(cursor.getColumnIndex(VALIDATE)));
            user.setImage(cursor.getString(cursor.getColumnIndex(AVATAR)));
            return user;
        }
    }
}
