package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class L25SC extends JFrame {
    static class ScheduleItem {
        LocalDate date;
        String title;
        String memo;

        ScheduleItem(LocalDate date, String title, String memo) {
            this.date = date;
            this.title = title;
            this.memo = memo;
        }

        @Override
        public String toString() {
            return date + " | " + title;
        }
    }

    private final DefaultListModel<ScheduleItem> listModel = new DefaultListModel<>();
    private final JList<ScheduleItem> scheduleList = new JList<>(listModel);

    private final JTextField dateField = new JTextField();
    private final JTextField titleField = new JTextField();
    private final JTextArea memoArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Ready");

    private final File dataFile = new File("l25_schedule.csv");

    public L25SC() {
        super("BL");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedItem();
            }
        });

        JScrollPane listScroll = new JScrollPane(scheduleList);
        listScroll.setPreferredSize(new Dimension(340, 420));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Date (YYYY-MM-DD)"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(new JLabel("Title"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        form.add(new JLabel("Memo"), gbc);

        memoArea.setLineWrap(true);
        memoArea.setWrapStyleWord(true);
        JScrollPane memoScroll = new JScrollPane(memoArea);
        memoScroll.setPreferredSize(new Dimension(360, 180));

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(memoScroll, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");

        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
        clearButton.addActionListener(e -> clearInputs());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.add(form, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(listScroll, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);
        root.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(root);
        setSize(900, 520);
        setLocation(480, 180);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                saveToFile();
            }
        });

        loadFromFile();
        setVisible(true);
    }

    private void showSelectedItem() {
        ScheduleItem item = scheduleList.getSelectedValue();
        if (item == null) {
            return;
        }
        dateField.setText(item.date.toString());
        titleField.setText(item.title);
        memoArea.setText(item.memo);
        statusLabel.setText("Selected: " + item);
    }

    private ScheduleItem readInput() {
        String dateText = dateField.getText().trim();
        String title = titleField.getText().trim();
        String memo = memoArea.getText().trim();

        if (dateText.isEmpty() || title.isEmpty()) {
            statusLabel.setText("Date and title are required.");
            return null;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            statusLabel.setText("Invalid date format. Use YYYY-MM-DD.");
            return null;
        }

        return new ScheduleItem(date, title, memo);
    }

    private void addItem() {
        ScheduleItem item = readInput();
        if (item == null) {
            return;
        }
        listModel.addElement(item);
        sortListModel();
        statusLabel.setText("Added.");
    }

    private void updateItem() {
        int idx = scheduleList.getSelectedIndex();
        if (idx < 0) {
            statusLabel.setText("Select an item to update.");
            return;
        }

        ScheduleItem item = readInput();
        if (item == null) {
            return;
        }

        listModel.set(idx, item);
        sortListModel();
        statusLabel.setText("Updated.");
    }

    private void deleteItem() {
        int idx = scheduleList.getSelectedIndex();
        if (idx < 0) {
            statusLabel.setText("Select an item to delete.");
            return;
        }

        listModel.remove(idx);
        clearInputs();
        statusLabel.setText("Deleted.");
    }

    private void clearInputs() {
        scheduleList.clearSelection();
        dateField.setText("");
        titleField.setText("");
        memoArea.setText("");
        statusLabel.setText("Input cleared.");
    }

    private void sortListModel() {
        List<ScheduleItem> items = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            items.add(listModel.get(i));
        }
        items.sort((a, b) -> {
            int d = a.date.compareTo(b.date);
            if (d != 0) {
                return d;
            }
            return a.title.compareToIgnoreCase(b.title);
        });

        listModel.clear();
        for (ScheduleItem item : items) {
            listModel.addElement(item);
        }
    }

    private String escapeCsv(String s) {
        return s.replace("\\", "\\\\").replace("\n", "\\n").replace(",", "\\,");
    }

    private String unescapeCsv(String s) {
        StringBuilder out = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (escaping) {
                if (ch == 'n') {
                    out.append('\n');
                } else {
                    out.append(ch);
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                out.append(ch);
            }
        }
        if (escaping) {
            out.append('\\');
        }
        return out.toString();
    }

    private List<String> splitEscapedCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaping) {
                cur.append('\\').append(ch);
                escaping = false;
                continue;
            }
            if (ch == '\\') {
                escaping = true;
                continue;
            }
            if (ch == ',') {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        parts.add(cur.toString());
        return parts;
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (int i = 0; i < listModel.size(); i++) {
                ScheduleItem item = listModel.get(i);
                writer.write(item.date + "," + escapeCsv(item.title) + "," + escapeCsv(item.memo));
                writer.newLine();
            }
            statusLabel.setText("Saved: " + dataFile.getName());
        } catch (IOException ex) {
            statusLabel.setText("Save failed: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        listModel.clear();
        if (!dataFile.exists()) {
            statusLabel.setText("No save file. Start with empty list.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                List<String> parts = splitEscapedCsvLine(line);
                if (parts.size() < 3) {
                    continue;
                }
                LocalDate date = LocalDate.parse(parts.get(0));
                String title = unescapeCsv(parts.get(1));
                String memo = unescapeCsv(parts.get(2));
                listModel.addElement(new ScheduleItem(date, title, memo));
            }
            sortListModel();
            statusLabel.setText("Loaded: " + dataFile.getName());
        } catch (Exception ex) {
            statusLabel.setText("Load failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(L25SC::new);
    }
}