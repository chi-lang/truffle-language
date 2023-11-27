package gh.marad.chi.language.image;

import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.PlusOperatorNodeGen;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.value.LongValue;
import gh.marad.chi.language.nodes.value.StringValue;

import java.io.DataInputStream;
import java.io.IOException;

public class NodeReader {
    private final DataInputStream stream;

    public NodeReader(DataInputStream stream) {
        this.stream = stream;
    }

    public ChiNode readNode() throws IOException {
        var nodeId = NodeId.fromId(stream.readShort());

        return switch (nodeId) {
            case Block -> readBlock();
            case ReadLocalArgument -> readLocalArgument();
            case InvokeFunction -> readInvokeFunction();
            case LongValue -> readLongValue();
            case StringValue -> readStringValue();
            case GetDefinedFunction -> readGetDefinedFunction();
            case PlusOperator -> readPlusOperator();
        };
    }

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

    public ChiNode readLongValue() throws IOException {
        return new LongValue(stream.readLong());
    }
    public ChiNode readStringValue() throws IOException {
        return new StringValue(stream.readUTF());
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
