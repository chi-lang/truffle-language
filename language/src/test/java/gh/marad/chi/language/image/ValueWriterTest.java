package gh.marad.chi.language.image;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.ProductType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.runtime.ChiArray;
import gh.marad.chi.language.runtime.ChiHostSymbol;
import gh.marad.chi.language.runtime.ChiObject;
import gh.marad.chi.language.runtime.ChiObjectGen;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.Arrays;
import java.util.List;

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
        var array = new ChiArray(data, Types.getInt());

        // when
        var result = serializeAndDeserialize(array);

        // then
        if (result instanceof ChiArray actual) {
            assertEquals(array.getType(), actual.getType());
            assertIterableEquals(Arrays.stream(data).toList(), array.getUnderlayingArrayList());
        } else fail("Did not deserialize array!");
    }

    @Test
    void testObjectSerialization() throws Exception {
        // given
        var type = new ProductType("mod", "pkg", "Type",
                List.of(Types.getInt()),
                List.of(), List.of());
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

    @Test
    void testHostSymbolSerialization() throws Exception {
        // given
        var env = Mockito.mock(TruffleLanguage.Env.class);
        Mockito.when(env.lookupHostSymbol("java.lang.System")).thenReturn(System.class);
        var hostSymbol = new ChiHostSymbol("java.lang.System", System.class);
        // when
        var result = serializeAndDeserialize(hostSymbol, env);
        // then
        if (result instanceof ChiHostSymbol actual) {
            assertEquals(hostSymbol.getSymbolName(), actual.getSymbolName());
            assertEquals(hostSymbol.getSymbol(), actual.getSymbol());
        } else fail();
    }

    Object serializeAndDeserialize(Object value) throws Exception {
        return serializeAndDeserialize(value, null);
    }

    Object serializeAndDeserialize(Object value, TruffleLanguage.Env env) throws Exception {
        var baos = new ByteArrayOutputStream();
        var dos = new DataOutputStream(baos);

        ValueWriter.writeValue(value, dos);

        var dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return ValueWriter.readValue(dis, env);
    }

}