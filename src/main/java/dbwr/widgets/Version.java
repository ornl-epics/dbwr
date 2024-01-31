/*******************************************************************************
 * Copyright (c) 2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Widget version info
 *  @author Kay Kasemir
 */
public class Version
{
    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");
    // Some older displays used a shorter "1.0" format without patch level
    private static final Pattern SHORT_VERSION_PATTERN = Pattern.compile("([0-9]+)\\.([0-9]+)");

    public final int major, minor, patch;

    public Version(final int major, final int minor, final int patch)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /** Parse version from text
     *  @param version "Major.Minor.Patch" type of text or null for "0.0.0"
     *  @return {@link Version}
     *  @throws IllegalArgumentException on error
     */
    public static Version parse(final String version)
    {
        if (version == null)
            return new Version(0, 0, 0);
            
        // First try the long format
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches())
            return new Version(Integer.parseInt(matcher.group(1)),
                               Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));

        matcher = SHORT_VERSION_PATTERN.matcher(version);
        if (matcher.matches())
            return new Version(Integer.parseInt(matcher.group(1)),
                               Integer.parseInt(matcher.group(2)),
                               0);
        throw new IllegalArgumentException("Invalid version string '" + version + "'");
    }

    public int compareTo(final Version other)
    {
        if (major != other.major)
            return major - other.major;
        if (minor != other.minor)
            return minor - other.minor;
        return patch - other.patch;
    }

    @Override
    public String toString()
    {
        return major + "." + minor + "." + patch;
    }
}


