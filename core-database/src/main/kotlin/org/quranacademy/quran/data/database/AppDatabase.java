package org.quranacademy.quran.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    static final String NAME = "QuranAcademy";

    static final int VERSION = 9;

}