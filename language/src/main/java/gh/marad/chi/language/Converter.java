package gh.marad.chi.language;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import gh.marad.chi.core.CompilationDefaults;
import gh.marad.chi.core.Expression;
import gh.marad.chi.core.Program;
import gh.marad.chi.core.namespace.TypeInfo;
import gh.marad.chi.core.namespace.VariantField;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.ProductType;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.TypeVariable;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.nodes.function.DefinePackageFunction;
import gh.marad.chi.language.nodes.objects.ConstructChiObject;
import gh.marad.chi.language.nodes.value.UnitValue;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.TODO;

import java.util.ArrayList;
import java.util.Map;
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

        for (TypeInfo typeInfo : program.getDefinedTypes()) {
            body.add(defineType(typeInfo));
            if (typeInfo.getType() instanceof ProductType) {
                body.add(defineConstructor(typeInfo));
            }
        }

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

    private ChiNode defineType(TypeInfo typeInfo) {
        throw new TODO("Define the type in package");
    }

    private ChiNode defineConstructor(TypeInfo typeInfo) {

        var fieldNames = typeInfo.getFields().stream().map(VariantField::getName).toList().toArray(new String[0]);
        var constructorFunction = createFunctionFromNode(
                new ConstructChiObject((ProductType) typeInfo.getType(), fieldNames), typeInfo.getName());
        if (typeInfo.getFields().isEmpty()) {
            throw new TODO("uncomment");
//            return WriteModuleVariableNodeGen.create(
//                    new InvokeFunction(new LambdaValue(constructorFunction), new ChiNode[0]),
//                    typeInfo.getModuleName(),
//                    typeInfo.getPackageName(),
//                    typeInfo.getName(),
//                    typeInfo.getType(),
//                    typeInfo.isPublic(),
//                    true
//            );
        } else {
            var types = new ArrayList<Type>();
            for (VariantField field : typeInfo.getFields()) {
                types.add(field.getType());
            }
            types.add(typeInfo.getType());
            var fnType = new FunctionType(
                    types,
                    types.stream()
                         .filter(it -> it instanceof TypeVariable)
                         .map(it -> (TypeVariable) it)
                         .toList()
            );
            return new DefinePackageFunction(
                    typeInfo.getModuleName(),
                    typeInfo.getPackageName(),
                    new ChiFunction(constructorFunction),
                    fnType,
                    typeInfo.isPublic()
            );
        }
    }

    public ChiNode convertExpression(Expression expr) {
//        if (expr instanceof Atom atom) {
//            return convertAtom(atom);
//        } else if (expr instanceof InterpolatedString interpolatedString) {
//            return convertInterpolatedString(interpolatedString);
//        } else if (expr instanceof NameDeclaration nameDeclaration) {
//            return convertNameDeclaration(nameDeclaration);
//        } else if (expr instanceof VariableAccess variableAccess) {
//            return convertVariableAccess(variableAccess);
//        } else if (expr instanceof FieldAccess fieldAccess) {
//            return convertFieldAccess(fieldAccess);
//        } else if (expr instanceof FieldAssignment assignment) {
//            return convertFieldAssignment(assignment);
//        } else if (expr instanceof Block block) {
//            return convertBlock(block);
//        } else if (expr instanceof InfixOp infixOp) {
//            return convertInfixOp(infixOp);
//        } else if (expr instanceof PrefixOp prefixOp) {
//            return convertPrefixOp(prefixOp);
//        } else if (expr instanceof Cast cast) {
//            return convertCast(cast);
//        } else if (expr instanceof IfElse ifElse) {
//            return convertIfExpr(ifElse);
//        } else if (expr instanceof Fn fn) {
//            return convertFnExpr(fn);
//        } else if (expr instanceof FnCall fnCall) {
//            return convertFnCall(fnCall);
//        } else if (expr instanceof Group group) {
//            return convertExpression(group.getValue());
//        } else if (expr instanceof Assignment assignment) {
//            return convertAssignment(assignment);
//        } else if (expr instanceof WhileLoop whileLoop) {
//            return convertWhileExpr(whileLoop);
//        } else if (expr instanceof Break) {
//            return new WhileBreakNode();
//        } else if (expr instanceof Continue) {
//            return new WhileContinueNode();
//        } else if (expr instanceof IndexOperator op) {
//            return IndexOperatorNodeGen.create(
//                    convertExpression(op.getVariable()),
//                    convertExpression(op.getIndex())
//            );
//        } else if (expr instanceof IndexedAssignment op) {
//            return IndexedAssignmentNodeGen.create(
//                    convertExpression(op.getVariable()),
//                    convertExpression(op.getIndex()),
//                    convertExpression(op.getValue())
//            );
//        } else if (expr instanceof Is is) {
//            return convertIs(is);
//        } else if (expr instanceof EffectDefinition definition) {
//            return convertEffectDefinition(definition);
//        } else if (expr instanceof Handle handle) {
//            return convertHandle(handle);
//        } else if (expr instanceof Return ret) {
//            if (ret.getValue() != null) {
//                return new ReturnNode(convertExpression(ret.getValue()));
//            } else {
//                return ReturnUnitNode.instance;
//            }
//        }

        throw new TODO("Unhandled expression conversion: %s".formatted(expr));
    }
