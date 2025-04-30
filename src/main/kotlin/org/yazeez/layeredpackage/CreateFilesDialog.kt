package org.yazeez.layeredpackage

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.*

class CreateFilesDialog : DialogWrapper(true) {
    private val featureNameField = JTextField()
    private val providerRadioButton = JRadioButton("Provider", true)
    private val riverpodRadioButton = JRadioButton("Riverpod")
    private val noneRadioButton = JRadioButton("None")
    private val createUICheckBox = JCheckBox("Create UI Layer?", true)

    init {
        title = "Generate Files"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        panel.add(JLabel("Enter the feature name:"), BorderLayout.NORTH)
        panel.add(featureNameField, BorderLayout.CENTER)

        // Create button group for provider type selection
        val providerTypeGroup = ButtonGroup()
        providerTypeGroup.add(providerRadioButton)
        providerTypeGroup.add(riverpodRadioButton)
        providerTypeGroup.add(noneRadioButton)

        // Add radio buttons for provider type selection
        val providerTypePanel = JPanel()
        providerTypePanel.add(JLabel("State Management:"))
        providerTypePanel.add(providerRadioButton)
        providerTypePanel.add(riverpodRadioButton)
        providerTypePanel.add(noneRadioButton)

        // Add components to main panel
        val optionsPanel = JPanel(BorderLayout())
        optionsPanel.add(providerTypePanel, BorderLayout.NORTH)
        optionsPanel.add(createUICheckBox, BorderLayout.CENTER)

        panel.add(optionsPanel, BorderLayout.SOUTH)

        return panel
    }

    fun getFeatureName(): String {
        val input = featureNameField.text
        return if (isValidSnakeCase(input)) {
            input
        } else {
            toSnakeCase(input)
        }
    }

    private fun isValidSnakeCase(input: String): Boolean {
        val snakeCaseRegex = "^[a-z][a-z0-9_]*$".toRegex()
        return snakeCaseRegex.matches(input)
    }

    private fun toSnakeCase(input: String): String {
        return input
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .replace(Regex("[-\\s]"), "_")
            .lowercase()
    }

    fun getSelectedStateManagementType(): StateManagementType {
        return when {
            providerRadioButton.isSelected -> StateManagementType.PROVIDER
            riverpodRadioButton.isSelected -> StateManagementType.RIVERPOD
            else -> StateManagementType.NONE
        }
    }

    fun shouldCreateUILayer(): Boolean {
        return createUICheckBox.isSelected
    }
}