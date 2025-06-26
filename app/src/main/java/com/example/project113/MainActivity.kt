package com.example.project113  // ЗАМЕНИТЕ НА ВАШ ПАКЕТ!

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Объявляем все UI-компоненты
    private lateinit var inputValue: EditText
    private lateinit var outputValue: TextView
    private lateinit var sourceUnitSpinner: Spinner
    private lateinit var targetUnitSpinner: Spinner

    // Доступные единицы измерения
    private val units = arrayOf("Метры", "Километры", "Мили", "Футы")

    // Коэффициенты преобразования в метрах
    private val conversionRates = mapOf(
        "Метры" to 1.0,
        "Километры" to 1000.0,
        "Мили" to 1609.34,
        "Футы" to 0.3048
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Убедитесь, что имя layout совпадает

        // Инициализация всех элементов по их ID
        inputValue = findViewById(R.id.inputValue)
        outputValue = findViewById(R.id.outputValue)
        sourceUnitSpinner = findViewById(R.id.sourceUnitSpinner)
        targetUnitSpinner = findViewById(R.id.targetUnitSpinner)

        // Настройка адаптеров для выпадающих списков
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            units
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        sourceUnitSpinner.adapter = adapter
        targetUnitSpinner.adapter = adapter

        // Установка значений по умолчанию
        sourceUnitSpinner.setSelection(0)  // Метры
        targetUnitSpinner.setSelection(1)  // Километры

        // Настройка слушателей изменений
        setupListeners()

        // Первоначальный расчет
        convert()
    }

    private fun setupListeners() {
        // Слушатель изменений текста
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = convert()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Общий слушатель для обоих спиннеров
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = convert()
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        sourceUnitSpinner.onItemSelectedListener = spinnerListener
        targetUnitSpinner.onItemSelectedListener = spinnerListener
    }

    private fun convert() {
        try {
            val inputText = inputValue.text.toString()
            if (inputText.isBlank()) {
                outputValue.text = "0.0"
                return
            }

            val input = inputText.toDouble()
            val fromUnit = sourceUnitSpinner.selectedItem.toString()
            val toUnit = targetUnitSpinner.selectedItem.toString()

            // Конвертируем через метры (базовая единица)
            val metersValue = input * conversionRates[fromUnit]!!
            val result = metersValue / conversionRates[toUnit]!!

            // Форматируем вывод
            outputValue.text = formatResult(result)
        } catch (e: NumberFormatException) {
            outputValue.text = "Ошибка"
        } catch (e: Exception) {
            outputValue.text = "0.0"
        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toInt().toString()
        } else {
            // Оставляем до 6 знаков после запятой и убираем лишние нули
            "%.6f".format(value).trimEnd('0').trimEnd('.')
        }
    }
}