//
//    private ChiNode convertInterpolatedString(InterpolatedString interpolatedString) {
//        var nodes = interpolatedString.getParts().stream()
//                                      .map(this::convertExpression)
//                                      .toList();
//        return new BuildInterpolatedString(nodes.toArray(new ChiNode[0]));
//    }
//
//    private ChiNode convertAtom(Atom atom) {
//        if (atom.getType() == Types.getInt()) {
//            return new LongValue(Long.parseLong(atom.getValue()));
//        }
//        if (atom.getType() == Types.getFloat()) {
//            return new FloatValue(Float.parseFloat(atom.getValue()));
//        }
//        if (atom.getType() == Types.getString()) {
//            return new StringValue(atom.getValue());
//        }
//        if (atom.getType() == Types.getBool()) {
//            return new BooleanValue(Boolean.parseBoolean(atom.getValue()));
//        }
//        throw new TODO("Unhandled atom type: %s".formatted(atom.getType()));
//    }
//
//    private ChiNode convertNameDeclaration(NameDeclaration nameDeclaration) {
//        if (!insideFunction && nameDeclaration.getValue() instanceof Fn fn) {
//            return convertModuleFunctionDefinitionFromFunctionNode(
//                    nameDeclaration.getName(),
//                    convertFnExpr(fn, nameDeclaration.getName()),
//                    (FunctionType) fn.getType(),
//                    nameDeclaration.getPublic()
//            );
//        } else if (!insideFunction && nameDeclaration.getValue().getType() instanceof FunctionType fnType) {
//            return convertModuleFunctionDefinitionFromFunctionNode(
//                    nameDeclaration.getName(),
//                    convertExpression(nameDeclaration.getValue()),
//                    fnType,
//                    nameDeclaration.getPublic()
//            );
//        } else if (!insideFunction) {
//            return DefineModuleVariableNodeGen.create(
//                    convertExpression(nameDeclaration.getValue()),
//                    currentModule, currentPackage, nameDeclaration.getName(),
//                    nameDeclaration.getValue().getType(),
//                    nameDeclaration.getPublic(),
//                    nameDeclaration.getMutable()
//                    );
//        } else {
//            int slot = currentFdBuilder.addSlot(FrameSlotKind.Illegal, nameDeclaration.getName(), null);
//            localSlots.put(nameDeclaration.getName(), slot);
//            ChiNode valueExpr = convertExpression(nameDeclaration.getValue());
//            return DefineModuleVariableNodeGen.create(valueExpr, slot, nameDeclaration.getName());
//        }
//    }
//
//    private ChiNode convertVariableAccess(VariableAccess variableAccess) {
//        var target = variableAccess.getTarget();
//        if (target instanceof PackageSymbol symbol) {
//            return new ReadModuleVariable(
//                    symbol.getModuleName(),
//                    symbol.getPackageName(),
//                    symbol.getName());
//        } else if (target instanceof LocalSymbol symbol) {
//            var localSlot = localSlots.get(symbol.getName());
//            if (localSlot != null) {
//                return new ReadLocalVariable(symbol.getName(), localSlot);
//            }
//            var argNum = fnArgs.get(symbol.getName());
//            if (argNum != null) {
//                return new ReadLocalArgument(argNum);
//            }
//        }
//        throw new TODO("Invalid variable access: " + variableAccess);
//    }
//
//    private ChiNode convertFieldAccess(FieldAccess fieldAccess) {
//        return ReadMemberNodeGen.create(convertExpression(fieldAccess.getReceiver()), fieldAccess.getFieldName());
//    }
//
//    private ChiNode convertFieldAssignment(FieldAssignment assignment) {
//        var receiver = convertExpression(assignment.getReceiver());
//        var value = convertExpression(assignment.getValue());
//        return WriteMemberNodeGen.create(receiver, value, assignment.getFieldName());
//    }
//
//    private ChiNode convertAssignment(Assignment assignment) {
//        var target = assignment.getTarget();
//
//        if (target instanceof PackageSymbol symbol) {
//            WriteModuleVariableNodeGen.create(
//                    convertExpression(assignment.getValue()),
//                    symbol.getModuleName(),
//                    symbol.getPackageName(),
//                    symbol.getName(),
//                    assignment.getValue().getType(),
//
//            , )
//        }
//
//        var scope = assignment.getDefinitionScope();
//        var symbolInfo = scope.getSymbol(assignment.getName(), true);
////        assert symbolInfo != null : "Symbol not found for local '%s'".formatted(assignment.getName());
//        if (symbolInfo == null) {
//            throw new TODO("Symbol not found for local '%s'".formatted(assignment.getName()));
//        }
//        if (symbolInfo.getScopeType() == ScopeType.Package) {
//            return WriteModuleVariableNodeGen.create(
//                    convertExpression(assignment.getValue()),
//                    currentModule,
//                    currentPackage,
//                    assignment.getName(),
//                    assignment.getValue().getType(),
//                    symbolInfo.getPublic(),
//                    symbolInfo.getMutable()
//            );
//        } else if (symbolInfo.getSymbolType() == SymbolType.Local) {
//            assert symbolInfo.getSlot() != -1 : "Slot for local '%s' was not set up!".formatted(assignment.getName());
//            if (scope.containsInNonVirtualScope(assignment.getName())) {
//                return WriteLocalVariableNodeGen.create(
//                        convertExpression(assignment.getValue()),
//                        symbolInfo.getSlot(),
//                        assignment.getName());
//            } else {
//                return WriteOuterVariableNodeGen.create(
//                        convertExpression(assignment.getValue()),
//                        assignment.getName()
//                );
//            }
//        } else if (symbolInfo.getSymbolType() == SymbolType.Argument) {
//            assert symbolInfo.getSlot() != -1 : "Slot for local '%s' was not set up!".formatted(assignment.getName());
//            return WriteLocalArgumentNodeGen.create(
//                    convertExpression(assignment.getValue()),
//                    symbolInfo.getSlot()
//            );
//        }
//        throw new TODO("This should not happen");
//    }
//
//    private ChiNode convertBlock(Block block) {
//        return convertBlock(block, null, null, null);
//    }
//
//    private ChiNode convertBlock(Block block, Type returnType, List<FnParam> fnParams, CompilationScope compilationScope) {
//        if (fnParams != null) {
//            assert compilationScope != null : "Compilation scope cannot be null if fnParams is not null!";
//            var argIndex = 0;
//            for (var param : fnParams) {
//                var symbol = compilationScope.getSymbol(param.getName(), true);
//                assert symbol != null : "Symbol not found for argument %s".formatted(param.getName());
//                assert symbol.getSymbolType() == SymbolType.Argument : String.format("Symbol '%s' is not an argument", param.getName());
//                compilationScope.updateSlot(param.getName(), argIndex);
//                argIndex += 1;
//            }
//        }
//
//        var body = new ArrayList<>(block.getBody().stream().map(this::convertExpression).toList());
//        if (returnType == Type.getUnit()) {
//            body.add(new UnitValue());
//        }
//        return new BlockExpr(body.toArray(new ChiNode[0]));
//    }
//
//    private ChiNode convertInfixOp(InfixOp infixOp) {
//        var left = convertExpression(infixOp.getLeft());
//        var right = convertExpression(infixOp.getRight());
//        return switch (infixOp.getOp()) {
//            case "+" -> PlusOperatorNodeGen.create(left, right);
//            case "-" -> MinusOperatorNodeGen.create(left, right);
//            case "*" -> MultiplyOperatorNodeGen.create(left, right);
//            case "/" -> DivideOperatorNodeGen.create(left, right);
//            case "%" -> ModuloOperatorNodeGen.create(left, right);
//            case "==" -> EqualOperatorNodeGen.create(left, right);
//            case "!=" -> NotEqualOperatorNodeGen.create(left, right);
//            case "<" -> LessThanOperatorNodeGen.create(false, left, right);
//            case "<=" -> LessThanOperatorNodeGen.create(true, left, right);
//            case ">" -> GreaterThanOperatorNodeGen.create(false, left, right);
//            case ">=" -> GreaterThanOperatorNodeGen.create(true, left, right);
//            case "&&" -> new LogicAndOperator(left, right);
//            case "||" -> new LogicOrOperator(left, right);
//            case "&" -> BitAndOperatorNodeGen.create(left, right);
//            case "|" -> BitOrOperatorNodeGen.create(left, right);
//            case "<<" -> ShlOperatorNodeGen.create(left, right);
//            case ">>" -> ShrOperatorNodeGen.create(left, right);
//            default -> throw new TODO("Unhandled infix operator: '%s'".formatted(infixOp.getOp()));
//        };
//    }
//
//    @SuppressWarnings("SwitchStatementWithTooFewBranches")
//    private ChiNode convertPrefixOp(PrefixOp prefixOp) {
//        var value = convertExpression(prefixOp.getExpr());
//        return switch (prefixOp.getOp()) {
//            case "!" -> LogicNotOperatorNodeGen.create(value);
//            default -> throw new TODO("Unhandled prefix operator: '%s'".formatted(prefixOp.getOp()));
//        };
//    }
//
//    private ChiNode convertCast(Cast cast) {
//        var value = convertExpression(cast.getExpression());
//        if (cast.getTargetType() == Type.getIntType()) {
//            return CastToLongExprNodeGen.create(value);
//        } else if (cast.getTargetType() == Type.getFloatType()) {
//            return CastToFloatNodeGen.create(value);
//        } else if (cast.getTargetType() == Type.getString()) {
//            return CastToStringNodeGen.create(value);
//        } else if (cast.getTargetType().isCompositeType()) {
//            return value;
//        } else {
//            return value;
//        }
//    }
//
//    private ChiNode convertIfExpr(IfElse ifElse) {
//        var condition = convertExpression(ifElse.getCondition());
//        var thenBranch = convertExpression(ifElse.getThenBranch());
//        ChiNode elseBranch;
//        if (ifElse.getElseBranch() != null) {
//            elseBranch = convertExpression(ifElse.getElseBranch());
//        } else {
//            elseBranch = new UnitValue();
//        }
//        return IfExpr.create(condition, thenBranch, elseBranch);
//    }
//
//    private ChiNode convertFnExpr(Fn fn, String name) {
//        var functionCallTarget = createFunctionWithName(fn, name);
//        return new LambdaValue(functionCallTarget);
//    }
//
//    private ChiNode convertFnExpr(Fn fn) {
//        return convertFnExpr(fn, "[lambda]");
//    }
//
//    private ChiNode convertModuleFunctionDefinitionFromFunctionNode(String name, ChiNode fnExprNode, FunctionType type, boolean isPublic) {
//        return DefinePackageFunctionFromNodeGen.create(fnExprNode, currentModule, currentPackage, name, type, isPublic);
//    }
//
    private RootCallTarget createFunctionFromNode(ExpressionNode body, String name) {
        RootNode rootNode = withNewFrameDescriptor(
                () -> new FnRootNode(language, currentFdBuilder.build(), body, name));
        return rootNode.getCallTarget();
    }

    private RootCallTarget createFunctionFromNodeWithoutNewFrameDescriptor(ChiNode body, String name) {
        RootNode rootNode = new FnRootNode(language, currentFdBuilder.build(), body, name);
        return rootNode.getCallTarget();
    }

