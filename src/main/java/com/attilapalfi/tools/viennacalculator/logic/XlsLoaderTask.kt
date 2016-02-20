package com.attilapalfi.tools.viennacalculator.logic

import com.attilapalfi.tools.viennacalculator.model.AssetFoundHolder
import com.attilapalfi.tools.viennacalculator.model.AssetFund
import com.attilapalfi.tools.viennacalculator.model.ValueEntry
import javafx.concurrent.Task
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import sun.plugin.dom.exception.InvalidStateException
import java.io.File
import java.io.FileInputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by palfi on 2016-02-20.
 */
class XlsLoaderTask(private val sourceFile: File) : Task<AssetFoundHolder>() {

    private val safeAssetFundName = "Pénzpiaci forint"
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")
    private val numberFormatter = NumberFormat.getInstance(Locale.FRANCE)

    private val assetFunds: MutableList<AssetFund> = ArrayList()
    private var safeAssetFund: AssetFund? = null

    override fun call(): AssetFoundHolder {
        val start = System.currentTimeMillis()
        doLoad()
        println(System.currentTimeMillis() - start)
        safeAssetFund?.let {
            succeeded()
            return AssetFoundHolder(assetFunds, it)
        }
        throw InvalidStateException("safeAssetFund cannot be null. 'Pénzpiaci forint eszközalap' not found.")
    }

    private fun doLoad() {
        FileInputStream(sourceFile).use {
            val workbook: Workbook = getWorkbook(it)
            fillAssetFounds(workbook)
            safeAssetFund = assetFunds.find { it.name.contains(safeAssetFundName, true) }
        }
    }

    private fun getWorkbook(it: FileInputStream): Workbook {
        val workbook: Workbook = if (sourceFile.extension.contains("xlsx", true)) {
            XSSFWorkbook(it)
        } else {
            HSSFWorkbook(it)
        }
        return workbook
    }

    private fun fillAssetFounds(workbook: Workbook) {
        workbook.sheetIterator().asSequence().forEachIndexed { i, sheet ->
            val fundName: String = sheet.getRow(0).getCell(0).stringCellValue
            val valueHistory = getValueHistory(sheet)
            assetFunds.add(AssetFund(fundName, valueHistory))
            updateProgress(i.toLong(), workbook.numberOfSheets.toLong() - 1)
        }
    }

    private fun getValueHistory(sheet: Sheet): TreeSet<ValueEntry> {
        val valueHistory = TreeSet<ValueEntry>()
        sheet.rowIterator()
                .asSequence()
                .drop(2)
                .requireNoNulls()
                .mapTo(valueHistory, {
                    ValueEntry(parseToEpoch(it.getCell(0).stringCellValue),
                            parseToDouble(it.getCell(1).stringCellValue))
                })
        return valueHistory
    }

    private fun parseToEpoch(stringDate: String): Long =
            dateFormatter.parse(stringDate).time

    private fun parseToDouble(stringDouble: String): Double =
            numberFormatter.parse(stringDouble).toDouble()
}