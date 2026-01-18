package com.trelix.trelix_app.service.search;

public abstract class BaseSearchProvider implements SearchProvider {

    protected double calculateRelevance(String title, String description, String query) {
        double score = 0.0;
        String lowerQuery = query.toLowerCase();
        String lowerTitle = title != null ? title.toLowerCase() : "";
        String lowerDesc = description != null ? description.toLowerCase() : "";
        if (lowerTitle.equals(lowerQuery))
            score += 100.0;
        else if (lowerTitle.startsWith(lowerQuery))
            score += 50.0;
        else if (lowerTitle.contains(lowerQuery))
            score += 25.0;
        if (lowerDesc.contains(lowerQuery))
            score += 10.0;
        score += (100.0 / (title != null ? title.length() + 1 : 1));
        return score;
    }

    protected String generateSnippet(String title, String description, String query) {
        String text = description != null ? description : title;
        if (text == null || text.isEmpty())
            return "";
        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int index = lowerText.indexOf(lowerQuery);
        if (index == -1)
            return text.length() > 100 ? text.substring(0, 100) + "..." : text;
        int start = Math.max(0, index - 50);
        int end = Math.min(text.length(), index + query.length() + 50);
        String snippet = text.substring(start, end);
        if (start > 0)
            snippet = "..." + snippet;
        if (end < text.length())
            snippet = snippet + "...";
        return snippet;
    }
}
