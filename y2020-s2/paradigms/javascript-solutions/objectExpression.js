"use strict";

/*------------ HW6 ------------*/

const primitive = {
    simplify: function () {
        return this
    },
    is: function (constructor) {
        return (this instanceof constructor)
    },
    prefix: function () {
        return this.toString()
    },
    postfix: function () {
        return this.toString()
    }
}

function Const(val) {
    this.val = val;
}

Const.prototype = Object.create(primitive);
Const.prototype.toString = function () {
    return this.val.toString()
};
Const.prototype.evaluate = function () {
    return this.val
};
Const.prototype.diff = () => ZERO;
Const.prototype.equals = function (o) {
    return (o instanceof Const) && this.val === o.val
};
const ZERO = new Const(0);
const ONE = new Const(1);
const TWO = new Const(2);
const THREE = new Const(3);

const VARIABLES = ["x", "y", "z"];

function Variable(name) {
    this.name = name;
    // if(!VARIABLES.includes(name)) throw new UnknownVariableError(name);
    this.index = VARIABLES.indexOf(name);
}

Variable.prototype = Object.create(primitive);
Variable.prototype.toString = function () {
    return this.name
};
Variable.prototype.evaluate = function (...args) {
    return args[this.index]
};
Variable.prototype.diff = function (v) {
    return this.name === v ? ONE : ZERO
};
Variable.prototype.equals = function (o) {
    return (o instanceof Variable) && this.name === o.name
};

const OPERATIONS = new Map();

function AbstractOperation(...operands) {
    this.operands = operands;
}

AbstractOperation.prototype = {
    is: function (constructor) {
        return (this instanceof constructor)
    },
    toString: function () {
        return this.operands.join(" ") + " " + this.sign;
    },
    evaluate: function (...args) {
        return this.action(...this.operands.map(operand => operand.evaluate(...args)));
    },
    diff: function (v) {
        return this.ruleDiff(...this.operands.concat(this.operands.map(operand => operand.diff(v))));
    },
    simplify: function () {
        return this.ruleSimplify(...this.operands.map(operand => operand.simplify()));
    },
    equals: function (o) {
        return (o instanceof AbstractOperation) && (o.sign === this.sign) &&
            (this.operands.length === o.operands.length && this.operands.every((v, i) => v === o.operands[i]));
    },
    prefix: function () {
        return "(" + this.sign + " " + this.operands.map(operand => operand.prefix()).join(" ") + ")";
    },
    postfix: function () {
        return "(" + this.operands.map(operand => operand.postfix()).join(" ") + " " + this.sign + ")";
    }
}

function Operation(action, sign, ruleDiff, ruleSimplify) {
    this.action = action;
    this.sign = sign;
    this.ruleDiff = ruleDiff;
    this.ruleSimplify = ruleSimplify;
}

Operation.prototype = AbstractOperation.prototype;

function createOperation(action, sign, ruleDiff, ruleSimplify) {
    let size = action.length;
    let Op = function (...operands) {
        if (size > 0) {
            if (operands.length < size) throw new WrongNumberOfOperandsError(sign, size, operands.size, operands);
            AbstractOperation.call(this, ...operands.slice(operands.length - size, operands.length));
        } else {
            AbstractOperation.call(this, ...operands);
        }
    };
    Op.prototype = new Operation(action, sign, ruleDiff, ruleSimplify);
    Op.opCount = size;
    OPERATIONS.set(sign, Op);
    return Op;
}

const Add = createOperation((a, b) => a + b, "+", (a, b, ad, bd) => new Add(ad, bd), (a, b) => {
    if (a.equals(ZERO)) return b;
    if (b.equals(ZERO)) return a;
    if (a.is(Const) && b.is(Const)) return new Const(a.val + b.val);
    return new Add(a, b);
});

const Subtract = createOperation((a, b) => a - b, "-", (a, b, ad, bd) => new Subtract(ad, bd), (a, b) => {
    if (a.equals(ZERO)) {
        if (b.is(Const)) return new Const(-b.val);
        else return new Negate(b);
    }
    if (b.equals(ZERO)) return a;
    if (a.is(Const) && b.is(Const)) return new Const(a.val - b.val);
    return new Subtract(a, b);
});

const Multiply = createOperation((a, b) => a * b, "*",
    (a, b, ad, bd) => new Add(new Multiply(ad, b), new Multiply(a, bd)), (a, b) => {
        if (a.equals(ZERO) || b.equals(ZERO)) return ZERO;
        if (a.equals(ONE)) return b;
        if (b.equals(ONE)) return a;
        if (a.is(Const) && b.is(Const)) return new Const(a.val * b.val);
        return new Multiply(a, b);
    });

const Divide = createOperation((a, b) => a / b, "/",
    (a, b, ad, bd) => new Divide(new Subtract(new Multiply(ad, b), new Multiply(a, bd)), new Multiply(b, b)), (a, b) => {
        if (a.equals(ZERO)) return ZERO;
        if (b.equals(ONE)) return a;
        if (a.is(Const) && b.is(Const)) return new Const(a.val / b.val);
        if (a.is(Multiply)) {
            if (b.equals(a.operands[0])) return a.operands[1];
            if (b.equals(a.operands[1])) return a.operands[0];
        }
        if (b.is(Multiply)) {
            if (a.equals(b.operands[0])) return new Divide(ONE, b.operands[1]);
            if (a.equals(b.operands[1])) return new Divide(ONE, b.operands[0]);
        }
        return new Divide(a, b);
    });

const Negate = createOperation(a => -a, "negate", (a, ad) => new Negate(ad), a => {
    if (a.is(Negate)) return a.operands[0];
    if (a.is(Const)) {
        if (a.equals(ZERO)) return ZERO;
        else return new Const(-a.val);
    }
    return new Negate(a);
});

const Cube = createOperation(a => Math.pow(a, 3), "cube",
    (a, ad) => new Multiply(new Multiply(THREE, ad), new Multiply(a, a)), a => {
        if (a.is(Cbrt)) return a.operands[0];
        return a.is(Const) ? new Const(Math.pow(a.val, 3)) : new Cube(a)
    });

const Cbrt = createOperation(Math.cbrt, "cbrt",
    (a, ad) => new Divide(ad, new Multiply(THREE, new Cbrt(new Multiply(a, a)))), a => {
        if (a.is(Cube)) return a.operands[0];
        return a.is(Const) ? new Const(Math.cbrt(a.val)) : new Cbrt(a)
    });

// Parser in HW6
const parseOld = (expr) => {
    let stack = [];
    expr.split(" ").filter(x => x !== "").forEach(token => {
        if (OPERATIONS.has(token)) {
            let Op = OPERATIONS.get(token);
            let operation = new Op(...stack);
            stack.splice(-Op.opCount);
            stack.push(operation);
        } else if (VARIABLES.includes(token)) {
            stack.push(new Variable(token));
        } else {
            stack.push(new Const(parseInt(token)));
        }
    });
    return stack.pop();
};
const parse = parseOld;

/*------------ HW7 ------------*/

const diffSumsq = (half, ...args) => {
    if (args.length === 0) {
        return ZERO;
    }
    let o = args.slice(0, args.length / 2), od = args.slice(args.length / 2, args.length);
    // :NOTE: map
    for (let i = 0; i < o.length; i++) {
        o[i] = half ? new Multiply(od[i], o[i]) : new Multiply(TWO, new Multiply(od[i], o[i]));
    }
    // :NOTE: reduce
    for (let i = 1; i < o.length; i++) {
        o[0] = new Add(o[0], o[i]);
    }
    return o[0];
};

const Sumsq = createOperation(
    (...operands) => operands.length === 0 ? 0 : operands.map(a => a * a).reduce((a, b) => a + b), "sumsq",
    (...args) => diffSumsq(false, ...args), (...operands) => new Sumsq(...operands)
);

const Length = createOperation(
    (...operands) => operands.length === 0 ? 0 : Math.sqrt(operands.map(a => a * a).reduce((a, b) => a + b)), "length",
    (...args) => args.length === 0 ? ZERO : new Divide(diffSumsq(true, ...args), new Length(...args.slice(0, args.length / 2))),
    (...operands) => new Length(...operands)
);

const ParseMode = {prefix: true, postfix: false};

