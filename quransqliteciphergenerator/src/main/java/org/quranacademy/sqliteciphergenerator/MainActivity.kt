package org.quranacademy.sqliteciphergenerator

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.AssetsFileExtractor
import org.quranacademy.sqliteciphergenerator.openhelpers.*
import org.quranacademy.sqliteciphergenerator.tablemanagers.AyahsTableManager
import org.quranacademy.sqliteciphergenerator.tablemanagers.WordByWordTableManager
import java.io.File
import kotlin.coroutines.CoroutineContext

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val ENCRYPTED_DB_NAME = "result.db"
    }

    private val parentJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    private var currentOperation: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch {
            SQLiteDatabaseCipher.loadLibs(applicationContext)
        }

        convert.setOnClickListener {
            currentOperation = launch(context = Dispatchers.IO) {
                startDatabasePreparing()
            }
        }
        operationResult.text = "Статус: ожидание"
    }

    private fun startDatabasePreparing() {
        currentOperation?.cancel()

        getDbFile(ENCRYPTED_DB_NAME).delete()
        val hqaDatabase = connectToSourceDatabase("hqa_database.db")
        val tajweedWords = connectToSourceDatabase("tajweedwords.db")
        //Не нужно, т. к. у нас используются только слова.
        //Обычный текст аятов (бещ таджвида) нужен только для копирования/шейринга
        val tajweedAyahs = connectToSourceDatabase("tajweedtext.db")
        val resultDatabase = connectToResultDatabase(isEncryptedOption.isChecked)

        AyahsTableManager.createTable(resultDatabase)
        WordByWordTableManager.createTable(resultDatabase)

        val listener = { progress: Progress ->
            runOnUI { operationResult.text = "Статус: идет конвертация ${progress.table} (${progress.current}/${progress.count})" }
        }

        runOnUI { operationResult.text = "Статус: началась конвертация" }

        try {
            resultDatabase.beginTransaction()
            AyahsTableManager.copyAyahs(hqaDatabase, resultDatabase,  listener)
            WordByWordTableManager.copyWords(hqaDatabase, tajweedWords, resultDatabase,  listener)
            resultDatabase.setTransactionSuccessful()
        } finally {
            resultDatabase.endTransaction()
            resultDatabase.close()
        }

        runOnUI {
            Toast.makeText(this, "Converted", Toast.LENGTH_SHORT).show()
            operationResult.text = "Статус: конвертация завершена"
        }
    }

    private fun runOnUI(body: () -> Unit) {
        launch(context = Dispatchers.Main) { body() }
    }

    private fun getDbFile(databaseFileName: String) = File(applicationContext.filesDir, databaseFileName)

    private fun connectToSourceDatabase(
            databaseFileName: String
    ): android.database.sqlite.SQLiteDatabase {
        //val databaseFileName = "hqa_database.db"
        val hqaDbFile = getDbFile(databaseFileName)
        AssetsFileExtractor(this).extract(databaseFileName, hqaDbFile)
        return DbOpenHelper(this, hqaDbFile.absolutePath).readableDatabase
    }

    private fun connectToResultDatabase(encrypted: Boolean): DatabaseWrapper {
        val resultDatabaseFile = File(applicationContext.filesDir, "result.db")
        resultDatabaseFile.delete()
        val databasePath = resultDatabaseFile.absolutePath
        return if (encrypted) {
            EncryptedDatabase(databasePath)
        } else {
            NotEncryptedDatabase(databasePath)
        }
    }

    class Progress(val table: String, val current: Int, val count: Int)

}
