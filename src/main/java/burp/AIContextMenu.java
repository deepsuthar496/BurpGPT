package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import javax.swing.*;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AIContextMenu implements ContextMenuItemsProvider {
    private final MontoyaApi api;
    private final AIApiClient aiClient;

    public AIContextMenu(MontoyaApi api, AIApiClient aiClient) {
        this.api = api;
        this.aiClient = aiClient;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        if (event.isFromTool(ToolType.PROXY)) {
            List<Component> menuItems = new ArrayList<>();
            JMenuItem analyzeItem = new JMenuItem("Analyze with AI Scanner");
            
            analyzeItem.addActionListener(e -> {
                Optional<HttpRequestResponse> reqRes = event.messageEditorRequestResponse()
                    .map(editor -> HttpRequestResponse.httpRequestResponse(
                        editor.requestResponse().request(),
                        editor.requestResponse().response()
                    ));
                
                if (reqRes.isPresent()) {
                    HttpRequestResponse requestResponse = reqRes.get();
                    String analysis = aiClient.analyzeRequest(requestResponse);
                    
                    // Add the finding to Burp's dashboard
                    api.siteMap().add(new AIScanIssue(
                        requestResponse,
                        "AI Security Analysis",
                        analysis,
                        "HIGH"
                    ));
                    
                    // Show results in a popup
                    JOptionPane.showMessageDialog(null, 
                        "AI Analysis Results:\n\n" + analysis,
                        "AI Scanner Results",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            menuItems.add(analyzeItem);
            return menuItems;
        }
        
        return null;
    }
} 