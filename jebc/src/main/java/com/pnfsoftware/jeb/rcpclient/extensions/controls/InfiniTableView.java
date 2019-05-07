
package com.pnfsoftware.jeb.rcpclient.extensions.controls;


import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class InfiniTableView
        extends Composite {
    private static final ILogger logger = GlobalLog.getLogger(InfiniTableView.class);

    private Table table;
    private IOutOfRangeHelper oorHandler;
    private int currentSelection = -1;


    public InfiniTableView(Composite parent, int style, String[] columnNames) {

        super(parent, style);


        setLayout(new GridLayout());


        this.table = new Table(this, 0x10010 | style);

        this.table.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));

        this.table.setHeaderVisible(true);

        this.table.setLinesVisible(true);


        for (String name : columnNames) {

            buildColumn(this.table, name, 100);

        }


        this.table.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                if (InfiniTableView.this.currentSelection == -1) {

                    InfiniTableView.this.currentSelection = InfiniTableView.this.table.getSelectionIndex();

                    return;

                }


                boolean syncChange = true;


                int prevSel = InfiniTableView.this.currentSelection;

                int sel = InfiniTableView.this.table.getSelectionIndex();

                int topIndex = InfiniTableView.this.table.getTopIndex();


                int visi = InfiniTableView.this.getMaximumVisibleRowCount(true);

                if ((prevSel < topIndex) || (prevSel > topIndex + visi)) {

                    InfiniTableView.this.oorHandler.onResetRange(topIndex);

                    sel -= topIndex;

                    InfiniTableView.this.table.setSelection(sel);

                }


                InfiniTableView.this.currentSelection = sel;


            }


        });

        this.table.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                int selIndex = InfiniTableView.this.table.getSelectionIndex();


                if (e.keyCode == 16777217) {

                    if (selIndex == -1) {

                        InfiniTableView.this.table.setSelection(0);

                        selIndex = 0;

                    }


                    if (selIndex == 0) {

                        InfiniTableView.this.oorHandler.onRequestOutOfRange(-1, 0);

                        e.doit = false;

                    }

                } else if (e.keyCode == 16777218) {

                    if (selIndex == InfiniTableView.this.table.getItemCount() - 1) {

                        InfiniTableView.this.oorHandler.onRequestOutOfRange(1, 0);

                        e.doit = false;

                    }

                } else if (e.keyCode == 16777221) {


                    e.doit = false;

                } else if (e.keyCode == 16777222) {


                    e.doit = false;

                } else if (e.keyCode == 16777223) {

                    e.doit = false;

                } else if (e.keyCode == 16777224) {

                    e.doit = false;

                }


            }

        });

        this.table.addMouseWheelListener(new MouseWheelListener() {

            public void mouseScrolled(MouseEvent e) {

                int topIndex = InfiniTableView.this.table.getTopIndex();

                int rowCount = InfiniTableView.this.table.getItemCount();

                int visi = InfiniTableView.this.getMaximumVisibleRowCount(true);

                int newTopIndex = topIndex - e.count;

                if ((newTopIndex < 0) || (newTopIndex >= rowCount - visi)) {

                    InfiniTableView.this.oorHandler.onRequestOutOfRange(0, newTopIndex - topIndex);

                } else {

                    InfiniTableView.this.table.setTopIndex(newTopIndex);

                }

                InfiniTableView.this.currentSelection = InfiniTableView.this.table.getSelectionIndex();

            }

        });

    }


    public void addControlListener(ControlListener listener) {

        this.table.addControlListener(listener);

    }


    public void removeControlListener(ControlListener listener) {

        this.table.removeControlListener(listener);

    }


    public int getMaximumVisibleRowCount(boolean includePartialRow) {

        int height = this.table.getClientArea().height;

        if (height == 0) {

            return 0;

        }

        int H = height - this.table.getHeaderHeight();

        int h = this.table.getItemHeight();

        int cnt = H / h;

        if ((includePartialRow) && (H % h != 0)) {

            cnt++;

        }

        return cnt;

    }


    public void setRequestOutOfRangeHandler(IOutOfRangeHelper oorHandler) {

        this.oorHandler = oorHandler;

    }


    public Table getTable() {

        return this.table;

    }


    protected void buildColumn(Table parent, String name, int initialWidth) {

        TableColumn tc = new TableColumn(parent, 16384);

        tc.setText(name);

        tc.setResizable(true);

        tc.setMoveable(true);

        if (initialWidth > 0) {

            tc.setWidth(initialWidth);

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\InfiniTableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */