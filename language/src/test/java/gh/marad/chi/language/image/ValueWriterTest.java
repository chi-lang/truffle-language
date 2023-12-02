package gh.marad.chi.language.image;

import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.Type;
import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.runtime.ChiArray;
import gh.marad.chi.language.runtime.ChiObject;
import gh.marad.chi.language.runtime.ChiObjectGen;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValueWriterTest {
    @Test
    void testSimpleValueSerialization() throws Exception {
        assertInstanceOf(Boolean.class, serializeAndDeserialize(true));
        assertEquals(true, serializeAndDeserialize(true));

        assertInstanceOf(Float.class, serializeAndDeserialize(5f));
        assertEquals(5f, serializeAndDeserialize(5f));

        assertInstanceOf(Long.class, serializeAndDeserialize(5L));
        assertEquals(5L, serializeAndDeserialize(5L));

        var hello = TruffleString.fromJavaStringUncached("hello", TruffleString.Encoding.UTF_8);
        assertInstanceOf(TruffleString.class, serializeAndDeserialize(hello));
        assertEquals(hello, serializeAndDeserialize(hello));
    }

    @Test
    void testArrayValueSerialization() throws Exception {
        // given
        var data = new Long[] {5L, 3L, 4L};
        var array = new ChiArray(data, Type.getIntType());

        // when
        var result = serializeAndDeserialize(array);

        // then
        if (result instanceof ChiArray actual) {
            assertEquals(array.getType(), actual.getType());
            assertArrayEquals(data, array.unsafeGetUnderlayingArray());
        } else fail("Did not deserialize array!");
    }

    @Test
    void testObjectSerialization() throws Exception {
        // given
        var type = new VariantType("mod", "pkg", "Type", List.of(), Map.of(),
                new VariantType.Variant(true, "Variant", List.of(
                        new VariantType.VariantField(
                                true, "Field", Type.getIntType()
                        )
                )));
        var obj = ChiLanguage.createObject(type, null);
        var interop = ChiObjectGen.InteropLibraryExports.Uncached.getUncached();
        interop.writeMember(obj, "Field", 5L);

        // when
        var result = serializeAndDeserialize(obj);

        // then
        if (result instanceof ChiObject actual) {
            var dol = DynamicObjectLibrary.getUncached();
            assertArrayEquals(dol.getKeyArray(obj), dol.getKeyArray(actual));
            assertEquals(obj.getType(), actual.getType());
            for (Object field : dol.getKeyArray(obj)) {
                assertEquals(
                        dol.getOrDefault(obj, field, 5L),
                        dol.getOrDefault(actual, field, false));
            }
        } else fail();

    }

    Object serializeAndDeserialize(Object value) throws Exception {

        var baos = new ByteArrayOutputStream();
        var dos = new DataOutputStream(baos);

        ValueWriter.writeValue(value, dos);

        var dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return ValueWriter.readValue(dis, null);
    }

}