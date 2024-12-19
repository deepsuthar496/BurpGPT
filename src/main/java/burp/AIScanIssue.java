package burp;

import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import burp.api.montoya.scanner.audit.issues.AuditIssueConfidence;
import burp.api.montoya.scanner.audit.issues.AuditIssueDefinition;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.collaborator.Interaction;
import java.util.List;
import java.util.Collections;

public class AIScanIssue implements AuditIssue {
    private final HttpRequestResponse requestResponse;
    private final String name;
    private final String detail;
    private final String severityLevel;

    public AIScanIssue(HttpRequestResponse requestResponse, String name, String detail, String severityLevel) {
        this.requestResponse = requestResponse;
        this.name = name;
        this.detail = detail;
        this.severityLevel = severityLevel;
    }

    @Override
    public AuditIssueDefinition definition() {
        return AuditIssueDefinition.auditIssueDefinition(
            name,
            "AI Scanner Issue",
            "Security",
            severity()
        );
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public AuditIssueSeverity severity() {
        return switch (severityLevel.toUpperCase()) {
            case "HIGH" -> AuditIssueSeverity.HIGH;
            case "MEDIUM" -> AuditIssueSeverity.MEDIUM;
            case "LOW" -> AuditIssueSeverity.LOW;
            default -> AuditIssueSeverity.INFORMATION;
        };
    }

    @Override
    public AuditIssueConfidence confidence() {
        return AuditIssueConfidence.TENTATIVE;
    }

    @Override
    public String remediation() {
        return "Review the AI-generated security analysis and take appropriate action.";
    }

    @Override
    public List<HttpRequestResponse> requestResponses() {
        return Collections.singletonList(requestResponse);
    }

    @Override
    public List<Interaction> collaboratorInteractions() {
        return Collections.emptyList();
    }

    @Override
    public String baseUrl() {
        return requestResponse.request().url();
    }

    @Override
    public HttpService httpService() {
        return requestResponse.httpService();
    }
}