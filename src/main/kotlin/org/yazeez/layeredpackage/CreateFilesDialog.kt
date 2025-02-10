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

    init {
        title = "Generate Files"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        // Add input field for feature name
        panel.add(JLabel("Enter the feature name:"), BorderLayout.NORTH)
        panel.add(featureNameField, BorderLayout.CENTER)

        // Add checkbox for provider layer
        panel.add(createProviderCheckBox, BorderLayout.SOUTH)

        return panel
    }

    fun getFeatureName(): String {
        return featureNameField.text
    }

    fun shouldCreateProviderLayer(): Boolean {
        return createProviderCheckBox.isSelected
    }
}