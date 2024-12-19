package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import javax.swing.*;
import java.awt.*;

public class BurpAIScanner implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("AI Security Assistant");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create main container
                JComponent mainComponent = createMainComponent(api);
                
                // Register as a suite tab
                api.userInterface().registerSuiteTab("AI Assistant", mainComponent);
                
                // Log successful initialization
                api.logging().logToOutput("AI Security Assistant initialized successfully");
            } catch (Exception e) {
                api.logging().logToError("Error initializing AI Security Assistant: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private JComponent createMainComponent(MontoyaApi api) {
        // Create an AI client instance
        AIApiClient aiClient = new AIApiClient(api);

        // Create tabs container
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(800, 600));

        // Create and add Chat tab
        AITabUI chatTab = new AITabUI(api, aiClient);
        tabbedPane.addTab("Chat", null, chatTab, "AI Chat Interface");

        // Create and add Scan Info tab
        ScanInfoUI scanTab = new ScanInfoUI(api, aiClient);
        tabbedPane.addTab("Scan Info", null, scanTab, "Request Analysis");

        // Create and add Settings tab
        AISettings settingsTab = new AISettings(api, aiClient);
        tabbedPane.addTab("AI Settings", null, settingsTab, "Configure AI Model and System Prompt");

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        return mainPanel;
    }
}