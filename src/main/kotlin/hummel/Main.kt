package hummel

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder

fun main() {
	EventQueue.invokeLater {
		try {
			for (info in UIManager.getInstalledLookAndFeels()) {
				if ("Windows Classic" == info.name) {
					UIManager.setLookAndFeel(info.className)
					break
				}
			}
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	private fun selectPath(pathField: JTextField, dir: Boolean) {
		val fileChooser = JFileChooser()
		if (dir) {
			fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
		}
		val result = fileChooser.showOpenDialog(this)
		if (result == JFileChooser.APPROVE_OPTION) {
			pathField.text = fileChooser.selectedFile.absolutePath
		}
	}

	private fun process(inputField: JTextField, outputField: JTextField) {
		if (inputField.text.isEmpty() || outputField.text.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Fill the fields", "Error", JOptionPane.ERROR_MESSAGE)
			return
		}

		val inputDirectory = File(inputField.text)
		val outputImage = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)

		var x = 0
		var y = 0

		inputDirectory.listFiles()?.sorted()?.forEachIndexed { index, file ->
			val image = ImageIO.read(file)
			outputImage.createGraphics().drawImage(image, x, y, null)

			x += 16
			if ((index + 1) % 16 == 0) {
				x = 0
				y += 16
			}
		}

		val outputFile = File(outputField.text)
		ImageIO.write(outputImage, "PNG", outputFile)
		JOptionPane.showMessageDialog(
			this, "Concatenation complete", "Message", JOptionPane.INFORMATION_MESSAGE
		)
	}

	init {
		title = "Image Concatenator"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(100, 100, 550, 180)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPanel.layout = GridLayout(0, 1, 0, 0)
		contentPane = contentPanel

		val inputPanel = JPanel()
		val inputLabel = JLabel("Input path:")
		inputLabel.preferredSize = Dimension(80, inputLabel.preferredSize.height)
		val inputField = JTextField(24)
		val inputButton = JButton("Select path")
		inputButton.addActionListener { selectPath(inputField, true) }
		inputPanel.add(inputLabel)
		inputPanel.add(inputField)
		inputPanel.add(inputButton)

		val outputPanel = JPanel()
		val outputLabel = JLabel("Output path:")
		outputLabel.preferredSize = Dimension(80, outputLabel.preferredSize.height)
		val outputField = JTextField(24)
		val outputButton = JButton("Select path")
		outputButton.addActionListener { selectPath(outputField, false) }
		outputPanel.add(outputLabel)
		outputPanel.add(outputField)
		outputPanel.add(outputButton)

		val processPanel = JPanel()
		val processButton = JButton("Process")
		processButton.addActionListener {
			process(inputField, outputField)
		}
		processPanel.add(processButton)

		contentPanel.add(inputPanel)
		contentPanel.add(outputPanel)
		contentPanel.add(processPanel)

		setLocationRelativeTo(null)
	}
}