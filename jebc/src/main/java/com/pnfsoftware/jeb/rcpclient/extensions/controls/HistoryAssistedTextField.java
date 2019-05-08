
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class HistoryAssistedTextField
        extends Composite {
    private static final ILogger logger = GlobalLog.getLogger(HistoryAssistedTextField.class);
    private Text text;
    private ContentProposalAdapter cpa;
    private TextHistory history;

    public HistoryAssistedTextField(Composite parent, String labelText, final TextHistory history, boolean displayHelpDecorator) {
        super(parent, 0);
        setLayout(new FillLayout());
        this.history = history;
        Composite ph = new Composite(this, 0);
        ph.setLayout(new GridLayout(2, false));
        Label label = new Label(ph, 0);
        label.setText(labelText);
        this.text = UIUtil.createTextboxInGrid(ph, 2052, 30, 1);
        ((GridData) this.text.getLayoutData()).horizontalIndent = 8;
        ((GridData) this.text.getLayoutData()).grabExcessHorizontalSpace = true;
        ((GridData) this.text.getLayoutData()).horizontalAlignment = 4;
        if (history != null) {
            Label hint2 = new Label(ph, 64);
            hint2.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
            hint2.setText("(Hit Control+Space to browse your input history)");
            if (displayHelpDecorator) {
                ControlDecoration deco = new ControlDecoration(this.text, 16512);
                Image image = FieldDecorationRegistry.getDefault().getFieldDecoration("DEC_INFORMATION").getImage();
                deco.setDescriptionText("Hit Control+Space to browse your input history");
                deco.setImage(image);
                deco.setShowOnlyOnFocus(true);
            }
            char[] autoActivationCharacters = new char[0];
            KeyStroke keyStroke = KeyStroke.getInstance(262144, 32);
            this.cpa = new ContentProposalAdapter(this.text, new TextContentAdapter(), new HistoryProposalProvider(history), keyStroke, autoActivationCharacters);
            this.cpa.setPopupSize(new Point(300, 200));
            this.cpa.setProposalAcceptanceStyle(2);
            this.text.addTraverseListener(new TraverseListener() {
                public void keyTraversed(TraverseEvent e) {
                    if (e.character == '\r') {
                        if (!HistoryAssistedTextField.this.cpa.isProposalPopupOpen()) {
                            history.record(HistoryAssistedTextField.this.text.getText());
                        }
                    }
                }
            });
        }
        this.text.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent e) {
            }
        });
        this.text.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if ((e.stateMask == SWT.MOD1) && (e.character == '\b')) {
                    HistoryAssistedTextField.this.text.setText("");
                }
            }
        });
    }

    class HistoryProposalProvider implements IContentProposalProvider {
        TextHistory hist;

        public HistoryProposalProvider(TextHistory history) {
            this.hist = history;
        }

        public IContentProposal[] getProposals(String contents, int position) {
            String template = "";
            if (HistoryAssistedTextField.this.text.getSelectionCount() != contents.length()) {
                template = contents;
            }
            List<IContentProposal> r = new ArrayList();
            for (String s : this.hist.getAll()) {
                if (s.contains(template)) {
                    ContentProposal proposal = new ContentProposal(s, s, null, s.length());
                    r.add(0, proposal);
                }
            }
            return (IContentProposal[]) r.toArray(new IContentProposal[r.size()]);
        }
    }

    static class OverwriteContentAdapter extends TextContentAdapter {
        public void insertControlContents(Control control, String contents, int cursorPosition) {
            ((Text) control).setText(contents);
            ((Text) control).setSelection(contents.length());
        }
    }

    public void confirm() {
        if (this.history != null) {
            if (!this.cpa.isProposalPopupOpen()) {
                String str = this.text.getText();
                if (!str.isEmpty()) {
                    this.history.record(this.text.getText());
                }
            }
        }
    }

    public Text getWidget() {
        return this.text;
    }

    public String getText() {
        return this.text.getText();
    }

    public void setText(String content) {
        this.text.setText(content);
    }

    public void selectAll() {
        this.text.selectAll();
    }
}


