package gh.marad.chi.language.image;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.PlusOperatorNodeGen;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.expr.variables.ReadModuleVariable;
import gh.marad.chi.language.nodes.expr.variables.WriteLocalVariableNodeGen;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.value.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Supplier;

public class NodeReader {
    private final DataInputStream stream;
    private FrameDescriptor.Builder currentFdBuilder;

    public NodeReader(DataInputStream stream) {
        this.stream = stream;
    }

    public <T> T withFrameDescriptor(FrameDescriptor.Builder fd, Supplier<T> f) {
        var previousFdBuilder = currentFdBuilder;
        currentFdBuilder = fd;
        var result = f.get();
        currentFdBuilder = previousFdBuilder;
        return result;
    }

    public ChiNode readNode() throws IOException {
        var nodeId = NodeId.fromId(stream.readShort());

        return switch (nodeId) {
            case LongValue -> readLongValue();
            case FloatValue -> readFloatValue();
            case StringValue -> readStringValue();
            case BooleanValue -> readBooleanValue();
            case BuildInterpolatedString -> readBuildInterpolatedString();
            case WriteLocalVariable -> readWriteLocalVariable();
            case ReadModuleVariable -> readReadModuleVariable();
            case Block -> readBlock();
            case ReadLocalArgument -> readLocalArgument();
            case InvokeFunction -> readInvokeFunction();
            case GetDefinedFunction -> readGetDefinedFunction();
            case PlusOperator -> readPlusOperator();
        };
    }

    public ChiNode readLongValue() throws IOException {
        return new LongValue(stream.readLong());
    }

    public ChiNode readFloatValue() throws IOException {
        return new FloatValue(stream.readFloat());
    }

    public ChiNode readStringValue() throws IOException {
        return new StringValue(stream.readUTF());
    }

    public ChiNode readBooleanValue() throws IOException {
        return new BooleanValue(stream.readBoolean());
    }

    public ChiNode readBuildInterpolatedString() throws IOException {
        var partCount = stream.readShort();
        var parts = new ChiNode[partCount];
        for(int i=0; i < partCount; i++) {
            parts[i] = readNode();
        }
        return new BuildInterpolatedString(parts);
    }

    public ChiNode readWriteLocalVariable() throws IOException {
        var slot = stream.readByte();
        var variableName = stream.readUTF();
        var valueNode = readNode();
        var addedSlot = currentFdBuilder.addSlot(FrameSlotKind.Illegal, variableName, null);
        assert addedSlot == slot : "Saved slot does not match added slot!";
        return WriteLocalVariableNodeGen.create(valueNode, slot, variableName);
    }

    public ChiNode readReadModuleVariable() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var variableNAme = stream.readUTF();
        return new ReadModuleVariable(moduleName, packageName, variableNAme);
    }

    // ---

    public ChiNode readBlock() throws IOException {
        var bodySize = stream.readShort();
        var elements = new ChiNode[bodySize];
        for (int i = 0; i < bodySize; i++) {
            elements[i] = readNode();
        }
        return new BlockExpr(elements);
    }

    public ChiNode readLocalArgument() throws IOException {
        var slot = stream.readByte();
        return new ReadLocalArgument(slot);
    }

    public ChiNode readInvokeFunction() throws IOException {
        var argCount = stream.readByte();
        var arguments = new ChiNode[argCount];
        for (byte i = 0; i < argCount; i++) {
            arguments[i] = readNode();
        }
        var function = readNode();
        return new InvokeFunction(function, arguments);
    }

    public ChiNode readGetDefinedFunction() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var functionName = stream.readUTF();
        var paramTypes = TypeWriter.readTypes(stream);
        return new GetDefinedFunction(moduleName, packageName, functionName, paramTypes);
    }

    public ChiNode readPlusOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return PlusOperatorNodeGen.create(left, right);
    }
}
