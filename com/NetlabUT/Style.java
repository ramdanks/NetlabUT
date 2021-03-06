package com.NetlabUT;

import java.awt.*;
import java.util.Stack;

/** standardized color used by {@link WindowGrading}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
final class Style
{
    private static Color[] COLOR_ASSUMPTION = null;
    public static final Color CORRECT_COLOR = new Color(220, 255, 220);
    public static final Color CORRECT_COLOR_FOCUS = new Color(180, 255, 180);
    public static final Color WRONG_COLOR = new Color(255, 220, 220);
    public static final Color WRONG_COLOR_FOCUS = new Color(255, 180, 180);
    public static final Color NEUTRAL_FOCUS = new Color(230, 230, 230);
    public static final Color NEUTRAL = new Color(255, 255, 255);

    public static final Color getAssumptionColor(Expectation status)
    {
        if (COLOR_ASSUMPTION == null)
            initAssumptionColor();
        return COLOR_ASSUMPTION[status.ordinal()];
    }

    @Deprecated
    public static final Color getAssumptionColor(int status)
    {
        if (COLOR_ASSUMPTION == null)
            initAssumptionColor();
        return COLOR_ASSUMPTION[status];
    }

    private static void initAssumptionColor()
    {
        COLOR_ASSUMPTION = new Color[8];
        COLOR_ASSUMPTION[Expectation.REFERENCE.ordinal()]       = new Color(220, 255, 220);
        COLOR_ASSUMPTION[Expectation.NOT_REFERENCE.ordinal()]   = new Color(160, 255, 160);
        COLOR_ASSUMPTION[Expectation.EQUAL.ordinal()]           = new Color(255, 250, 200);
        COLOR_ASSUMPTION[Expectation.NOT_EQUAL.ordinal()]       = new Color(255, 240, 150);
        COLOR_ASSUMPTION[Expectation.ARRAY_EQUAL.ordinal()]     = new Color(195, 249, 255);
        COLOR_ASSUMPTION[Expectation.ARRAY_NOT_EQUAL.ordinal()] = new Color(147, 255, 255);
        COLOR_ASSUMPTION[Expectation.THROWS.ordinal()]          = new Color(253, 211, 211);
        COLOR_ASSUMPTION[Expectation.THROWS_TYPE.ordinal()]     = new Color(255, 167, 167);
    }
}
