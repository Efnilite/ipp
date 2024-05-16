package dev.efnilite.ipp.util;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class to serialize objects with in Base 64.
 */
public class ObjectSerializer {

    /**
     * Serialize an object to base 64.
     *
     * @param object The object.
     * @param <T>    The object type.
     * @return A base 64 representation of the object.
     * @throws IOException When something goes wrong while encoding or writing.
     */
    public static <T> String serialize64(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream output = new BukkitObjectOutputStream(outputStream);

        output.writeObject(object);

        output.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    /**
     * Deserialize an object from base 64.
     *
     * @param string The base 64 string.
     * @param <T>    The object type.
     * @return The deserialized object.
     * @throws IOException            When something goes wrong while decoding or reading.
     * @throws ClassNotFoundException When the class of the object cannot be found.
     */
    public static <T> @Nullable T deserialize64(String string) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(string));
        BukkitObjectInputStream input = new BukkitObjectInputStream(inputStream);

        input.close();
        return (T) input.readObject();
    }
}