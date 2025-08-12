
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ToDoApp extends JFrame {
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskInput, searchField;
    private File saveFile = new File("tasks.txt");

    public ToDoApp() {
        setTitle("Advanced To-Do List");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== TOP PANEL ======
        JPanel topPanel = new JPanel(new BorderLayout());

        taskInput = new JTextField();
        JButton addButton = new JButton("Add Task");

        topPanel.add(taskInput, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        // ====== SEARCH BAR ======
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setToolTipText("Search tasks...");
        searchPanel.add(new JLabel("🔍 Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // ====== TASK LIST ======
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);

        // ====== BUTTONS PANEL ======
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        JButton completeButton = new JButton("Mark Completed");
        JButton saveButton = new JButton("Save");

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(saveButton);

        // ====== LAYOUT ======
        add(topPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.AFTER_LAST_LINE);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ====== EVENTS ======
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        editButton.addActionListener(e -> editTask());
        completeButton.addActionListener(e -> markCompleted());
        saveButton.addActionListener(e -> saveTasks());
        taskInput.addActionListener(e -> addTask());

        // Search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTasks(); }
            public void removeUpdate(DocumentEvent e) { filterTasks(); }
            public void changedUpdate(DocumentEvent e) { filterTasks(); }
        });

        // Load saved tasks
        loadTasks();
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            String deadline = JOptionPane.showInputDialog(this, "Enter deadline (YYYY-MM-DD) or leave blank:");
            if (deadline != null && !deadline.isEmpty()) {
                task += " [Due: " + deadline + "]";
            }
            taskListModel.addElement(task);
            taskInput.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a task!");
        }
    }

    private void deleteTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            taskListModel.remove(index);
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to delete!");
        }
    }

    private void editTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            String current = taskListModel.getElementAt(index);
            String newTask = JOptionPane.showInputDialog(this, "Edit Task:", current);
            if (newTask != null && !newTask.trim().isEmpty()) {
                taskListModel.set(index, newTask);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to edit!");
        }
    }

    private void markCompleted() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            String task = taskListModel.getElementAt(index);
            if (!task.startsWith("✔ ")) {
                taskListModel.set(index, "✔ " + task);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to mark as completed!");
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            for (int i = 0; i < taskListModel.size(); i++) {
                writer.write(taskListModel.getElementAt(i));
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Tasks saved!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks!");
        }
    }

    private void loadTasks() {
        if (saveFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    taskListModel.addElement(line);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading tasks!");
            }
        }
    }

    private void filterTasks() {
        String search = searchField.getText().toLowerCase();
        DefaultListModel<String> filteredModel = new DefaultListModel<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(search)) {
                    filteredModel.addElement(line);
                }
            }
        } catch (IOException e) {
            // Ignore if file not found
        }

        if (search.isEmpty()) {
            loadTasks();
        } else {
            taskList.setModel(filteredModel);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoApp().setVisible(true);
        });
    }
}
