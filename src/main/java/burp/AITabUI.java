package burp;

import burp.api.montoya.MontoyaApi;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AITabUI extends JPanel {
    private final MontoyaApi api;
    private final AIApiClient aiClient;
    private final JPanel chatPanel;
    private final JScrollPane scrollPane;
    private final JTextArea userInput;
    private final JButton sendButton;
    private final Color userBubbleColor = new Color(0, 132, 255);
    private final Color aiBubbleColor = new Color(64, 64, 64);

    private JPanel loadingMessagePanel;
    private Timer loadingTimer;
    private int dotCount = 0;

    public AITabUI(MontoyaApi api, AIApiClient aiClient) {
        this.api = api;
        this.aiClient = aiClient;
        setLayout(new BorderLayout());
        
        // Set preferred size for the panel
        setPreferredSize(new Dimension(800, 600));

        // Chat panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        // Scroll pane for chat
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // User input area
        userInput = new JTextArea(3, 50);
        userInput.setWrapStyleWord(true);
        userInput.setLineWrap(true);
        userInput.setFont(new Font("Arial", Font.PLAIN, 14));
        userInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    sendMessage();
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        JScrollPane inputScrollPane = new JScrollPane(userInput);

        // Send button
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        // Bottom panel for input and button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(inputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Add components to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Welcome message
        addAIMessage("Hello! I'm your AI security assistant. How can I help you analyze security issues today?");
    }

    private void addUserMessage(String message) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        // Remove fixed height constraint
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        messagePanel.setBackground(Color.WHITE);

        JTextPane textPane = createMessageBubble(message, userBubbleColor, Color.WHITE);
        JPanel bubblePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bubblePanel.setBackground(Color.WHITE);
        bubblePanel.add(textPane);
        
        messagePanel.add(bubblePanel, BorderLayout.CENTER);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 5));
        
        chatPanel.add(messagePanel);
        refreshChat();
    }

    private void addAIMessage(String message) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        // Remove fixed height constraint
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        messagePanel.setBackground(Color.WHITE);

        JTextPane textPane = createMessageBubble(message, aiBubbleColor, Color.WHITE);
        JPanel bubblePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bubblePanel.setBackground(Color.WHITE);
        bubblePanel.add(textPane);
        
        messagePanel.add(bubblePanel, BorderLayout.CENTER);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 50));
        
        chatPanel.add(messagePanel);
        refreshChat();
    }

    private JTextPane createMessageBubble(String message, Color backgroundColor, Color textColor) {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(backgroundColor);
        textPane.setForeground(textColor);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        textPane.setMargin(new Insets(10, 10, 10, 10));
        
        // Apply styled text
        applyStyledText(textPane, message);
        
        // Calculate width based on content
        int preferredWidth = Math.min(400, textPane.getPreferredSize().width);
        textPane.setSize(preferredWidth, Short.MAX_VALUE);
        int preferredHeight = textPane.getPreferredSize().height;
        
        textPane.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        
        return textPane;
    }

    void applyStyledText(JTextPane textPane, String message) {
        StyledDocument doc = textPane.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        // Create styles with better properties
        Style bold = textPane.addStyle("bold", defaultStyle);
        StyleConstants.setBold(bold, true);
        
        Style h1Style = textPane.addStyle("h1", defaultStyle);
        StyleConstants.setBold(h1Style, true);
        StyleConstants.setFontSize(h1Style, 20);
        StyleConstants.setSpaceAbove(h1Style, 20.0f);
        StyleConstants.setSpaceBelow(h1Style, 10.0f);
        
        Style h2Style = textPane.addStyle("h2", defaultStyle);
        StyleConstants.setBold(h2Style, true);
        StyleConstants.setFontSize(h2Style, 18);
        StyleConstants.setSpaceAbove(h2Style, 15.0f);
        StyleConstants.setSpaceBelow(h2Style, 8.0f);
        
        Style h3Style = textPane.addStyle("h3", defaultStyle);
        StyleConstants.setBold(h3Style, true);
        StyleConstants.setFontSize(h3Style, 16);
        StyleConstants.setSpaceAbove(h3Style, 10.0f);
        StyleConstants.setSpaceBelow(h3Style, 5.0f);

        Style listStyle = textPane.addStyle("list", defaultStyle);
        StyleConstants.setLeftIndent(listStyle, 20.0f);
        StyleConstants.setFirstLineIndent(listStyle, -10.0f);

        try {
            doc.remove(0, doc.getLength());
            String[] paragraphs = message.split("\n\n");
            
            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i].trim();
                String[] lines = paragraph.split("\n");
                
                for (String line : lines) {
                    // Handle headers with proper spacing
                    if (line.startsWith("### ")) {
                        doc.insertString(doc.getLength(), line.substring(4), h3Style);
                        doc.insertString(doc.getLength(), "\n", defaultStyle);
                    }
                    else if (line.startsWith("## ")) {
                        doc.insertString(doc.getLength(), line.substring(3), h2Style);
                        doc.insertString(doc.getLength(), "\n", defaultStyle);
                    }
                    else if (line.startsWith("# ")) {
                        doc.insertString(doc.getLength(), line.substring(2), h1Style);
                        doc.insertString(doc.getLength(), "\n", defaultStyle);
                    }
                    // Handle bullet points with proper indentation
                    else if (line.startsWith("- ")) {
                        doc.insertString(doc.getLength(), "• " + line.substring(2), listStyle);
                        doc.insertString(doc.getLength(), "\n", defaultStyle);
                    }
                    // Handle numbered lists
                    else if (line.matches("^\\d+\\.\\s.*")) {
                        doc.insertString(doc.getLength(), line, listStyle);
                        doc.insertString(doc.getLength(), "\n", defaultStyle);
                    }
                    // Handle bold text
                    else {
                        int lastIndex = 0;
                        int startBold = line.indexOf("**");
                        
                        while (startBold != -1) {
                            doc.insertString(doc.getLength(), line.substring(lastIndex, startBold), defaultStyle);
                            int endBold = line.indexOf("**", startBold + 2);
                            if (endBold == -1) break;
                            
                            doc.insertString(doc.getLength(), line.substring(startBold + 2, endBold), bold);
                            lastIndex = endBold + 2;
                            startBold = line.indexOf("**", lastIndex);
                        }
                        
                        if (lastIndex < line.length()) {
                            doc.insertString(doc.getLength(), line.substring(lastIndex), defaultStyle);
                        }
                        doc.insertString(doc.getLength(), "\n", defaultStyle);
                    }
                }
                
                // Add extra newline between paragraphs
                if (i < paragraphs.length - 1) {
                    doc.insertString(doc.getLength(), "\n", defaultStyle);
                }
            }
        } catch (BadLocationException e) {
            // Fallback to plain text
            try {
                doc.remove(0, doc.getLength());
                doc.insertString(0, message, defaultStyle);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void refreshChat() {
        chatPanel.revalidate();
        chatPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void showLoadingIndicator() {
        loadingMessagePanel = new JPanel(new BorderLayout());
        loadingMessagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        loadingMessagePanel.setBackground(Color.WHITE);

        JTextPane textPane = createMessageBubble("...", aiBubbleColor, Color.WHITE);
        JPanel bubblePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bubblePanel.setBackground(Color.WHITE);
        bubblePanel.add(textPane);
        
        loadingMessagePanel.add(bubblePanel, BorderLayout.CENTER);
        loadingMessagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 50));
        
        chatPanel.add(loadingMessagePanel);
        refreshChat();

        // Animate the dots
        loadingTimer = new Timer(500, e -> {
            dotCount = (dotCount + 1) % 4;
            String dots = ".".repeat(dotCount + 1);
            textPane.setText(dots);
            refreshChat();
        });
        loadingTimer.start();
    }

    private void removeLoadingIndicator() {
        if (loadingTimer != null) {
            loadingTimer.stop();
        }
        if (loadingMessagePanel != null) {
            chatPanel.remove(loadingMessagePanel);
            refreshChat();
        }
    }

    private void sendMessage() {
        String userMessage = userInput.getText().trim();
        if (!userMessage.isEmpty()) {
            addUserMessage(userMessage);
            userInput.setText("");

            // Show loading indicator
            showLoadingIndicator();

            // Use SwingWorker to handle AI response in background
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return aiClient.analyzeRequest(userMessage);
                }

                @Override
                protected void done() {
                    try {
                        removeLoadingIndicator();
                        String response = get();
                        addAIMessage(response);
                    } catch (Exception ex) {
                        removeLoadingIndicator();
                        addAIMessage("Sorry, there was an error processing your request.");
                    }
                }
            };
            worker.execute();
        }
    }
}