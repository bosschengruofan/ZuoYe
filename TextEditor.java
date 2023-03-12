import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TextEditor extends JFrame implements ActionListener {
    private JTextArea textArea;
    private Clipboard clipboard;
    private String filePath;

    public TextEditor() {
        super("文本剪辑器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        JMenuItem openItem = new JMenuItem("打开");
        openItem.addActionListener(this);
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.addActionListener(this);
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("编辑");
        menuBar.add(editMenu);

        JMenuItem cutItem = new JMenuItem("剪切");
        cutItem.addActionListener(this);
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("复制");
        copyItem.addActionListener(this);
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("粘贴");
        pasteItem.addActionListener(this);
        editMenu.add(pasteItem);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("就绪");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "打开":
                open();
                break;
            case "保存":
                save();
                break;
            case "退出":
                System.exit(0);
                break;
            case "剪切":
                cut();
                break;
            case "复制":
                copy();
                break;
            case "粘贴":
                paste();
                break;
        }
    }

    private void open() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                textArea.setText(builder.toString());
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                updateStatus("打开文件：" + filePath);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "无法打开文件：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void save() {
        if (filePath == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存文件");
            fileChooser.setSelectedFile(new File("未命名文件"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                filePath = file.getAbsolutePath();
                updateStatus("保存文件：" + filePath);

            } else {
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(textArea.getText());
            writer.flush();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "无法保存文件：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cut() {
        StringSelection selection = new StringSelection(textArea.getSelectedText());
        clipboard.setContents(selection, selection);
        textArea.replaceSelection("");
    }

    private void copy() {
        StringSelection selection = new StringSelection(textArea.getSelectedText());
        clipboard.setContents(selection, selection);
    }

    private void paste() {
        Transferable clipboardContent = clipboard.getContents(this);
        if (clipboardContent != null && clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String content = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
                textArea.insert(content, textArea.getCaretPosition());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "无法粘贴：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStatus(String message) {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                Component[] subComponents = ((JPanel) component).getComponents();
                for (Component subComponent : subComponents) {
                    if (subComponent instanceof JLabel) {
                        ((JLabel) subComponent).setText(message);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        editor.setVisible(true);
    }
}

