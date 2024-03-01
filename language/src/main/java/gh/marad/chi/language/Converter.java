package gh.marad.chi.language;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.nodes.RootNode;
import gh.marad.chi.core.*;
import gh.marad.chi.core.types.*;
import gh.marad.chi.core.types.Record;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.nodes.IndexOperatorNodeGen;
import gh.marad.chi.language.nodes.IndexedAssignmentNodeGen;
import gh.marad.chi.language.nodes.arrays.ConstructArrayNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.nodes.expr.cast.CastToFloatNodeGen;
import gh.marad.chi.language.nodes.expr.cast.CastToLongExprNodeGen;
import gh.marad.chi.language.nodes.expr.cast.CastToStringNodeGen;
import gh.marad.chi.language.nodes.expr.flow.IfExpr;
import gh.marad.chi.language.nodes.expr.flow.IsNodeGen;
import gh.marad.chi.language.nodes.expr.flow.ReturnNode;
import gh.marad.chi.language.nodes.expr.flow.ReturnUnitNode;
import gh.marad.chi.language.nodes.expr.flow.effect.HandleEffectNode;
import gh.marad.chi.language.nodes.expr.flow.effect.InvokeEffect;
import gh.marad.chi.language.nodes.expr.flow.effect.ResumeNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileBreakNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileContinueNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileExprNode;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.operators.bit.BitAndOperatorNodeGen;
import gh.marad.chi.language.nodes.expr.operators.bit.BitOrOperatorNodeGen;
import gh.marad.chi.language.nodes.expr.operators.bit.ShlOperatorNodeGen;
import gh.marad.chi.language.nodes.expr.operators.bit.ShrOperatorNodeGen;
import gh.marad.chi.language.nodes.expr.operators.bool.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.function.DefinePackageFunction;
import gh.marad.chi.language.nodes.function.DefinePackageFunctionFromNodeGen;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.objects.ConstructChiObject;
import gh.marad.chi.language.nodes.objects.ReadMemberNodeGen;
import gh.marad.chi.language.nodes.objects.WriteMemberNodeGen;
import gh.marad.chi.language.nodes.value.*;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.TODO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Converter {
    private final ChiLanguage language;
    private FrameDescriptor.Builder currentFdBuilder;
    private boolean insideFunction = false;
    private Map<String, Integer> localSlots = null;
    private Map<String, Integer> fnArgs = null;

    private String currentModule = CompilationDefaults.INSTANCE.getDefaultModule();
    private String currentPackage = CompilationDefaults.INSTANCE.getDefaultPacakge();

    public Converter(ChiLanguage language, FrameDescriptor.Builder fdBuilder) {
        this.language = language;
        this.currentFdBuilder = fdBuilder;
    }

    public ChiNode convertProgram(Program program) {
        currentModule = program.getPackageDefinition().getModuleName();
        currentPackage = program.getPackageDefinition().getPackageName();
        var body = new ArrayList<ChiNode>();

        // definicje konstruktorów i objectów - nie są potrzebne bo to można robić ręcznie
        // lub może to zrobić kompilator automatycznie
//        for (TypeInfo typeInfo : program.getDefinedType()) {
//            if (typeInfo.getType() instanceof SimpleType) {
//                body.add(defineConstructedObject(typeInfo));
//            } else if (typeInfo.getType() instanceof ProductType) {
//                body.add(defineConstructor(typeInfo));
//            }
//        }

        for (Expression expr : program.getExpressions()) {
            var node = convertExpression(expr);
            if (node != null) {
                body.add(node);
            }
        }

        if (!body.isEmpty()) {
            var block = new BlockExpr(body.toArray(new ChiNode[0]));
            block.addRootTag();
            return block;
        } else {
            return new UnitValue();
        }
    }

    public ChiNode convertExpression(Expression expr) {
        if (expr instanceof Atom atom) {
            return convertAtom(atom);
        } else if (expr instanceof InterpolatedString interpolatedString) {
            return convertInterpolatedString(interpolatedString);
        } else if (expr instanceof NameDeclaration nameDeclaration) {
            return convertNameDeclaration(nameDeclaration);
        } else if (expr instanceof VariableAccess variableAccess) {
            return convertVariableAccess(variableAccess);
        } else if (expr instanceof FieldAccess fieldAccess) {
            return convertFieldAccess(fieldAccess);
        } else if (expr instanceof FieldAssignment assignment) {
            return convertFieldAssignment(assignment);
        } else if (expr instanceof Block block) {
            return convertBlock(block);
        } else if (expr instanceof InfixOp infixOp) {
            return convertInfixOp(infixOp);
        } else if (expr instanceof PrefixOp prefixOp) {
            return convertPrefixOp(prefixOp);
        } else if (expr instanceof Cast cast) {
            return convertCast(cast);
        } else if (expr instanceof IfElse ifElse) {
            return convertIfExpr(ifElse);
        } else if (expr instanceof Fn fn) {
            return convertFnExpr(fn);
        } else if (expr instanceof FnCall fnCall) {
            return convertFnCall(fnCall);
        } else if (expr instanceof Assignment assignment) {
            return convertAssignment(assignment);
        } else if (expr instanceof WhileLoop whileLoop) {
            return convertWhileExpr(whileLoop);
        } else if (expr instanceof Break) {
            return new WhileBreakNode();
        } else if (expr instanceof Continue) {
            return new WhileContinueNode();
        } else if (expr instanceof IndexOperator op) {
            return IndexOperatorNodeGen.create(
                    convertExpression(op.getVariable()),
                    convertExpression(op.getIndex())
            );
        } else if (expr instanceof IndexedAssignment op) {
            return IndexedAssignmentNodeGen.create(
                    convertExpression(op.getVariable()),
                    convertExpression(op.getIndex()),
                    convertExpression(op.getValue())
            );
        } else if (expr instanceof Is is) {
            return convertIs(is);
        } else if (expr instanceof EffectDefinition definition) {
            return convertEffectDefinition(definition);
        } else if (expr instanceof Handle handle) {
            return convertHandle(handle);
        } else if (expr instanceof Return ret) {
            if (ret.getValue() != null) {
                return new ReturnNode(convertExpression(ret.getValue()));
            } else {
                return ReturnUnitNode.instance;
            }
        } else if (expr instanceof CreateRecord createRecord) {
            var fieldCount = createRecord.getFields().size();
            var fieldNames = new String[fieldCount];
            var fieldValues = new ChiNode[fieldCount];
            for (int i = 0; i < createRecord.getFields().size(); i++) {
                var field = createRecord.getFields().get(i);
                fieldNames[i] = field.getName();
                fieldValues[i] = convertExpression(field.getValue());
            }
            return new ConstructChiObject((Record) createRecord.getType(), fieldNames, fieldValues);
        } else if (expr instanceof CreateArray createArray) {
            var valueNodes = new ChiNode[createArray.getValues().size()];
            for (int i = 0; i < createArray.getValues().size(); i++) {
                var value = createArray.getValues().get(i);
                valueNodes[i] = convertExpression(value);
            }
            return new ConstructArrayNode(valueNodes, createArray.getType());
        }

        throw new TODO("Unhandled expression conversion: %s".formatted(expr));
    }

    private ChiNode convertAtom(Atom atom) {
        if (atom.getType() == Type.getInt()) {
            return new LongValue(Long.parseLong(atom.getValue()));
        }
        if (atom.getType() == Type.getFloat()) {
            return new FloatValue(Float.parseFloat(atom.getValue()));
        }
        if (atom.getType() == Type.getString()) {
            return new StringValue(atom.getValue());
        }
        if (atom.getType() == Type.getBool()) {
            return new BooleanValue(Boolean.parseBoolean(atom.getValue()));
        }
        if (atom.getType() == Type.getUnit()) {
            return new UnitValue();
        }
        throw new TODO("Unhandled atom type: %s".formatted(atom.getType()));
    }

    private ChiNode convertInterpolatedString(InterpolatedString interpolatedString) {
        var nodes = interpolatedString.getParts().stream()
                                      .map(this::convertExpression)
                                      .toList();
        return new BuildInterpolatedString(nodes.toArray(new ChiNode[0]));
    }

    private ChiNode convertNameDeclaration(NameDeclaration nameDeclaration) {
        if (!insideFunction && nameDeclaration.getValue() instanceof Fn fn) {
            assert nameDeclaration.getType() != null;
            return convertModuleFunctionDefinitionFromFunctionNode(
                    nameDeclaration.getName(),
                    // This is similar to the next block but this also preserves the function name
                    convertFnExpr(fn, nameDeclaration.getName()),
                    new PolyType(0, nameDeclaration.getType()),
                    nameDeclaration.getPublic()
            );
        } else if (!insideFunction && nameDeclaration.getValue().getType() instanceof Function) {
            assert nameDeclaration.getType() != null;
            return convertModuleFunctionDefinitionFromFunctionNode(
                    nameDeclaration.getName(),
                    convertExpression(nameDeclaration.getValue()),
                    new PolyType(0, nameDeclaration.getType()),
                    nameDeclaration.getPublic()
            );
        } else if (!insideFunction) {
            return DefineModuleVariableNodeGen.create(
                    convertExpression(nameDeclaration.getValue()),
                    currentModule, currentPackage, nameDeclaration.getName(),
                    nameDeclaration.getType(),
                    nameDeclaration.getPublic(),
                    nameDeclaration.getMutable()
                    );
        } else {
            int slot = currentFdBuilder.addSlot(FrameSlotKind.Illegal, nameDeclaration.getName(), null);
            localSlots.put(nameDeclaration.getName(), slot);
            ChiNode valueExpr = convertExpression(nameDeclaration.getValue());
            return WriteLocalVariableNodeGen.create(valueExpr,
                    slot,
                    nameDeclaration.getName());
        }
    }

    private ChiNode convertVariableAccess(VariableAccess variableAccess) {
        var target = variableAccess.getTarget();
        if (target instanceof PackageSymbol symbol) {
            return new ReadModuleVariable(
                    symbol.getModuleName(),
                    symbol.getPackageName(),
                    symbol.getName());
        } else if (target instanceof LocalSymbol symbol) {
            if (localSlots == null) {
                return new ReadModuleVariable(
                        currentModule,
                        currentPackage,
                        symbol.getName()
                );
            }
            var localSlot = localSlots.get(symbol.getName());
            if (localSlot != null) {
                return new ReadLocalVariable(symbol.getName(), localSlot);
            }
            var argNum = fnArgs.get(symbol.getName());
            if (argNum != null) {
                return new ReadLocalArgument(argNum);
            }
        }
        throw new TODO("Invalid variable access: " + variableAccess);
    }

    private ChiNode convertFieldAccess(FieldAccess fieldAccess) {
        var target = fieldAccess.getTarget();
        if (target instanceof DotTarget.Field) {
            return ReadMemberNodeGen.create(convertExpression(fieldAccess.getReceiver()), fieldAccess.getFieldName());
        } else if (target instanceof DotTarget.PackageFunction fn) {
            return new ReadModuleVariable(
                    fn.getModuleName(),
                    fn.getPackageName(),
                    fn.getName()
            );
        } else if (target instanceof DotTarget.LocalFunction) {
            if (localSlots != null) {
                var slot = localSlots.get(fieldAccess.getFieldName());
                return new ReadLocalVariable(fieldAccess.getFieldName(), slot);
            } else {
                return new ReadModuleVariable(
                        currentModule,
                        currentPackage,
                        fieldAccess.getFieldName()
                );
            }
        }

        throw new TODO("Unsupported field access target: %s".formatted(target));
    }

    private ChiNode convertFieldAssignment(FieldAssignment assignment) {
        var receiver = convertExpression(assignment.getReceiver());
        var value = convertExpression(assignment.getValue());
        return WriteMemberNodeGen.create(receiver, value, assignment.getFieldName());
    }

    private ChiNode convertAssignment(Assignment assignment) {
        var target = assignment.getTarget();

        var value = convertExpression(assignment.getValue());
        if (target instanceof PackageSymbol symbol) {
            return WriteModuleVariableNodeGen.create(
                    value,
                    symbol.getModuleName(),
                    symbol.getPackageName(),
                    symbol.getName());
        } else if (target instanceof LocalSymbol symbol) {
            if (localSlots == null) {
                return WriteModuleVariableNodeGen.create(
                        value,
                        currentModule,
                        currentPackage,
                        symbol.getName());
            }
            var slot = localSlots.get(symbol.getName());
            if (slot != null) {
                return WriteLocalVariableNodeGen.create(value, slot, symbol.getName());
            }
            var arg = fnArgs.get(symbol.getName());
            if (arg != null) {
                return WriteLocalArgumentNodeGen.create(value, arg);
            }
        }
        throw new TODO("This should not happen");
    }

    private ChiNode convertBlock(Block block) {
        return convertBlock(block, null);
    }

    private ChiNode convertBlock(Block block, Type returnType) {
        if (block.getBody().isEmpty()) {
            return new UnitValue();
        }

        var body = new ArrayList<>(block.getBody().stream().map(this::convertExpression).toList());
        if (returnType == Type.getUnit()) {
            body.add(new UnitValue());
        }
        return new BlockExpr(body.toArray(new ChiNode[0]));
    }

    private ChiNode convertInfixOp(InfixOp infixOp) {
        var left = convertExpression(infixOp.getLeft());
        var right = convertExpression(infixOp.getRight());
        return switch (infixOp.getOp()) {
            case "+" -> PlusOperatorNodeGen.create(left, right);
            case "-" -> MinusOperatorNodeGen.create(left, right);
            case "*" -> MultiplyOperatorNodeGen.create(left, right);
            case "/" -> DivideOperatorNodeGen.create(left, right);
            case "%" -> ModuloOperatorNodeGen.create(left, right);
            case "==" -> EqualOperatorNodeGen.create(left, right);
            case "!=" -> NotEqualOperatorNodeGen.create(left, right);
            case "<" -> LessThanOperatorNodeGen.create(false, left, right);
            case "<=" -> LessThanOperatorNodeGen.create(true, left, right);
            case ">" -> GreaterThanOperatorNodeGen.create(false, left, right);
            case ">=" -> GreaterThanOperatorNodeGen.create(true, left, right);
            case "&&" -> new LogicAndOperator(left, right);
            case "||" -> new LogicOrOperator(left, right);
            case "&" -> BitAndOperatorNodeGen.create(left, right);
            case "|" -> BitOrOperatorNodeGen.create(left, right);
            case "<<" -> ShlOperatorNodeGen.create(left, right);
            case ">>" -> ShrOperatorNodeGen.create(left, right);
            default -> throw new TODO("Unhandled infix operator: '%s'".formatted(infixOp.getOp()));
        };
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private ChiNode convertPrefixOp(PrefixOp prefixOp) {
        var value = convertExpression(prefixOp.getExpr());
        return switch (prefixOp.getOp()) {
            case "!" -> LogicNotOperatorNodeGen.create(value);
            default -> throw new TODO("Unhandled prefix operator: '%s'".formatted(prefixOp.getOp()));
        };
    }

    private ChiNode convertCast(Cast cast) {
        var value = convertExpression(cast.getExpression());
        if (cast.getTargetType() == Type.getInt()) {
            return CastToLongExprNodeGen.create(value);
        } else if (cast.getTargetType() == Type.getFloat()) {
            return CastToFloatNodeGen.create(value);
        } else if (cast.getTargetType() == Type.getString()) {
            return CastToStringNodeGen.create(value);
        } else {
            return value;
        }
    }

    private ChiNode convertIfExpr(IfElse ifElse) {
        var condition = convertExpression(ifElse.getCondition());
        var thenBranch = convertExpression(ifElse.getThenBranch());
        ChiNode elseBranch;
        if (ifElse.getElseBranch() != null) {
            elseBranch = convertExpression(ifElse.getElseBranch());
        } else {
            elseBranch = new UnitValue();
        }
        return IfExpr.create(condition, thenBranch, elseBranch);
    }

    private ChiNode convertFnExpr(Fn fn, String name) {
        var functionCallTarget = createFunctionWithName(fn, name);
        return new LambdaValue(functionCallTarget);
    }

    private ChiNode convertFnExpr(Fn fn) {
        return convertFnExpr(fn, "[lambda]");
    }

    private ChiNode convertModuleFunctionDefinitionFromFunctionNode(String name, ChiNode fnExprNode, TypeScheme type, boolean isPublic) {
        return DefinePackageFunctionFromNodeGen.create(fnExprNode, currentModule, currentPackage, name, type, isPublic);
    }

    private RootCallTarget createFunctionFromNode(ExpressionNode body, String name) {
        RootNode rootNode = withNewFunctionScope(
                () -> new FnRootNode(language, currentFdBuilder.build(), body, name));
        return rootNode.getCallTarget();
    }

    private RootCallTarget createFunctionFromNodeWithoutNewFrameDescriptor(ChiNode body, String name) {
        RootNode rootNode = new FnRootNode(language, currentFdBuilder.build(), body, name);
        return rootNode.getCallTarget();
    }

    private RootCallTarget createFunctionWithName(Fn fn, String name) {
        var rootNode = withNewFunctionScope(() -> {
            var argIndex = 0;
            for (FnParam parameter : fn.getParameters()) {
                fnArgs.put(parameter.getName(), argIndex++);
            }

            var body = (ExpressionNode) convertBlock(fn.getBody(), fn.getType());
            body.addRootTag();
            return new FnRootNode(language, currentFdBuilder.build(), body, name);
        });
        return rootNode.getCallTarget();
    }

    private <T> T withNewFunctionScope(Supplier<T> f) {
        var previousFdBuilder = currentFdBuilder;
        var previousLocalSlots = localSlots;
        var previousFnArgs = fnArgs;

        currentFdBuilder = FrameDescriptor.newBuilder();
        localSlots = new HashMap<>();
        fnArgs = new HashMap<>();


        var prevInside = insideFunction;
        insideFunction = true;
        var result = f.get();
        insideFunction = prevInside;

        currentFdBuilder = previousFdBuilder;
        localSlots = previousLocalSlots;
        fnArgs = previousFnArgs;

        return result;
    }

    private ChiNode convertFnCall(FnCall fnCall) {
        var functionExpr = fnCall.getFunction();
        Function fnType = (Function) functionExpr.getType();
        assert fnType != null;
        var argType = new ArrayList<>(fnType.getTypes());
        argType.remove(argType.size()-1); // remove return type

        ChiNode[] arguments = new ChiNode[fnCall.getParameters().size()];
        var i = 0;
        for (Expression parameter : fnCall.getParameters()) {
            arguments[i++] = convertExpression(parameter);
        }

        if (functionExpr instanceof VariableAccess variableAccess && variableAccess.getTarget() instanceof PackageSymbol symbol) {
            var function = new GetDefinedFunction(
                    symbol.getModuleName(),
                    symbol.getPackageName(),
                    symbol.getName(),
                    argType.toArray(new Type[0]));
            return new InvokeFunction(function, arguments);
        } else {
            var function = convertExpression(functionExpr);
            return new InvokeFunction(function, arguments);
        }
    }

    private ChiNode convertWhileExpr(WhileLoop whileLoop) {
        var condition = convertExpression(whileLoop.getCondition());
        var body = convertExpression(whileLoop.getLoop());
        return new WhileExprNode(condition, body);
    }

    private ChiNode convertIs(Is is) {
        return IsNodeGen.create(convertExpression(is.getValue()), is.getCheckedType());
    }

    private ChiNode convertEffectDefinition(EffectDefinition definition) {
        RootNode rootNode = withNewFunctionScope(
                () -> {
                    ChiNode[] body = {new InvokeEffect(definition.getModuleName(), definition.getPackageName(), definition.getName())};
                    var block = new BlockExpr(body);
                    return new FnRootNode(language, currentFdBuilder.build(), block, definition.getName());
                });
        var callTarget = rootNode.getCallTarget();
        var fnType = (Function) definition.getType();
        return new DefinePackageFunction(
                currentModule, currentPackage,
                new ChiFunction(callTarget),
                fnType,
                definition.getPublic()
        );
    }

    private ChiNode convertHandle(Handle handle) {
        var bodyInstructionNodes = handle.getBody().getBody().stream()
                                         .map(this::convertExpression).toArray(ChiNode[]::new);
        var bodyNode = new BlockExpr(bodyInstructionNodes);
        var handlers = new HashMap<EffectHandlers.Qualifier, ChiFunction>();
        for (HandleCase it : handle.getCases()) {
            var callTarget = withNewFunctionScope(() -> {
                AtomicInteger argIndex = new AtomicInteger();
                it.getArgumentNames().forEach(argName -> fnArgs.put(argName, argIndex.getAndIncrement()));

                var resumeSlot = currentFdBuilder.addSlot(FrameSlotKind.Illegal, "resume", null);
                localSlots.put("resume", resumeSlot);

                var resumeFunc = ResumeNode.createResumeFunction();
                var bodyNode2 = new BlockExpr(new ChiNode[]{
                        WriteLocalVariableNodeGen.create(
                                new LambdaValue(resumeFunc.getCallTarget()),
                                resumeSlot,
                                "resume"
                        ),
                        convertExpression(it.getBody())
                });
                return createFunctionFromNodeWithoutNewFrameDescriptor(bodyNode2, it.getEffectName());
            });
            handlers.put(
                    new EffectHandlers.Qualifier(it.getModuleName(), it.getPackageName(), it.getEffectName()),
                    new ChiFunction(callTarget));
        }
        return new HandleEffectNode(bodyNode, handlers);
    }
}
