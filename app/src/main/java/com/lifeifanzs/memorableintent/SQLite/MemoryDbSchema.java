package com.lifeifanzs.memorableintent.SQLite;

public class MemoryDbSchema {
    public static final class MemoryTable {
        public static final String NAME = "memories";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String TIME = "time";
            public static final String DETAIL = "detail";
            public static final String SOLVED = "solved";
            public static final String ISNOTE = "isnote";
            public static final String OVER = "over";
            public static final String SIGN = "sign";
            public static final String ZHIDING = "zhiding";
            public static final String ZHIDINGDATE = "zhidingdate";
            public static final String REMIND = "remind";
        }
    }
}
