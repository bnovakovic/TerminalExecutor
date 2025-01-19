package com.bojan.terminalexecutor.swing

import com.bojan.terminalexecutor.constants.JSON_EXTENSION
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView

/**
 * File save window.
 *
 * @param title Title to display on top of the window.
 * @param currentDir Default directory to use.
 * @param initialFileName Default filename to use.
 * @param onFileConfirm The callback invoked when user has pressed save button.
 * @param overwriteMessage In case file exist, we show this message.
 * @param overwriteTitle In case overwrite message is shown, window has this title.
 */
fun saveFileSwingChooser(
    title: String,
    currentDir: File = File(""),
    initialFileName: String,
    onFileConfirm: (File) -> Unit,
    fileNameExtensionFilter: FileNameExtensionFilter,
    overwriteMessage: String,
    overwriteTitle: String,
) {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)

    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    fileChooser.currentDirectory = currentDir
    fileChooser.fileFilter = fileNameExtensionFilter
    fileChooser.selectedFile = File(initialFileName)

    val result = fileChooser.showSaveDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFileStringWithExtension = "${fileChooser.selectedFile}.json"
        val selectedFile = fileChooser.selectedFile
        val selectedFileWithExtension = File(selectedFileStringWithExtension)
        if (selectedFile.exists() || selectedFileWithExtension.exists()) {
            val confirmResult = JOptionPane.showConfirmDialog(
                null,
                overwriteMessage,
                overwriteTitle,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )
            if (confirmResult == JOptionPane.NO_OPTION) {
                return
            } else if (confirmResult == JOptionPane.YES_OPTION) {
                onFileConfirm(fileChooser.selectedFile)
            }
        } else {
            onFileConfirm(fileChooser.selectedFile)
        }
    }
}

/**
 * File open window.
 *
 * @param title Title to display on top of the window.
 * @param currentDir Default directory to use.
 * @param initialFileName Default filename to use.
 * @param onFileConfirm The callback invoked when user has pressed save button.
 * @param fileDoesNotExistMessage In case file exist, we show this message.
 * @param fileDoesNotExistTitle In case overwrite message is shown, window has this title.
 */
fun openFileSwingChooser(
    title: String,
    currentDir: File = File(""),
    initialFileName: String,
    onFileConfirm: (File) -> Unit,
    fileNameExtensionFilter: FileNameExtensionFilter,
    fileDoesNotExistMessage: String,
    fileDoesNotExistTitle: String,
) {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)

    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    fileChooser.currentDirectory = currentDir
    fileChooser.fileFilter = fileNameExtensionFilter
    fileChooser.selectedFile = File(initialFileName)

    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFileStringWithExtension = "${fileChooser.selectedFile}.$JSON_EXTENSION"
        val selectedFile = fileChooser.selectedFile
        val selectedFileWithExtension = File(selectedFileStringWithExtension)
        if (!selectedFile.exists() && !selectedFileWithExtension.exists()) {
            JOptionPane.showMessageDialog(
                null,
                fileDoesNotExistMessage,
                fileDoesNotExistTitle,
                JOptionPane.OK_OPTION
            )
        } else {
            if (selectedFile.exists()) {
                onFileConfirm(selectedFile)
            } else if (!selectedFile.exists() && selectedFileWithExtension.exists()) {
                onFileConfirm(selectedFileWithExtension)
            }
        }
    }
}

/**
 * Just a regular Folder chooser.
 *
 * @param title Title to show on top of the window.
 * @param currentDir Default directory to use.
 * @param onFolderSelected The callback invoked when folder is selected.
 */
fun folderSwingChooser(title: String, currentDir: File = File(""), onFolderSelected: (File) -> Unit) {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    fileChooser.currentDirectory = currentDir
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        onFolderSelected(fileChooser.selectedFile)
    }
}