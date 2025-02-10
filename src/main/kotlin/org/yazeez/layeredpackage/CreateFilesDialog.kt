package org.yazeez.layeredpackage

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JLabel
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class CreateFilesDialog : DialogWrapper(true) {
    private val featureNameField = JTextField()
    private val createProviderCheckBox = JCheckBox("Create Provider Layer?", true)
    private val createUICheckBox = JCheckBox("Create UI Layer?", true)

    init {
        title = "Generate Files"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        panel.add(JLabel("Enter the feature name:"), BorderLayout.NORTH)
        panel.add(featureNameField, BorderLayout.CENTER)

        // Add checkboxes for different layers
        val checkboxPanel = JPanel()
        checkboxPanel.layout = BorderLayout()
        checkboxPanel.add(createProviderCheckBox, BorderLayout.NORTH)
        checkboxPanel.add(createUICheckBox, BorderLayout.CENTER)

        panel.add(checkboxPanel, BorderLayout.SOUTH)


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

    fun shouldCreateProviderLayer(): Boolean {
        return createProviderCheckBox.isSelected
    }

    fun shouldCreateUILayer(): Boolean {
        return createUICheckBox.isSelected
    }
}