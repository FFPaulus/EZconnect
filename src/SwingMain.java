//This Source Code Form is subject to the terms of the Mozilla Public
//License, v. 2.0. If a copy of the MPL was not distributed with this
//file, You can obtain one at https://mozilla.org/MPL/2.0/.

import java.awt.BorderLayout;

import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class SwingMain extends JFrame implements ActionListener {

    // Constants
    private static String CONFIGPATH = "lib/webserver/settings.yml";
    private static String CREDENTIALPATH = "lib/webserver/storage/user.yml";
    private static String RUBYPATH = "lib/webserver/config.ru";

    // Basic settings variables
    private JTextField textField_Username;
    private JPasswordField passwordField;

    // Advanced settings variables
    private JTextField textField_port_website;
    private JTextField textField_port_websockify;
    private JTextField txtField_x11Args;
    private JComboBox<String> comboBox;

    // Created here to use vars outside of main
    private JTabbedPane tabbedPane;
    private JTextPane textPaneStatus;
    private JTextPane textPaneIp;

    // Variables for processes spawned later
    Process process_webserver = null;
    Process process_websockify = null;
    Process process_x11vnc = null;
    
    // Others
    private boolean isStarted = false; // Tells the program if subprocesses have been started

    // Launch the application.
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SwingMain frame = new SwingMain();
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Create the Frame and add ActionListeners to buttons
    public SwingMain() {
        // Define close behavior
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // We override this
        // Override the behavior on clicking x to execute custom exit() function
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exit();
            }
        });

        setBounds(200, 150, 450, 300); // Position and size of the created window

        // Level 0 : Tabbed Pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.NORTH);

        // Level 1: Tab 0
        JPanel panel_basic = new JPanel();
        tabbedPane.addTab("Basic Settings", null, panel_basic, null);
        panel_basic.setLayout(new GridLayout(0, 2, 0, 3));

        // Username
        JLabel lblUsername = new JLabel("Username");
        panel_basic.add(lblUsername);

        textField_Username = new JTextField();
        panel_basic.add(textField_Username);
        textField_Username.setText("admin");
        textField_Username.setColumns(10);

        // Password
        JLabel lblPassword = new JLabel("Password");
        panel_basic.add(lblPassword);

        passwordField = new JPasswordField();
        panel_basic.add(passwordField);
        
        // Platform selector
        JLabel lblPlatform = new JLabel("Platform");
        panel_basic.add(lblPlatform);

        String[] platforms = { "Linux", "Windows", "Mac" };
        comboBox = new JComboBox<String>(platforms);
        panel_basic.add(comboBox);

        // Level 1 : Tab 1
        JPanel panel_adv = new JPanel();
        tabbedPane.addTab("Advanced Settings", null, panel_adv, null);
        panel_adv.setLayout(new GridLayout(0, 2, 0, 3));

        // Port Webserver
        JLabel lblPort = new JLabel("Website Port");
        panel_adv.add(lblPort);

        textField_port_website = new JTextField();
        panel_adv.add(textField_port_website);
        textField_port_website.setText("9292");
        textField_port_website.setColumns(8);

        // Port Websockify
        JLabel lbl_port_websockify = new JLabel("Webockify Port");
        panel_adv.add(lbl_port_websockify);

        textField_port_websockify = new JTextField();
        panel_adv.add(textField_port_websockify);
        textField_port_websockify.setText("3456");
        textField_port_websockify.setColumns(10);

        // x11vnc arguments
        JLabel lbl_x11Args = new JLabel("x11vnc Arguments");
        panel_adv.add(lbl_x11Args);

        txtField_x11Args = new JTextField();
        panel_adv.add(txtField_x11Args);
        txtField_x11Args.setText("-forever -shared");
        txtField_x11Args.setColumns(10);

        // Reset button
        JButton btn_ResetSettings = new JButton("Reset Settings");
        panel_adv.add(btn_ResetSettings);
        btn_ResetSettings.setActionCommand("reset");
        btn_ResetSettings.addActionListener(this);

        // Level 0: Panel for bottom buttons
        JPanel panel_2 = new JPanel();
        getContentPane().add(panel_2, BorderLayout.SOUTH);
        panel_2.setLayout(new GridLayout(3, 2, 0, 0));

        // Start Button
        JButton btn_start = new JButton("Start");
        panel_2.add(btn_start);
        btn_start.setActionCommand("start");
        btn_start.addActionListener(this);

        // End Button
        JButton btn_cancel = new JButton("End");
        panel_2.add(btn_cancel);
        btn_cancel.setActionCommand("end");
        btn_cancel.addActionListener(this);

        // Status
        JLabel lblStatus = new JLabel("Status");
        panel_2.add(lblStatus);

        textPaneStatus = new JTextPane();
        textPaneStatus.setText("Ready");
        panel_2.add(textPaneStatus);
        
        // Ip Address
        JLabel lblConnectToenter = new JLabel("Connect to (enter in Browser)");
		panel_2.add(lblConnectToenter);
		
		textPaneIp = new JTextPane();
		panel_2.add(textPaneIp);
		btn_cancel.addActionListener(this);

        // Load configuration
        loadConfig();
    }

    // Define behavior of buttons
    public void actionPerformed(ActionEvent e) {
        // Start Button
        if ("start".equals(e.getActionCommand()) && !isStarted) {

            // Used to issue shell commands
            ProcessBuilder processBuilder = new ProcessBuilder();
            storeConfig();

            try {
                // Run the webserver
                processBuilder.command("bash", "-c", "rackup " + RUBYPATH);
                process_webserver = processBuilder.start();

                // Run x11vnc
                processBuilder.command("bash", "-c", "x11vnc " + txtField_x11Args.getText());
                process_x11vnc = processBuilder.start();

                // Run websockify
                processBuilder.command("bash", "-c",
                        "websockify " + textField_port_websockify.getText() + " 127.0.0.1:5900");
                process_websockify = processBuilder.start();

                changeStatus("Processes started.");
                getAndDisplayIpAddress();
            } catch (IOException e1) {
                stopProcesses();
                changeStatus("Could not start processes.");
            }
        }
        // End button
        else if ("end".equals(e.getActionCommand())) {
            exit();
        }
        // Reset Button
        else if ("reset".equals(e.getActionCommand())) {
            resetConfig();
        }
    }

    // Make sure spawned processes are killed on close and store the config
    public void exit() {
        stopProcesses();

        storeConfig();

        System.exit(0);
    }

    // Kill running processes
    public void stopProcesses() {
        // Check if processes have been spawned and kill if yes
        if (process_webserver != null)
            process_webserver.destroy();
        if (process_websockify != null)
            process_websockify.destroy();
        if (process_x11vnc != null)
            process_x11vnc.destroy();
    }

    // Reset values of variables and refresh the swing window
    public void resetConfig() {
        // Beautifully hardcoded default values
        textField_port_website.setText("9292");
        textField_port_websockify.setText("3456");
        passwordField.setText("");
        txtField_x11Args.setText("-forever -shared");

        tabbedPane.revalidate();
    }

    // Store variable values in file at CONFIGPATH
    public void storeConfig() {
        FileWriter fw;

        // Config
        try {
            // Create File / Open File and discard content
            fw = new FileWriter(CONFIGPATH, false);

            // Write variable content to file
            // Uses the format <variable_name>: <value>
            fw.write("port_website: " + textField_port_website.getText() + "\n");
            fw.write("port_websockify: " + textField_port_websockify.getText() + "\n");
            fw.write("x11_arguments: \"" + txtField_x11Args.getText() + "\"\n");

            // Don't forget to close the writer / file again
            fw.close();
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        // Credentials
        try {
            // Create File / Open File and discard content
            fw = new FileWriter(CREDENTIALPATH, false);

            // Write variable content to file
            // Uses the format <variable_name>: <value>
            fw.write("username: " + textField_Username.getText() + "\n");

            // Java wants us to use the try catch block here, but it is not really needed
            MessageDigest digest;
            try {
                // Hash using SHA-256
                digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(new String(passwordField.getPassword()).getBytes(StandardCharsets.UTF_8));

                // Use StringBuilder to convert bytes to hex
                StringBuilder sb = new StringBuilder();
                for (byte b : hash) {
                    sb.append(String.format("%02x", b));
                }

                // Finally store hashed pass
                fw.write("password: " + sb.toString() + "\n");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // Don't forget to close the writer / file again
            fw.close();
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Load config file located at CONFIGPATH
    public void loadConfig() {
        FileReader fr;
        BufferedReader br;
        try {
            // Open File and create a buffered reader to read lines instead of only
            // characters
            fr = new FileReader(CONFIGPATH);
            br = new BufferedReader(fr);

            // Read a line and repeat as long as we still read something
            String line = br.readLine();
            while (line != null) {
                // Split the line according to the format used in storeConfig()
                String[] split = line.split(": ");

                // Check which variable we got
                if ("port_website".equals(split[0])) {
                    textField_port_website.setText(split[1]);
                } else if ("port_websockify".equals(split[0])) {
                    textField_port_websockify.setText(split[1]);
                } else if ("x11_arguments".equals(split[0])) {
                    int length = split[1].length();
                    String text = split[1].substring(1, length - 1);
                    txtField_x11Args.setText(text);
                }

                // Read another line
                line = br.readLine();
            }

            // Don't forget to close the reader / file again
            br.close();
            fr.close();
        } catch (IOException e) {
            changeStatus("Error loading Config.");
            // Auto-generated catch block
            e.printStackTrace();
        }
        changeStatus("Configuration loaded.");
    }

    private void changeStatus(String str) {
        textPaneStatus.setText(str);
        tabbedPane.revalidate();
    }
    
    private void getAndDisplayIpAddress() {
    	String ip = "Something went wrong.";
    	try(final DatagramSocket socket = new DatagramSocket()){
    		  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
    		  ip = socket.getLocalAddress().getHostAddress();
    		  ip = ip + ":" + textField_port_website.getText();
    		} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	textPaneIp.setText(ip);
    }
}
