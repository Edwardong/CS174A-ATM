package bankteller;

import dao.TimeDao;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTimeUI extends JPanel implements ActionListener {
    private JPanel currentDays;

    private JLabel timeLabel;

    private JPanel timePanel;

    private JPanel confirmPanel;

    private JTextField timeField;

    private JButton confirmButton;

    private int currentAdd;

    public AddTimeUI(){
        // Get current add days
        currentAdd = TimeDao.getCurrentAddTime();
        currentDays = new JPanel();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String date = sdf.format(TimeDao.getCurrentTime());
        timeLabel = new JLabel("Time added : " + currentAdd + " days! And current time is : " + date);
        currentDays.add(timeLabel);

        timePanel = new JPanel();
        timeField = new JTextField(20);
        timePanel.add(new JLabel("Please input days that you continue want to add:"));
        timePanel.add(timeField);

        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        this.add(currentDays);
        this.add(timePanel);
        this.add(confirmPanel);

        this.setLayout(new GridLayout(3,1));
        this.setVisible(true);

        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm")){
            int add = Integer.valueOf(timeField.getText());
            TimeDao.addTime(add);
            currentAdd += add;
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String date = sdf.format(TimeDao.getCurrentTime());
            timeLabel.setText("Time added : " + currentAdd + " days! And current time is : " + date);
            timeLabel.repaint();
            timeLabel.revalidate();
            JOptionPane.showMessageDialog(this, "Time added!");
        }
    }
}