//    private RootCallTarget createFunctionWithName(Fn fn, String name) {
//        var rootNode = withNewFrameDescriptor(() -> {
//            var body = (ExpressionNode) convertBlock(fn.getBody(), fn.getReturnType(), fn.getParameters(), fn.getFnScope());
//            body.addRootTag();
//            return new FnRootNode(language, currentFdBuilder.build(), body, name);
//        });
//        return rootNode.getCallTarget();
//    }
//
    private <T> T withNewFrameDescriptor(Supplier<T> f) {
        var previousFdBuilder = currentFdBuilder;
        currentFdBuilder = FrameDescriptor.newBuilder();
        var result = f.get();
        currentFdBuilder = previousFdBuilder;
        return result;
    }

//    private ChiNode convertFnCall(FnCall fnCall) {
//        var functionExpr = fnCall.getFunction();
//        FnType fnType;
//        if (functionExpr.getType() instanceof OverloadedFnType overloaded) {
//            fnType = overloaded.getType(fnCall.getParameters().stream().map(Expression::getType).toList());
//        } else if (functionExpr.getType() instanceof FnType type) {
//            fnType = type;
//        } else {
//            throw new TODO("This is not a function type %s".formatted(functionExpr.getType()));
//        }
//        assert fnType != null;
//        var paramTypes = fnType.getParamTypes().toArray(new Type[0]);
//        ChiNode[] parameters = new ChiNode[fnCall.getParameters().size()];
//        var i = 0;
//        for (Expression parameter : fnCall.getParameters()) {
//            parameters[i++] = convertExpression(parameter);
//        }
//        if (functionExpr instanceof VariableAccess variableAccess) {
//            var scope = variableAccess.getDefinitionScope();
//            var symbol = scope.getSymbol(variableAccess.getName(), true);
//            assert symbol != null : "Symbol not found for name %s".formatted(variableAccess.getName());
//            var symbolType = symbol.getSymbolType();
//            if (symbol.getScopeType() == ScopeType.Package) {
//                var function = new GetDefinedFunction(
//                        variableAccess.getModuleName(),
//                        variableAccess.getPackageName(),
//                        variableAccess.getName(),
//                        paramTypes);
//                return new InvokeFunction(function, parameters);
//            } else if (symbolType == SymbolType.Local || symbolType == SymbolType.Argument) {
//                var function = convertExpression(functionExpr);
//                return new InvokeFunction(function, parameters);
//            } else {
//                throw new TODO("Dedicated error here. You should not be here!");
//            }
//        } else {
//            var function = convertExpression(functionExpr);
//            return new InvokeFunction(function, parameters);
//        }
//    }
//
//    private ChiNode convertWhileExpr(WhileLoop whileLoop) {
//        var condition = convertExpression(whileLoop.getCondition());
//        var body = convertExpression(whileLoop.getLoop());
//        return new WhileExprNode(condition, body);
//    }
//
//    private ChiNode convertIs(Is is) {
//        return IsNodeGen.create(convertExpression(is.getValue()), is.getTypeOrVariant());
//    }
//
//    private ChiNode convertEffectDefinition(EffectDefinition definition) {
//        RootNode rootNode = withNewFrameDescriptor(
//                () -> {
//                    ChiNode[] body = {new InvokeEffect(definition.getModuleName(), definition.getPackageName(), definition.getName())};
//                    var block = new BlockExpr(body);
//                    return new FnRootNode(language, currentFdBuilder.build(), block, definition.getName());
//                });
//        var callTarget = rootNode.getCallTarget();
//        var fnType = (FnType) definition.getType();
//        return new DefinePackageFunction(
//                currentModule, currentPackage,
//                new ChiFunction(callTarget),
//                fnType,
//                definition.getPublic()
//        );
//    }
//
//    private ChiNode convertHandle(Handle handle) {
//        var bodyInstructionNodes = handle.getBody().getBody().stream()
//                                         .map(this::convertExpression).toArray(ChiNode[]::new);
//        var bodyNode = new BlockExpr(bodyInstructionNodes);
//        var handlers = handle.getCases().stream()
//                             .map(it -> {
//                                 var callTarget = withNewFrameDescriptor(() -> {
//
//                                     AtomicInteger argIndex = new AtomicInteger();
//                                     it.getArgumentNames().forEach(argName -> it.getScope().updateSlot(argName, argIndex.getAndIncrement()));
//
//                                     var resumeSlot = currentFdBuilder.addSlot(FrameSlotKind.Illegal, "resume", null);
//                                     it.getScope().updateSlot("resume", resumeSlot);
//
//                                     var resumeFunc = ResumeNode.createResumeFunction();
//                                     var bodyNode2 = new BlockExpr(new ChiNode[]{
//                                             WriteLocalVariableNodeGen.create(
//                                                     new LambdaValue(resumeFunc.getCallTarget()),
//                                                     resumeSlot,
//                                                     "resume"
//                                             ),
//                                             convertExpression(it.getBody())
//                                     });
//                                     return createFunctionFromNodeWithoutNewFrameDescriptor(bodyNode2, it.getEffectName());
//                                 });
//                                 return new Pair<>(
//                                         new EffectHandlers.Qualifier(it.getModuleName(), it.getPackageName(), it.getEffectName()),
//                                         new ChiFunction(callTarget));
//                             })
//                             .collect(Collectors.toMap(Pair::first, Pair::second));
//
//        return new HandleEffectNode(bodyNode, handlers);
//    }

    record Pair<T, U>(T first, U second) {
    }

}