function parseGeneral(tokens, mode) {
    function checkedNext() {
        if (!tokens.hasNext()) {
            throw new IncompleteExpressionError(tokens.location());
        }
        return tokens.next();
    }

    let token = checkedNext();
    if (token === '(') {
        let args = [];
        while (tokens.hasNext()) {
            if (OPERATIONS.has(tokens.getNext())) {
                break;
            }
            args.push(parseGeneral(tokens, mode));
        }
        if (mode) {
            args.reverse();
        }

        token = checkedNext();
        let Op = OPERATIONS.get(token);
        if (Op.opCount !== 0 && Op.opCount !== args.length) {
            throw new WrongNumberOfOperandsError(token, Op.opCount, args.length, tokens.location());
        }
        token = checkedNext();
        if (token !== ')') {
            // :NOTE: ??
            throw new SyntaxError(token, tokens.getPrev(), ')', tokens.location());
        }
        return new Op(...args);
    }
    if (token === ')' || OPERATIONS.has(token)) {
        throw new SyntaxError(token, tokens.getPrev() ? tokens.getPrev() : "start", "operand or '('", tokens.location());
    } else if (VARIABLES.includes(token)) {
        return new Variable(token);
    } else if (token[0] === '-' || '0' <= token[0] && token[0] <= '9') {
        if (isFinite(token)) {
            return new Const(parseInt(token));
        }
        throw new InvalidNumberError(token, tokens.location());
    } else {
        throw new UnknownLexemeError(token, tokens.location());
    }
}

function parseWrapper(expr, mode) {
    expr = (mode
            ? expr.replaceAll("(", " ) ").replaceAll(")", " ( ")
            : expr.replaceAll("(", " ( ").replaceAll(")", " ) ")
    ).split(" ").filter(x => x !== "");
    let source = new Source(expr, mode);
    let res = parseGeneral(source, mode);
    if (source.hasNext()) {
        throw new ExtraPartOfExpressionError(source.location(true));
    }
    return res;
}

const parsePostfix = expr => parseWrapper(expr, ParseMode.postfix);
const parsePrefix = expr => parseWrapper(expr, ParseMode.prefix);

function Source(source, reverse) {
    let tokens = source;
    if (reverse) {
        tokens.reverse();
    }
    let i = 0;
    return {
        next: function () {
            return tokens[i++];
        },
        hasNext: function () {
            return i < tokens.length;
        },
        getNext: function () {
            return tokens[i];
        },
        getPrev: function () {
            return tokens[i - 2];
        },
        size: function () {
            return tokens.length;
        },
        location: function (expand = false) {
            let str = " ->[ ";
            if (reverse) {
                for (let j = 0; j < source.length - i; j++) {
                    str = str.concat(source[j] === '(' ? ')' : (source[j] === ')' ? '(' : source[j]), " ");
                }
            } else {
                for (let j = 0; j < i; j++) {
                    str = str.concat(source[j], " ");
                }
            }
            str = str.concat(expand ? " >>-HERE->> " : " <<-HERE-<< ");
            if (reverse) {
                for (let j = source.length - i; j < source.length; j++) {
                    str = str.concat(source[j] === '(' ? ')' : (source[j] === ')' ? '(' : source[j]), " ");
                }
            } else {
                for (let j = i; j < source.length; j++) {
                    str = str.concat(source[j], " ");
                }
            }
            return str.concat(" ]<- ");
        }
    };
}

function createParseError(name, format) {
    let ParseError = function (...args) {
        this.message = format(...args);
    };
    ParseError.prototype = Object.create(Error.prototype);
    ParseError.prototype.name = name;
    return ParseError;
}

const UnknownLexemeError = createParseError("UnknownLexemeError", (lexeme, all) =>
    "Unknown operation or variable: '" + lexeme + "' in: " + all);
const InvalidNumberError = createParseError("InvalidNumberError", (token, all) =>
    "Invalid number: '" + token + "' in: " + all);
const SyntaxError = createParseError("SyntaxError", (token, prev, instead, all) =>
    `This token: '${token}' should not after '${prev}' instead '${instead}' in: ${all}`);
const WrongNumberOfOperandsError = createParseError("WrongNumberOfOperandsError", (sign, size, len, all) =>
    `Wrong number of operands for '${sign}': expected ${size}, found ${len} in: ${all}`);
const ExtraPartOfExpressionError = createParseError("ExtraPartOfExpressionError", all =>
    "Extra tokens: " + all);
const IncompleteExpressionError = createParseError("IncompleteExpressionError", all =>
    "Not enough tokens: " + all);
