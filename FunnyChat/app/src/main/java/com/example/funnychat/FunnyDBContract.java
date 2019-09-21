package com.example.funnychat;

public final class FunnyDBContract {
    private FunnyDBContract() {}

    public static class ChatUser {
        public static final String TABLE_NAME = "chatUser";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NICKNAME = "nickName";
        public static final String COLUMN_FOUNDED_DATE = "date";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "(" +
                COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NICKNAME + " TEXT, " +
                COLUMN_FOUNDED_DATE + " INTEGER" + ")";
    }
}