package common.utils;

import java.util.UUID;

public final class GeneratorUtil
{

    /**
     * @return
     */
    public final static String getUUID()
    {
        return UUID.randomUUID().toString();
    }
}
