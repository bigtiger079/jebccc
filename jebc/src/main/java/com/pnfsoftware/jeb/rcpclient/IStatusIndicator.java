package com.pnfsoftware.jeb.rcpclient;

import org.eclipse.jface.action.StatusLineContributionItem;

public interface IStatusIndicator {
    public abstract StatusLineContributionItem getContribution(String paramString);

    public abstract boolean hasContribution(String paramString);

    public abstract void addContribution(StatusLineContributionItem paramStatusLineContributionItem);

    public abstract boolean removeContribution(StatusLineContributionItem paramStatusLineContributionItem);

    public abstract boolean removeContribution(String paramString);

    public abstract void clearAdditionalContributions();

    public abstract StatusIndicatorData save();

    public abstract boolean restore(StatusIndicatorData paramStatusIndicatorData);

    public abstract void clear();

    public abstract void setText(String paramString);

    public abstract String getText();

    public abstract void setText(int paramInt, String paramString);

    public abstract String getText(int paramInt);
}


