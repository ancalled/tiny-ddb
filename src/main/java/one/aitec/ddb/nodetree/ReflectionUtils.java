package one.aitec.ddb.nodetree;

import java.lang.reflect.Field;

public class ReflectionUtils {


    public static Object fieldValue(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            final boolean accessible =f.isAccessible();
            if (!accessible) {
                f.setAccessible(true);
            }
            Object value = f.get(obj);
            if (!accessible) {
                f.setAccessible(false);
            }
            return value;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
}
