package dbwr.macros;

import java.util.Collection;

public interface MacroProvider
{
    public Collection<String> getMacroNames();

    public String getMacroValue(String name);
}
