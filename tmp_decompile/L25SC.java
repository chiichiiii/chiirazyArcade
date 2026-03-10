package com.smu8.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class L25SC extends JFrame {
   private final DefaultListModel listModel = new DefaultListModel();
   private final JList scheduleList;
   private final JTextField dateField;
   private final JTextField titleField;
   private final JTextArea memoArea;
   private final JLabel statusLabel;
   private final File dataFile;

   public L25SC() {
      super("BL");
      this.scheduleList = new JList(this.listModel);
      this.dateField = new JTextField();
      this.titleField = new JTextField();
      this.memoArea = new JTextArea();
      this.statusLabel = new JLabel("Ready");
      this.dataFile = new File("l25_schedule.csv");
      this.setDefaultCloseOperation(2);
      this.scheduleList.setSelectionMode(0);
      this.scheduleList.addListSelectionListener((e) -> {
         if (!e.getValueIsAdjusting()) {
            this.showSelectedItem();
         }

      });
      JScrollPane listScroll = new JScrollPane(this.scheduleList);
      listScroll.setPreferredSize(new Dimension(340, 420));
      JPanel form = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(6, 6, 6, 6);
      gbc.fill = 2;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = (double)0.0F;
      form.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
      gbc.gridx = 1;
      gbc.weightx = (double)1.0F;
      form.add(this.dateField, gbc);
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.weightx = (double)0.0F;
      form.add(new JLabel("Title"), gbc);
      gbc.gridx = 1;
      gbc.weightx = (double)1.0F;
      form.add(this.titleField, gbc);
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.weightx = (double)0.0F;
      gbc.anchor = 11;
      form.add(new JLabel("Memo"), gbc);
      this.memoArea.setLineWrap(true);
      this.memoArea.setWrapStyleWord(true);
      JScrollPane memoScroll = new JScrollPane(this.memoArea);
      memoScroll.setPreferredSize(new Dimension(360, 180));
      gbc.gridx = 1;
      gbc.weightx = (double)1.0F;
      gbc.weighty = (double)1.0F;
      gbc.fill = 1;
      form.add(memoScroll, gbc);
      JPanel buttonPanel = new JPanel(new FlowLayout(0, 8, 0));
      JButton addButton = new JButton("Add");
      JButton updateButton = new JButton("Update");
      JButton deleteButton = new JButton("Delete");
      JButton clearButton = new JButton("Clear");
      JButton saveButton = new JButton("Save");
      JButton loadButton = new JButton("Load");
      addButton.addActionListener((e) -> this.addItem());
      updateButton.addActionListener((e) -> this.updateItem());
      deleteButton.addActionListener((e) -> this.deleteItem());
      clearButton.addActionListener((e) -> this.clearInputs());
      saveButton.addActionListener((e) -> this.saveToFile());
      loadButton.addActionListener((e) -> this.loadFromFile());
      buttonPanel.add(addButton);
      buttonPanel.add(updateButton);
      buttonPanel.add(deleteButton);
      buttonPanel.add(clearButton);
      buttonPanel.add(saveButton);
      buttonPanel.add(loadButton);
      JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
      rightPanel.add(form, "Center");
      rightPanel.add(buttonPanel, "South");
      JPanel root = new JPanel(new BorderLayout(10, 10));
      root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      root.add(listScroll, "West");
      root.add(rightPanel, "Center");
      root.add(this.statusLabel, "South");
      this.setContentPane(root);
      this.setSize(900, 520);
      this.setLocation(480, 180);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent e) {
            L25SC.this.saveToFile();
         }
      });
      this.loadFromFile();
      this.setVisible(true);
   }

   private void showSelectedItem() {
      ScheduleItem item = (ScheduleItem)this.scheduleList.getSelectedValue();
      if (item != null) {
         this.dateField.setText(item.date.toString());
         this.titleField.setText(item.title);
         this.memoArea.setText(item.memo);
         this.statusLabel.setText("Selected: " + String.valueOf(item));
      }
   }

   private ScheduleItem readInput() {
      String dateText = this.dateField.getText().trim();
      String title = this.titleField.getText().trim();
      String memo = this.memoArea.getText().trim();
      if (!dateText.isEmpty() && !title.isEmpty()) {
         LocalDate date;
         try {
            date = LocalDate.parse(dateText);
         } catch (DateTimeParseException var6) {
            this.statusLabel.setText("Invalid date format. Use YYYY-MM-DD.");
            return null;
         }

         return new ScheduleItem(date, title, memo);
      } else {
         this.statusLabel.setText("Date and title are required.");
         return null;
      }
   }

   private void addItem() {
      ScheduleItem item = this.readInput();
      if (item != null) {
         this.listModel.addElement(item);
         this.sortListModel();
         this.statusLabel.setText("Added.");
      }
   }

   private void updateItem() {
      int idx = this.scheduleList.getSelectedIndex();
      if (idx < 0) {
         this.statusLabel.setText("Select an item to update.");
      } else {
         ScheduleItem item = this.readInput();
         if (item != null) {
            this.listModel.set(idx, item);
            this.sortListModel();
            this.statusLabel.setText("Updated.");
         }
      }
   }

   private void deleteItem() {
      int idx = this.scheduleList.getSelectedIndex();
      if (idx < 0) {
         this.statusLabel.setText("Select an item to delete.");
      } else {
         this.listModel.remove(idx);
         this.clearInputs();
         this.statusLabel.setText("Deleted.");
      }
   }

   private void clearInputs() {
      this.scheduleList.clearSelection();
      this.dateField.setText("");
      this.titleField.setText("");
      this.memoArea.setText("");
      this.statusLabel.setText("Input cleared.");
   }

   private void sortListModel() {
      List<ScheduleItem> items = new ArrayList();

      for(int i = 0; i < this.listModel.size(); ++i) {
         items.add((ScheduleItem)this.listModel.get(i));
      }

      items.sort((a, b) -> {
         int d = a.date.compareTo(b.date);
         return d != 0 ? d : a.title.compareToIgnoreCase(b.title);
      });
      this.listModel.clear();

      for(ScheduleItem item : items) {
         this.listModel.addElement(item);
      }

   }

   private String escapeCsv(String s) {
      return s.replace("\\", "\\\\").replace("\n", "\\n").replace(",", "\\,");
   }

   private String unescapeCsv(String s) {
      StringBuilder out = new StringBuilder();
      boolean escaping = false;

      for(int i = 0; i < s.length(); ++i) {
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

   private List splitEscapedCsvLine(String line) {
      List<String> parts = new ArrayList();
      StringBuilder cur = new StringBuilder();
      boolean escaping = false;

      for(int i = 0; i < line.length(); ++i) {
         char ch = line.charAt(i);
         if (escaping) {
            cur.append('\\').append(ch);
            escaping = false;
         } else if (ch == '\\') {
            escaping = true;
         } else if (ch == ',') {
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
      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(this.dataFile));

         try {
            for(int i = 0; i < this.listModel.size(); ++i) {
               ScheduleItem item = (ScheduleItem)this.listModel.get(i);
               String var10001 = String.valueOf(item.date);
               writer.write(var10001 + "," + this.escapeCsv(item.title) + "," + this.escapeCsv(item.memo));
               writer.newLine();
            }

            this.statusLabel.setText("Saved: " + this.dataFile.getName());
         } catch (Throwable var5) {
            try {
               writer.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         writer.close();
      } catch (IOException ex) {
         this.statusLabel.setText("Save failed: " + ex.getMessage());
      }

   }

   private void loadFromFile() {
      this.listModel.clear();
      if (!this.dataFile.exists()) {
         this.statusLabel.setText("No save file. Start with empty list.");
      } else {
         try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));

            try {
               String line;
               while((line = reader.readLine()) != null) {
                  if (!line.isEmpty()) {
                     List<String> parts = this.splitEscapedCsvLine(line);
                     if (parts.size() >= 3) {
                        LocalDate date = LocalDate.parse((CharSequence)parts.get(0));
                        String title = this.unescapeCsv((String)parts.get(1));
                        String memo = this.unescapeCsv((String)parts.get(2));
                        this.listModel.addElement(new ScheduleItem(date, title, memo));
                     }
                  }
               }

               this.sortListModel();
               this.statusLabel.setText("Loaded: " + this.dataFile.getName());
            } catch (Throwable var8) {
               try {
                  reader.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            reader.close();
         } catch (Exception ex) {
            this.statusLabel.setText("Load failed: " + ex.getMessage());
         }

      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(L25SC::new);
   }

   static class ScheduleItem {
      LocalDate date;
      String title;
      String memo;

      ScheduleItem(LocalDate date, String title, String memo) {
         this.date = date;
         this.title = title;
         this.memo = memo;
      }

      public String toString() {
         String var10000 = String.valueOf(this.date);
         return var10000 + " | " + this.title;
      }
   }
}
