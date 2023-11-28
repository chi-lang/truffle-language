package gh.marad.chi.language.image;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.objects.ReadMemberNodeGen;
import gh.marad.chi.language.nodes.objects.WriteMemberNodeGen;
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
            case ReadLocalVariable -> readReadLocalVariable();
            case ReadOuterScopeVariable -> readReadOuterScopeVariable();
            case ReadOuterScopeArgument -> readReadOuterScopeArgument();
            case ReadMember -> readReadMember();
            case WriteMember -> readWriteMember();
            case Block -> readBlock();
            case ReadLocalArgument -> readLocalArgument();
            case PlusOperator -> readPlusOperator();
            case MinusOperator -> readMinusOperator();
            case MultiplyOperator -> readMultiplyOperator();
            case DivideOperator -> readDivideOperator();
            case ModuloOperator -> readModuloOperator();
            case InvokeFunction -> readInvokeFunction();
            case GetDefinedFunction -> readGetDefinedFunction();
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

    public ChiNode readReadLocalVariable() throws IOException {
        var slot = stream.readByte();
        var name = stream.readUTF();
        return new ReadLocalVariable(name, slot);
    }

    public ChiNode readReadOuterScopeVariable() throws IOException {
        var name = stream.readUTF();
        return new ReadOuterScopeVariable(name);
    }

    public ChiNode readLocalArgument() throws IOException {
        var slot = stream.readByte();
        return new ReadLocalArgument(slot);
    }

    public ChiNode readReadOuterScopeArgument() throws IOException {
        var scopesUp = stream.readByte();
        var argIndex = stream.readByte();
        return new ReadOuterScopeArgument(scopesUp, argIndex);
    }

    public ChiNode readReadMember() throws IOException {
        var member = stream.readUTF();
        var receiver = readNode();
        return ReadMemberNodeGen.create(receiver, member);
    }

    public ChiNode readWriteMember() throws IOException {
        var member = stream.readUTF();
        var receiver = readNode();
        var value = readNode();
        return WriteMemberNodeGen.create(receiver, value, member);
    }


    public ChiNode readBlock() throws IOException {
        var bodySize = stream.readShort();
        var elements = new ChiNode[bodySize];
        for (int i = 0; i < bodySize; i++) {
            elements[i] = readNode();
        }
        return new BlockExpr(elements);
    }

    public ChiNode readPlusOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return PlusOperatorNodeGen.create(left, right);
    }

    public ChiNode readMinusOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return MinusOperatorNodeGen.create(left, right);
    }

    public ChiNode readMultiplyOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return MultiplyOperatorNodeGen.create(left, right);
    }

    public ChiNode readDivideOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return DivideOperatorNodeGen.create(left, right);
    }

    public ChiNode readModuloOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return ModuloOperatorNodeGen.create(left, right);
    }

    // ---


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

}
