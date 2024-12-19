package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ScanInfoUI extends JPanel {
    private final MontoyaApi api;
    private final AIApiClient aiClient;
    private final JTextPane scanResultsPane;
    private final JButton scanButton;

    public ScanInfoUI(MontoyaApi api, AIApiClient aiClient) {
        this.api = api;
        this.aiClient = aiClient;
        setLayout(new BorderLayout(10, 10));
        
        // Set preferred size for the panel
        setPreferredSize(new Dimension(800, 600));
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create scan controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scanButton = new JButton("Analyze Selected Request");
        scanButton.addActionListener(e -> analyzeSelectedRequest());
        controlsPanel.add(scanButton);

        // Create results area
        scanResultsPane = new JTextPane();
        scanResultsPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(scanResultsPane);
        
        // Add components
        add(controlsPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void analyzeSelectedRequest() {
        var history = api.proxy().history();
        if (!history.isEmpty()) {
            var selectedMessage = history.get(history.size() - 1);
            
            scanButton.setEnabled(false);
            scanResultsPane.setText("Analyzing request...");
            
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return aiClient.analyzeRequest(selectedMessage.finalRequest());
                }

                @Override
                protected void done() {
                    try {
                        String result = get();
                        scanResultsPane.setText("");
                        new AITabUI(api, aiClient).applyStyledText(scanResultsPane, result);
                    } catch (Exception ex) {
                        scanResultsPane.setText("Error analyzing request: " + ex.getMessage());
                    } finally {
                        scanButton.setEnabled(true);
                    }
                }
            };
            worker.execute();
        } else {
            scanResultsPane.setText("Please make some requests in Proxy to analyze them.");
        }
    }
}
