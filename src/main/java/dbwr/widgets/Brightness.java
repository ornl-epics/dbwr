/*******************************************************************************
 * Copyright (c) 2023 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

public class Brightness
{
    /** Threshold for considering a color 'bright', suggesting black for text */
    public static final double BRIGHT_THRESHOLD = 410;

    /** Brightness differences below this are considered 'similar brightness' */
    public static final double SIMILARITY_THRESHOLD = 350;

    /** @param color "#RRGGBB"
     *  @return Weighed brightness of that color
     */
    public static double of(final String color)
    {
        int[] rgb = new int[]
        {
            Integer.parseInt(color.substring(1, 3), 16),
            Integer.parseInt(color.substring(3, 5), 16),
            Integer.parseInt(color.substring(5, 7), 16)
        };
        return of(rgb);
    }
    
    /** @param color Color
     *  @return Weighed brightness of that color
     */
    public static double of(final int rgb[])
    {
        return rgb[0]/255.0 * 299 + rgb[1]/255.0 * 587 + rgb[2]/255.0 * 114;
    }
}
