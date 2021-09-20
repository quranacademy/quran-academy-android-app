package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ConflictAction
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase


@Table(
        name = "TranslationsOrder",
        database = AppDatabase::class,
        useBooleanGetterSetters = false,
        primaryKeyConflict = ConflictAction.REPLACE
)
class TranslationOrderModel() {

    @PrimaryKey
    @Column(name = "translation_code")
    lateinit var translationCode: String

    @Column(name = "show_in_dialog")
    var showInDialog: Boolean = false

    @Column(name = "order")
    var order: Int = 0

    constructor(
            translationCode: String,
            showInDialog: Boolean,
            order: Int
    ) : this() {
        this.translationCode = translationCode
        this.showInDialog = showInDialog
        this.order = order
    }

}