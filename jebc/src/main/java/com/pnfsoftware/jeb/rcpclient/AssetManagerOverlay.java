/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.resource.CompositeImageDescriptor;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.graphics.Point;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class AssetManagerOverlay
        /*     */ {
    /*  25 */   private List<Image> images = new ArrayList();
    /*  26 */   private List<Point> positions = new ArrayList();
    /*  27 */   private String id = "";
    /*  28 */   private boolean built = false;

    /*     */
    /*     */
    /*     */
    /*     */
    public String getId()
    /*     */ {
        /*  34 */
        return this.id;
        /*     */
    }

    /*     */
    /*     */
    public AssetManagerOverlay addLayer(String filename, Point position) {
        /*  38 */
        if (!this.built) {
            /*  39 */
            Image img = UIAssetManager.getInstance().getImage(filename);
            /*  40 */
            if (img != null) {
                /*  41 */
                this.images.add(img);
                /*  42 */
                this.positions.add(position);
                /*  43 */
                this.id += String.format("[%s:%d:%d]", new Object[]{filename, Integer.valueOf(position.x), Integer.valueOf(position.y)});
                /*     */
            }
            /*     */
        }
        /*  46 */
        return this;
        /*     */
    }

    /*     */
    /*     */
    public boolean hasLayer() {
        /*  50 */
        return !this.images.isEmpty();
        /*     */
    }

    /*     */
    /*     */
    public Image build(Image image) {
        /*  54 */
        this.built = true;
        /*  55 */
        List<OverlayImage> overlays = new ArrayList();
        /*  56 */
        for (int i = 0; i < this.images.size(); i++) {
            /*  57 */
            overlays.add(new OverlayImage((Image) this.images.get(i), (Point) this.positions.get(i)));
            /*     */
        }
        /*  59 */
        return new OverlayImageDescriptor(image, overlays).createImage();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */   private static class OverlayImage
            /*     */ {
        /*     */     private ImageData data;
        /*     */
        /*     */
        /*     */     private Point position;

        /*     */
        /*     */
        /*     */
        public OverlayImage(Image image, Point position)
        /*     */ {
            /*  74 */
            this.data = image.getImageData();
            /*  75 */
            this.position = position;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   private static class OverlayImageDescriptor extends CompositeImageDescriptor
            /*     */ {
        /*     */     private final ImageData base;
        /*     */     private final List<AssetManagerOverlay.OverlayImage> ovImages;

        /*     */
        /*     */     OverlayImageDescriptor(Image image, List<AssetManagerOverlay.OverlayImage> ovImages) {
            /*  85 */
            this.base = image.getImageData();
            /*  86 */
            this.ovImages = ovImages;
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        protected void drawCompositeImage(int width, int height)
        /*     */ {
            /*  93 */
            for (AssetManagerOverlay.OverlayImage ovImage : this.ovImages) {
                /*  94 */
                drawImage(AssetManagerOverlay.OverlayImage.access$000(ovImage), AssetManagerOverlay.OverlayImage.access$100(ovImage).x, AssetManagerOverlay.OverlayImage.access$100(ovImage).y);
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /*  99 */
            drawImage(this.base, 0, 0);
            /*     */
        }

        /*     */
        /*     */
        protected Point getSize()
        /*     */ {
            /* 104 */
            int width = this.base.width;
            /* 105 */
            int height = this.base.height;
            /* 106 */
            for (AssetManagerOverlay.OverlayImage ovImage : this.ovImages) {
                /* 107 */
                width = Math.max(width, AssetManagerOverlay.OverlayImage.access$000(ovImage).width + AssetManagerOverlay.OverlayImage.access$100(ovImage).x);
                /* 108 */
                height = Math.max(height, AssetManagerOverlay.OverlayImage.access$000(ovImage).height + AssetManagerOverlay.OverlayImage.access$100(ovImage).y);
                /*     */
            }
            /* 110 */
            return new Point(width, height);
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\AssetManagerOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */