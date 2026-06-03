package com.luxof.lapisworks;

import static com.luxof.lapisworks.Lapisworks.fmt;
import static com.luxof.lapisworks.Lapisworks.last;
import static com.luxof.lapisworks.Lapisworks.pop;

import com.mojang.datafixers.util.Either;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

/** <p>"Brain, can I has programming?"
 * <p>"To do amazing stuff that's not half-assing reimplementing desmos in Java, riiiiight?"
 * <p>"Riiiight."
 * <p>*half-asses reimplementing desmos in Java like a BOSS*
 */
public class LapisMathEngine {

    // :)
    // ...AND FOR MY NEXT TRICK I'LL PUT A PREPROCESSOR ON MY VS CODE EXTENSION--
    private static final Pattern EQUATION_REGEX = Pattern.compile("((?<!\\d)-)?\\d+(\\.\\d+)?|[+\\-*\\/^%(),]|([A-Za-z]+)(?=\\(.+\\))|.(_[^+\\-*\\/^(),]*)?|\\s+");
    private static final Pattern NUMBER_REGEX = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern OPERATOR_REGEX = Pattern.compile("[+\\-*\\/^%]");
    private static final Pattern VARIABLE_REGEX = Pattern.compile(".(_[^+\\-*\\/^(),]*)?");

    private static HashMap<String, List<String>> mathEquationCache = new HashMap<>();
    private static HashMap<String, Double> defaultConstants = new HashMap<>(Map.of(
        "π", Math.PI,
        "e", Math.E
    ));

    private static Function<Double[], Double> makeChainInfiniteOperator(
        BiFunction<Double, Double, Double> base
    ) {
        return args -> {
            boolean first = false;
            double buffer = 0.0;
            for (Double ele : args) {
                if (first) {
                    buffer = ele;
                    first = false;
                    continue;
                }

                buffer = base.apply(buffer, ele);
            }
            return buffer;
        };
    }

    private static Map<String, Integer> precedence = Map.of(
        "^", 5,
        "*", 4,
        "/", 4,
        "%", 4,
        "+", 3,
        "-", 3
    );

    private static Map<String, Integer> associative = Map.of(
        "^", 1,
        "*", 0,
        "/", 0,
        "%", 0,
        "+", 0,
        "-", 0
    );

    // like what the fuck is this type below me scoob
    private static Map<String, Map<Integer, Function<Double[], Double>>> functions = new HashMap<>();

    /** The <code>999</code> arity defines generic handling for any arity above the highest integer arity.
     * The <code>0</code> arity defines generic handling for any arity below the lowest integer arity. */
    private static void registerFunction(
        String name,
        Map<Integer, Function<Double[], Double>> usagePerArity
    ) {
        functions.put(name, usagePerArity);
    }

    private static void registerFunction(
        String name,
        int arity,
        Function<Double[], Double> usage
    ) {
        functions.put(name, Map.of(arity, usage));
    }

    static {
        registerFunction(
            "max",
            Map.ofEntries(
                makeEntry(2, args -> Math.max(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(Math::max))
            )
        );
        registerFunction(
            "min",
            Map.ofEntries(
                makeEntry(2, args -> Math.min(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(Math::min))
            )
        );
        registerFunction(
            "log",
            Map.of(
                1, args -> Math.log(args[0]),
                2, args -> logBase(args[0], args[1])
            )
        );

        registerFunction(
            "AND",
            Map.ofEntries(
                makeEntry(2, args -> AND(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(LapisMathEngine::AND))
            )
        );
        registerFunction(
            "OR",
            Map.ofEntries(
                makeEntry(2, args -> OR(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(LapisMathEngine::OR))
            )
        );
        registerFunction(
            "XOR",
            Map.ofEntries(
                makeEntry(2, args -> XOR(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(LapisMathEngine::XOR))
            )
        );
        registerFunction(
            "NAND",
            Map.ofEntries(
                makeEntry(2, args -> NAND(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(LapisMathEngine::NAND))
            )
        );
        registerFunction(
            "NOR",
            Map.ofEntries(
                makeEntry(2, args -> NOR(args[0], args[1])),
                makeEntry(999, makeChainInfiniteOperator(LapisMathEngine::NOR))
            )
        );
        registerFunction("NOT", 1, args -> NOT(args[0]));

        registerFunction("sqrt", 1, args -> Math.sqrt(args[0]));
        registerFunction("abs", 1, args -> Math.abs(args[0]));
        registerFunction("signum", 1, args -> Math.signum(args[0]));
        registerFunction("degrees", 1, args -> Math.toDegrees(args[0]));
        registerFunction("radians", 1, args -> Math.toRadians(args[0]));
        registerFunction("root", 2, args -> Math.pow(args[0], 1 / args[1]));
        registerFunction("random", 2, args -> Math.random() * (args[1] - args[0]) + args[0]);

        registerFunction("sin", 1, args -> Math.sin(args[0]));
        registerFunction("sinh", 1, args -> Math.sinh(args[0]));
        registerFunction("arcsin", 1, args -> Math.asin(args[0]));

        registerFunction("cos", 1, args -> Math.cos(args[0]));
        registerFunction("cosh", 1, args -> Math.cosh(args[0]));
        registerFunction("arccos", 1, args -> Math.acos(args[0]));

        registerFunction("tan", 1, args -> Math.tan(args[0]));
        registerFunction("tanh", 1, args -> Math.tanh(args[0]));
        registerFunction("arctan", 1, args -> Math.atan(args[0]));

        registerFunction("csc", 1, args -> 1.0 / Math.cos(args[0]));
        registerFunction("csch", 1, args -> 1.0 / Math.cosh(args[0]));
        registerFunction("arccsc", 1, args -> 1.0 / Math.acos(args[0]));

        registerFunction("sec", 1, args -> 1.0 / Math.sin(args[0]));
        registerFunction("sech", 1, args -> 1.0 / Math.sinh(args[0]));
        registerFunction("arcsec", 1, args -> 1.0 / Math.asin(args[0]));

        registerFunction("cot", 1, args -> 1.0 / Math.tan(args[0]));
        registerFunction("coth", 1, args -> 1.0 / Math.tanh(args[0]));
        registerFunction("arccot", 1, args -> 1.0 / Math.atan(args[0]));
    }


    public static List<String> shuntingYard(String raw) {
        if (mathEquationCache.containsKey(raw))
            return mathEquationCache.get(raw);

        Matcher matcher = EQUATION_REGEX.matcher(raw);
        if (raw.replaceAll(" ", "").isEmpty()) throw new RuntimeException(fmt(
            "LapisMathEngine: \"%s\" is not a valid equation: it's empty.",
            raw
        ));

        List<String> math = new ArrayList<>();

        // this is a weird ass way to do it, Java.
        int prev_end = 0;
        while (matcher.find()) {
            if (matcher.start() != prev_end) throw new RuntimeException(fmt(
                "LapisMathEngine: \"%s\" is not a valid equation: unknown symbols or functions.",
                raw
            ));
            prev_end = matcher.end();
            if (matcher.group().replaceAll(" ", "").isEmpty()) continue;
            math.add(matcher.group());
        }

        List<String> rpn = new ArrayList<>();
        List<String> opStack = new ArrayList<>();
        List<Integer> arityStack = new ArrayList<>();
        for (String token : math) {

            if (matches(NUMBER_REGEX, token))
                rpn.add(token);

            else if (matches(OPERATOR_REGEX, token)) {
                int tokenPrecedence = precedence.get(token);
                int tokenAssociativeness = associative.get(token);

                String topOp = opStack.size() > 0 ? last(opStack) : null;
                while (
                    topOp != null && !topOp.equals("(") &&
                    (
                        precedence.get(topOp) > tokenPrecedence ||
                        (precedence.get(topOp) == tokenPrecedence && tokenAssociativeness == 0)
                    )
                ) {
                    rpn.add(pop(opStack));
                    topOp = opStack.size() > 0 ? last(opStack) : null;
                }
                opStack.add(token);
            }

            else if (token.equals(",")) {
                String topOp = opStack.size() > 0 ? last(opStack) : null;
                while (topOp != null && !topOp.equals("(")) {
                    rpn.add(pop(opStack));
                    topOp = opStack.size() > 0 ? last(opStack) : null;
                }

                if (arityStack.size() == 0)
                    throw new RuntimeException(fmt(
                        "LapisMathEngine: \"%s\" is not a valid equation: comma found outside of function arguments.",
                        raw
                    ));
                arityStack.add(pop(arityStack) + 1);
            }

            else if (token.equals("("))
                opStack.add(token);

            else if (token.equals(")")) {

                if (opStack.size() == 0)
                    throw new RuntimeException(fmt(
                        "LapisMathEngine: \"%s\" is not a valid equation: mismatched parenthesis (missing left parenthesis).",
                        raw
                    ));

                String topOp = pop(opStack);
                while (!topOp.equals("(")) {

                    if (opStack.size() == 0)
                        throw new RuntimeException(fmt(
                            "LapisMathEngine: \"%s\" is not a valid equation: mismatched parenthesis (missing left parenthesis).",
                            raw
                        ));

                    rpn.add(topOp);
                    topOp = pop(opStack);

                }

                if (opStack.size() == 0)
                    continue;

                topOp = last(opStack);

                if (functions.containsKey(topOp)) {
                    if (arityStack.size() == 0)
                        throw new RuntimeException(fmt(
                            "LapisMathEngine: \"%s\" is not a valid equation: function %s was provided 0 arguments.",
                            raw,
                            topOp
                        ));
                    int arity = pop(arityStack);
                    var functionArities = functions.get(topOp).keySet();

                    int minArity = 99;
                    int maxArity = 0;
                    for (Integer contender : functionArities) {
                        if (contender == 999 || contender == 0) continue;
                        if (contender > maxArity)
                            maxArity = contender;
                        if (contender < minArity)
                            minArity = contender;
                    }

                    if (
                        !functionArities.contains(arity) &&
                        !(arity < minArity && functionArities.contains(0)) &&
                        !(arity > maxArity && functionArities.contains(999))
                    )
                        throw new RuntimeException(fmt(
                            "LapisMathEngine: \"%s\" is not a valid equation: function %s got %d arguments but can only take %s.",
                            raw,
                            topOp,
                            displayArities(functionArities)
                        ));

                    rpn.add(String.valueOf(arity));
                    rpn.add(pop(opStack));
                }
            }

            else if (functions.containsKey(token)) {
                arityStack.add(1);
                opStack.add(token);
            }

            else if (matches(VARIABLE_REGEX, token))
                // variable
                rpn.add(token);
        }

        int opsLeft = opStack.size();
        for (int i = 0; i < opsLeft; i++) {
            String operator = pop(opStack);
            if (operator.equals("("))
                throw new RuntimeException(fmt(
                    "LapisMathEngine: \"%s\" is not a valid equation: mismatched parenthesis (missing right parenthesis).",
                    raw
                ));
            rpn.add(operator);
        }

        mathEquationCache.put(raw, rpn);
        return rpn;
    }

    /** does NOT handle unknown variables. All variables must be defined or will throw. */
    public static double evaluate(
        List<String> postfix,
        Map<String, Number> variables
    ) {
        ArrayList<String> rpn = new ArrayList<>(postfix);
        rpn.replaceAll(str -> variables.containsKey(str) ? String.valueOf(variables.get(str)) : str);

        int idx = -1;
        while (true) {
            idx += 1;
            if (rpn.size() == 1) {
                if (!isDouble(rpn.get(0))) throw new RuntimeException(
                    fmt("LapisMathEngine: unknown variable (%s) found at end of evaluation.", rpn.get(0))
                );
                return Double.valueOf(rpn.get(0));

            } else if (idx > rpn.size()) throw new RuntimeException(
                fmt("LapisMathEngine: your equation is invalid-- evaluation has become... Difficult.")
            );

            String token = rpn.get(idx);

            if (matches(OPERATOR_REGEX, token)) {
                idx = evalStepOperatorFound(rpn, idx, token);
            }

            else if (functions.containsKey(token)) {
                idx = evalStepFunctionTokenFound(rpn, idx, token);
            }
        }
    }

    private static int evalStepOperatorFound(
        ArrayList<String> rpn,
        int idx,
        String token
    ) {
        if (idx < 2)
            throw new RuntimeException(
                fmt("LapisMathEngine: operator %s was not provided two arguments on either side.", token)
            );

        String string1 = rpn.remove(idx - 2);
        String string2 = rpn.remove(idx - 2);

        if (!isDouble(string1) || !isDouble(string2)) {
            throw new RuntimeException(
                fmt("LapisMathEngine: unknown variable (%s) found in equation.", !isDouble(string1) ? string1 : string2)
            );
        }

        double arg1 = Double.valueOf(string1);
        double arg2 = Double.valueOf(string2);
        idx -= 2;
        rpn.set(idx, String.valueOf(switch (token) {
            case "+" -> arg1 + arg2;
            case "-" -> arg1 - arg2;
            case "*" -> arg1 * arg2;
            case "/" -> arg1 / arg2;
            case "^" -> Math.pow(arg1, arg2);
            default -> 0;
        }));

        return idx;
    }

    private static int evalStepFunctionTokenFound(
        ArrayList<String> rpn,
        int idx,
        String token
    ) {
        int arity = Integer.valueOf(rpn.remove(idx - 1));
        boolean stillADoubleDream = true;

        Double[] args = new Double[arity];

        for (int i = 0; i < arity; i++) {
            String arg = rpn.remove(idx - 2 - i);
            if (!isDouble(arg)) throw new RuntimeException(
                fmt("Unknown variable (%s) found in equation.", arg)
            );
            args[arity - 1 - i] = Double.valueOf(arg);
        }

        double result = getFunctionForArity(functions.get(token), arity).apply(args);

        idx -= arity + 1;
        rpn.set(idx, String.valueOf(result));

        return idx;
    }

    /** tries to math. returns a number or an error message. */
    public static Either<Double, String> tryMath(String expr, Map<String, Number> variables) {
        try {
            return Either.left(evaluate(shuntingYard(expr), variables));
        } catch (RuntimeException e) {
            return Either.right(e.getMessage());
        }
    }



    private static final Double AND(Double a, Double b) {
        return Double.longBitsToDouble(Double.doubleToLongBits(a) & Double.doubleToLongBits(b));
    }
    private static final Double OR(Double a, Double b) {
        return Double.longBitsToDouble(Double.doubleToLongBits(a) | Double.doubleToLongBits(b));
    }
    private static final Double NOT(Double a) {
        return Double.longBitsToDouble(~Double.doubleToLongBits(a));
    }
    private static final Double XOR(Double a, Double b) {
        return Double.longBitsToDouble(Double.doubleToLongBits(a) ^ Double.doubleToLongBits(b));
    }
    private static final Double NAND(Double a, Double b) {
        return NOT(AND(a, b));
    }
    private static final Double NOR(Double a, Double b) {
        return NOT(OR(a, b));
    }

    private static final boolean matches(Pattern regex, String performOn) {
        return regex.matcher(performOn).matches();
    }

    private static double logBase(double number, double base) {
        return Math.log(number) / Math.log(base);
    }

    private static boolean isDouble(String possiblyANum) {
        try {
            Double.valueOf(possiblyANum);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static Function<Double[], Double> getFunctionForArity(
        Map<Integer, Function<Double[], Double>> function,
        int arity
    ) {
        int max = 0;
        int min = 999;
        for (Integer contender : function.keySet()) {
            if (contender == 0 || contender == 999) continue;
            if (contender < min) min = contender;
            if (contender > max) max = contender;
        }

        return !function.containsKey(arity)
            ? arity < min
                ? function.get(0)
                : arity > max
                    ? function.get(999)
                    : null // unreachable case (shunting yard handles this)
            : function.get(arity);
    }

    private static String displayArities(Collection<Integer> arities) {
        List<Integer> usuals = arities.stream().filter(n -> n != 0 && n != 999).toList();
        String ret = "";
        for (Integer i : usuals.subList(0, usuals.size() - 1)) {
            ret += i + ", ";
        }

        boolean belowMin = arities.contains(0);
        boolean aboveMax = arities.contains(999);
        if (belowMin || aboveMax) {
            ret += usuals.get(usuals.size() - 1) + ", ";
            ret += belowMin && aboveMax
                ? "or below the minimum and above the maximum present here."
                : "or " + (belowMin ? "below the minimum" : "above the maximum") + " present here.";
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, Function<Double[], Double>> makeMap(
        Object... stuff
    ) {
        Map<Integer, Function<Double[], Double>> map = new HashMap<>();
        Integer first = 999;
        boolean second = false;
        for (Object obj : stuff) {
            if (!second) {
                first = (Integer)obj;
                second = true;
            } else {
                map.put(first, (Function<Double[], Double>)obj);
                second = false;
            }
        }
        return map;
    }

    private static Entry<Integer, Function<Double[], Double>> makeEntry(
        Integer arity,
        Function<Double[], Double> func
    ) {
        return entry(arity, func);
    }
}
