
package com.pnfsoftware.jeb.rcpclient.extensions.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


class DropZones {
//    private static final ILogger logger = ;

    Display display;

    Control topLevelContainer;
    boolean includeDockingAreas;
    boolean includeSelfDockingAreas;
    CTabItem srcTab;
    CTabFolder srcFolder;
    List<DropZone> dropzones = new ArrayList();


    public DropZones(Control topLevelContainer, CTabItem srcTab, boolean includeDockingAreas, boolean includeSelfDockingAreas) {

        this.display = topLevelContainer.getDisplay();

        this.topLevelContainer = topLevelContainer;


        this.includeDockingAreas = includeDockingAreas;

        this.includeSelfDockingAreas = includeSelfDockingAreas;


        this.srcTab = srcTab;

        this.srcFolder = (srcTab == null ? null : srcTab.getParent());

    }


    public Control getTopLevelContainer() {

        return this.topLevelContainer;

    }


    public List<DropZone> determine() {

        this.dropzones.clear();

        determineRecurse(this.topLevelContainer, 0);

        return this.dropzones;

    }


    private void determineRecurse(Control ctl, int depth) {

        CTabFolder folder;

        Set<Integer> avoidIndexes;

        int index;

        if (((ctl instanceof Folder)) && (ctl.isVisible())) {

            folder = ((Folder) ctl).getFolderWidget();
            Rectangle r;
            DropZone dropzone;

            if (folder.getItemCount() == 0) {
                Rectangle client = folder.getClientArea();
                r = this.display.map(folder, this.topLevelContainer, client);
                dropzone = new DropZone(this.topLevelContainer, r);
                dropzone.ctl = ctl;
                dropzone.index = 0;
                this.dropzones.add(dropzone);
            } else {
                avoidIndexes = new HashSet();
                if (folder == this.srcFolder) {
                    int srcIndex = Arrays.asList(this.srcFolder.getItems()).indexOf(this.srcTab);
                    if (srcIndex >= 0) {
                        avoidIndexes.add(Integer.valueOf(srcIndex));
                        avoidIndexes.add(Integer.valueOf(srcIndex + 1));
                    }
                }

                index = 0;

                if (!avoidIndexes.contains(Integer.valueOf(index))) {

                    r = this.display.map(folder, this.topLevelContainer, new Rectangle(0, 0, 4, folder.getTabHeight()));

                    dropzone = new DropZone(this.topLevelContainer, r);

                    dropzone.ctl = ctl;

                    dropzone.index = 0;

                    this.dropzones.add(dropzone);

                }

                index++;


//                Rectangle r = folder.getItems();
//                DropZone dropzone = r.length;
                for(CTabItem item : folder.getItems()){
                //for (DropZone localDropZone1 = 0; localDropZone1 < dropzone; localDropZone1++) {

                    if (!avoidIndexes.contains(Integer.valueOf(index))) {

                        Rectangle b = item.getBounds();

                        int x0 = b.x + b.width - 2;

                        int width = 4;


                        if (index == folder.getItemCount()) {

                            Rectangle folderCA = folder.getClientArea();

                            width = folderCA.x + folderCA.width - x0;

                        }

                        r = this.display.map(folder, this.topLevelContainer, new Rectangle(x0, b.y, width, b.height));

                        dropzone = new DropZone(this.topLevelContainer, r);

                        dropzone.ctl = ctl;

                        dropzone.index = index;

                        this.dropzones.add(dropzone);
                    }
                    index++;
                }
                if ((this.includeDockingAreas) && ((this.includeSelfDockingAreas) || (folder != this.srcFolder))) {
                    Rectangle client = folder.getClientArea();

                    int sqSpacing = 10;

                    int minSqSize = 10;

                    int min = 70;

                    if ((client.width >= 70) && (client.height >= 70)) {

                        int sqWidth = (client.width - 40) / 3;

                        int sqHeight = (client.height - 40) / 3;


                        r = this.display.map(folder, this.topLevelContainer, new Rectangle(client.x + 20 + sqWidth, client.y + 10, sqWidth, sqHeight));


                        dropzone = new DropZone(this.topLevelContainer, r);

                        dropzone.ctl = ctl;

                        dropzone.index = -1;

                        this.dropzones.add(dropzone);


                        r = this.display.map(folder, this.topLevelContainer, new Rectangle(client.x + 20 + sqWidth, client.y + 30 + 2 * sqHeight, sqWidth, sqHeight));


                        dropzone = new DropZone(this.topLevelContainer, r);

                        dropzone.ctl = ctl;

                        dropzone.index = -2;

                        this.dropzones.add(dropzone);


                        r = this.display.map(folder, this.topLevelContainer, new Rectangle(client.x + 10, client.y + 20 + sqHeight, sqWidth, sqHeight));


                        dropzone = new DropZone(this.topLevelContainer, r);

                        dropzone.ctl = ctl;

                        dropzone.index = -3;

                        this.dropzones.add(dropzone);


                        r = this.display.map(folder, this.topLevelContainer, new Rectangle(client.x + 30 + 2 * sqWidth, client.y + 20 + sqHeight, sqWidth, sqHeight));


                        dropzone = new DropZone(this.topLevelContainer, r);

                        dropzone.ctl = ctl;

                        dropzone.index = -4;

                        this.dropzones.add(dropzone);
                    }
                }
            }
        }

        if ((ctl instanceof Composite)) {
            for (Control c : ((Composite) ctl).getChildren()) {
                determineRecurse(c, depth + 1);
            }
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\DropZones.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */