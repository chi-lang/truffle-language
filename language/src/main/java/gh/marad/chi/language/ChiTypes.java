package gh.marad.chi.language;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.runtime.*;

@TypeSystem({long.class, float.class, boolean.class, TruffleString.class, ChiFunction.class, ChiObject.class, ChiArray.class, ChiHostSymbol.class})
public class ChiTypes {

    @CompilerDirectives.TruffleBoundary
    public static Type getType(Object object) {
        if(object instanceof Long) {
            return Type.getIntType();
        } else if (object instanceof Float) {
            return Type.getFloatType();
        } else if (object instanceof Boolean) {
            return Type.getBool();
        } else if (object instanceof TruffleString) {
            return Type.getString();
        } else if (object instanceof ChiFunction f) {
            throw new TODO("Determining function type is unsupported! (yet?)");
        } else if (object instanceof ChiObject o) {
            return o.getType();
        } else if (object instanceof ChiArray a) {
            return a.getType();
        }
        return Type.getAny();
    }

    public static Object unwrapHostSymbol(Object o) {
        if (ChiTypesGen.isChiHostSymbol(o)) {
            return ((ChiHostSymbol)o).getSymbol();
        }
        return o;
    }

    @ImplicitCast
    public static TruffleString toTruffleString(int i) {
        return TruffleString.fromLongUncached(i, TruffleString.Encoding.UTF_8, false);
    }

    @ImplicitCast
    public static TruffleString toTruffleString(long l) {
        return TruffleString.fromLongUncached(l, TruffleString.Encoding.UTF_8, false);
    }

    @ImplicitCast
    // there is something about formatting float as string that cannot be compiled to native
    @CompilerDirectives.TruffleBoundary
    public static TruffleString toTruffleString(float f) {
        return TruffleString.fromJavaStringUncached(Float.toString(f), TruffleString.Encoding.UTF_8);
    }

    @ImplicitCast
    public static TruffleString toTruffleString(String s) {
        return TruffleString.fromJavaStringUncached(s, TruffleString.Encoding.UTF_8);
    }

    @ImplicitCast
    public static TruffleString toTruffleString(boolean b) {
        return TruffleString.fromJavaStringUncached(Boolean.toString(b), TruffleString.Encoding.UTF_8);
    }

    @ImplicitCast
    public static TruffleString toTruffleString(ChiObject o) {
        return TruffleString.fromJavaStringUncached(
                (String) o.toDisplayString(false, DynamicObjectLibrary.getUncached(), InteropLibrary.getUncached()),
                TruffleString.Encoding.UTF_8);
    }

    @ImplicitCast
    public static TruffleString toTruffleString(ChiArray arr) {
        return TruffleString.fromJavaStringUncached((String) arr.toDisplayString(false), TruffleString.Encoding.UTF_8);
    }

    @ImplicitCast
    public static String truffleStringToString(TruffleString s) {
        return s.toString();
    }


    @ImplicitCast
    public static long toLong(int i) {
        return i;
    }

    @ImplicitCast
    public static long toLong(Integer i) {
        return i;
    }

    @ImplicitCast
    public static float toFloat(double d) {
        return (float) d;
    }

    @ImplicitCast
    public static float toFloat(Double d) {
        return (float) d.doubleValue();
    }

    @ImplicitCast
    public static Object[] toJavaArray(ChiArray array) {
        return array.unsafeGetUnderlyingArray();
    }

    @ImplicitCast
    public static ChiArray toChiArray(Object[] array) {
        return new ChiArray(array, Type.getAny());
    }
}
