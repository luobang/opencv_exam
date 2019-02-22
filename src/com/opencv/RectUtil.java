package com.opencv;

import org.opencv.core.*;
import org.opencv.core.Point;

import java.awt.*;

/**
 * Created by zxc on 2019/2/21.
 */
public class RectUtil {

    /**
     * 判断是否在选择框内
     *
     * @param parent
     * @param child
     * @param distance
     * @return
     */
    public static boolean rectContainRect(Rect parent, Rect child, int distance) {
        if ((
                (parent.x - distance) < child.x
        ) && (
                (parent.y - distance) < child.y
        ) && ((parent.x + parent.width + distance) > child.x)
                && (parent.y + parent.height + distance) > child.y
                && parent.width > (child.width - distance) && parent.height > (child.height - distance)
                )
            return true;
        else
            return false;
    }

    public static boolean rectContainPoint(Rect parent, Point point) {
        if (
                parent.x < point.x
                        && parent.y < point.y
                        && (parent.x + parent.width) > point.x
                        && (parent.y + parent.height) > point.y
                )
            return true;
        else
            return false;
    }


}
