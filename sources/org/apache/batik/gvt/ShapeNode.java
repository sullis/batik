/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A graphics node that represents a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ShapeNode extends AbstractGraphicsNode {

    /**
     * The shape that describes this <tt>ShapeNode</tt>.
     */
    protected Shape shape;

    /**
     * The shape painter used to paint the shape of this shape node.
     */
    protected ShapePainter shapePainter;

    /**
     * Internal Cache: Primitive bounds
     */
    private Rectangle2D primitiveBounds;

    /**
     * Internal Cache: Geometry bounds
     */
    private Rectangle2D geometryBounds;

    /**
     * Internal Cache: The painted area.
     */
    private Shape paintedArea;

    /**
     * Constructs a new empty <tt>ShapeNode</tt>.
     */
    public ShapeNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the shape of this <tt>ShapeNode</tt>.
     *
     * @param newShape the new shape of this shape node
     */
    public void setShape(Shape newShape) {
        invalidateGeometryCache();
        this.shape = newShape;
        if(this.shapePainter != null){
            this.shapePainter.setShape(newShape);
        }
    }

    /**
     * Returns the shape of this <tt>ShapeNode</tt>.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the <tt>ShapePainter</tt> used by this shape node to render its
     * shape.
     *
     * @param newShapePainter the new ShapePainter to use 
     */
    public void setShapePainter(ShapePainter newShapePainter) {
        invalidateGeometryCache();
        this.shapePainter = newShapePainter;
        if(shapePainter != null && shape != this.shapePainter.getShape()){
            shapePainter.setShape(shape);
        }
    }

    /**
     * Returns the <tt>ShapePainter</tt> used by this shape node to render its
     * shape.
     */
    public ShapePainter getShapePainter() {
        return shapePainter;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (isVisible) {
            super.paint(g2d);
        }

    }

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (shapePainter != null) {
            shapePainter.paint(g2d);
        }
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates this <tt>ShapeNode</tt>. This node and all its ancestors have
     * been informed that all its cached values related to its bounds must be
     * recomputed.  
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
        paintedArea = null;
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
        Rectangle2D b = getBounds();
        if (b != null) {
            return (b.contains(p) &&
                    paintedArea != null &&
                    paintedArea.contains(p));
        } else {
	    return false;
	}
    }

    /**
     * Returns true if the interior of this node intersects the interior of a
     * specified Rectangle2D, false otherwise.
     *
     * @param r the specified Rectangle2D in the user node space
     */
    public boolean intersects(Rectangle2D r) {
        Rectangle2D b = getBounds();
        if (b != null) {
            return (b.intersects(r) &&
                    paintedArea != null &&
                    paintedArea.intersects(r));
        }
        return false;
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        if (primitiveBounds == null) {
            if ((shape == null) || (shapePainter == null)) {
                return null;
            }
            paintedArea = shapePainter.getPaintedArea();
            primitiveBounds = paintedArea.getBounds2D();

            // Make sure we haven't been interrupted
            if (Thread.currentThread().isInterrupted()) {
                // The Thread has been interrupted. Invalidate
                // any cached values and proceed.
                invalidateGeometryCache();
            }
        }
        return primitiveBounds;
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds(){
        if (geometryBounds == null) {
            if (shape == null) {
                return null;
            }
            geometryBounds = shape.getBounds2D();
        }
        return geometryBounds;
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        return shape;
    }
}
