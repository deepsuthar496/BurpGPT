package burp;

import burp.api.montoya.MontoyaApi;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AISettings extends JPanel {
    private final MontoyaApi api;
    private final AIApiClient aiClient;
    private final JComboBox<String> modelSelector;
    private final JTextArea systemPrompt;
    private static final Map<String, String> MODEL_DESCRIPTIONS = new HashMap<>();
    public static String selectedModel = "evil"; // Default model
    public static String customSystemPrompt = "You are an expert security analyst skilled in web application security testing...";

    static {
        MODEL_DESCRIPTIONS.put("openai", "OpenAI GPT-4o (Censored)");
        MODEL_DESCRIPTIONS.put("mistral", "Mistral Nemo");
        MODEL_DESCRIPTIONS.put("mistral-large", "Mistral Large (v2)");
        MODEL_DESCRIPTIONS.put("llama", "Llama 3.1 (Censored)");
        MODEL_DESCRIPTIONS.put("command-r", "Command-R");
        MODEL_DESCRIPTIONS.put("unity", "Unity with Mistral Large");
        MODEL_DESCRIPTIONS.put("midijourney", "Midijourney musical transformer (Censored)");
        MODEL_DESCRIPTIONS.put("rtist", "Rtist image generator (Censored)");
        MODEL_DESCRIPTIONS.put("searchgpt", "SearchGPT with realtime search (Censored)");
        MODEL_DESCRIPTIONS.put("evil", "Evil Mode - Experimental");
        MODEL_DESCRIPTIONS.put("qwen-coder", "Qwen Coder 32b Instruct (Censored)");
        MODEL_DESCRIPTIONS.put("p1", "Pollinations 1 (OptiLLM)");
    }

    public AISettings(MontoyaApi api, AIApiClient aiClient) {
        this.api = api;
        this.aiClient = aiClient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Model selection panel
        JPanel modelPanel = new JPanel(new BorderLayout(5, 5));
        modelPanel.setBorder(BorderFactory.createTitledBorder("AI Model Selection"));
        
        modelSelector = new JComboBox<>(MODEL_DESCRIPTIONS.keySet().toArray(new String[0]));
        modelSelector.setSelectedItem(selectedModel);
        modelSelector.addActionListener(e -> selectedModel = (String) modelSelector.getSelectedItem());
        
        JTextArea modelDescription = new JTextArea();
        modelDescription.setEditable(false);
        modelDescription.setWrapStyleWord(true);
        modelDescription.setLineWrap(true);
        modelDescription.setRows(2);
        modelDescription.setBackground(getBackground());
        
        modelSelector.addActionListener(e -> {
            String selected = (String) modelSelector.getSelectedItem();
            modelDescription.setText(MODEL_DESCRIPTIONS.get(selected));
        });
        
        modelPanel.add(new JLabel("Select Model:"), BorderLayout.NORTH);
        modelPanel.add(modelSelector, BorderLayout.CENTER);
        modelPanel.add(modelDescription, BorderLayout.SOUTH);

        // System prompt panel
        JPanel promptPanel = new JPanel(new BorderLayout(5, 5));
        promptPanel.setBorder(BorderFactory.createTitledBorder("System Prompt"));
        
        systemPrompt = new JTextArea(customSystemPrompt, 10, 50);
        systemPrompt.setWrapStyleWord(true);
        systemPrompt.setLineWrap(true);
        JScrollPane promptScroll = new JScrollPane(systemPrompt);
        
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            customSystemPrompt = systemPrompt.getText();
            JOptionPane.showMessageDialog(this, "Settings saved successfully!");
        });

        promptPanel.add(new JLabel("Customize System Prompt:"), BorderLayout.NORTH);
        promptPanel.add(promptScroll, BorderLayout.CENTER);
        promptPanel.add(saveButton, BorderLayout.SOUTH);

        // Add panels to main settings
        add(modelPanel, BorderLayout.NORTH);
        add(promptPanel, BorderLayout.CENTER);
    }
}
