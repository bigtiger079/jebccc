
package com.pnfsoftware.jeb.rcpclient;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;


public class AssetManagerOverlay {
    private List<Image> images = new ArrayList();
    private List<Point> positions = new ArrayList();
    private String id = "";
    private boolean built = false;


    public String getId() {

        return this.id;

    }


    public AssetManagerOverlay addLayer(String filename, Point position) {

        if (!this.built) {

            Image img = UIAssetManager.getInstance().getImage(filename);

            if (img != null) {

                this.images.add(img);

                this.positions.add(position);

                this.id += String.format("[%s:%d:%d]", new Object[]{filename, Integer.valueOf(position.x), Integer.valueOf(position.y)});

            }

        }

        return this;

    }


    public boolean hasLayer() {

        return !this.images.isEmpty();

    }


    public Image build(Image image) {

        this.built = true;

        List<OverlayImage> overlays = new ArrayList();

        for (int i = 0; i < this.images.size(); i++) {

            overlays.add(new OverlayImage((Image) this.images.get(i), (Point) this.positions.get(i)));

        }

        return new OverlayImageDescriptor(image, overlays).createImage();

    }


    private static class OverlayImage {
        private ImageData data;


        private Point position;


        public OverlayImage(Image image, Point position) {

            this.data = image.getImageData();

            this.position = position;

        }

    }


    private static class OverlayImageDescriptor extends CompositeImageDescriptor {
        private final ImageData base;
        private final List<AssetManagerOverlay.OverlayImage> ovImages;


        OverlayImageDescriptor(Image image, List<AssetManagerOverlay.OverlayImage> ovImages) {

            this.base = image.getImageData();

            this.ovImages = ovImages;

        }


        protected void drawCompositeImage(int width, int height) {
            for (OverlayImage ovImage : this.ovImages) {
                drawImage(ovImage.data, ovImage.position.x, ovImage.position.y);
            }
            drawImage(this.base, 0, 0);
        }


        protected Point getSize() {
            int width = this.base.width;
            int height = this.base.height;
            for (OverlayImage ovImage : this.ovImages) {
                width = Math.max(width, ovImage.data.width + ovImage.position.x);
                height = Math.max(height, ovImage.data.height + ovImage.position.y);
            }
            return new Point(width, height);
        }
    }
}


