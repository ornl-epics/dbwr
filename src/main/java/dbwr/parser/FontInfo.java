package dbwr.parser;

import java.util.Map;

public class FontInfo
{
	private final int size;

	public FontInfo(final int size)
	{
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}

	public void addToStyles(Map<String, String> styles)
	{
		styles.put("font-size", size + "px");
	}

	@Override
	public String toString()
	{
		return String.format("font-size: %dpx;", size);
	}
}
