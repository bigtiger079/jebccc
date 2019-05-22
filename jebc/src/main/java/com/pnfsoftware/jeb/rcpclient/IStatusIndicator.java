package com.pnfsoftware.jeb.rcpclient;

import org.eclipse.jface.action.StatusLineContributionItem;

public interface IStatusIndicator {
    StatusLineContributionItem getContribution(String paramString);

    boolean hasContribution(String paramString);

    void addContribution(StatusLineContributionItem paramStatusLineContributionItem);

    boolean removeContribution(StatusLineContributionItem paramStatusLineContributionItem);

    boolean removeContribution(String paramString);

    void clearAdditionalContributions();

    StatusIndicatorData save();

    boolean restore(StatusIndicatorData paramStatusIndicatorData);

    void clear();

    void setText(String paramString);

    String getText();

    void setText(int paramInt, String paramString);

    String getText(int paramInt);
}


