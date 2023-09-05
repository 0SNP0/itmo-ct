"use strict";

const VARIABLES = ["x", "y", "z"];

const cnst = val => (...vars) => val;
const variable = name => {
    let index = VARIABLES.indexOf(name);
    return (...vars) => vars[index];
}

const OPERATIONS = new Map();
const operation = (act, char, size) => {
    let op = (...operands) => (...vars) => act(...operands.map(operand => operand(...vars)));
    OPERATIONS.set(char, [op, size]);
    return op;
}
const add = operation((a, b) => a + b, "+", 2);
const subtract = operation((a, b) => a - b, "-", 2);
const multiply = operation((a, b) => a * b, "*", 2);
const divide = operation((a, b) => a / b, "/", 2);
const negate = operation(a => -a, "negate", 1);
const min5 = operation((a, b, c, d, e) => Math.min(a, b, c, d, e), "min5", 5);
const max3 = operation((a, b, c) => Math.max(a, b, c), "max3", 3);

const CONSTANTS = {
    "one" : cnst(1),
    "two" : cnst(2)
};
const one = CONSTANTS["one"];
const two = CONSTANTS["two"];

const parse = expr => (x, y, z) => {
    let stack = [];
    expr.split(" ").filter(x => x !== "").forEach(token => {
        if (OPERATIONS.has(token)) {
            let act = OPERATIONS.get(token);
            let operands = stack.slice(stack.length - act[1]);
            stack.splice(-act[1]);
            stack.push(act[0](...operands));
        } else if (VARIABLES.includes(token)) {
            stack.push(variable(token));
        } else if (token in CONSTANTS) {
            stack.push(CONSTANTS[token]);
        } else {
            stack.push(cnst(parseInt(token)));
        }
    });
    return stack.pop()(x, y, z);
};
