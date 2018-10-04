grammar Exp;

file
    : block EOF
    ;

block
    : statement*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function | variable | expression | whileBlock | ifBlock | assignment | returnStatement
    ;

function
    : 'fun' IDENTIFIER '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

parameterNames
    : ((IDENTIFIER ',')* IDENTIFIER)?
    ;

whileBlock
    : 'while' '(' expression ')' blockWithBraces
    ;

ifBlock
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnStatement
    : 'return' expression
    ;

expressionWithBraces
    : '(' expression ')'
    ;

expression
    : functionCall
    | larg = expression op = ('*' | '/' | '%') rarg = expression
    | larg = expression op = ('+' | '-') rarg = expression
    | larg = expression op = ('<' | '<=' | '>' | '>=') rarg = expression
    | larg = expression op = ('==' | '!=') rarg = expression
    | larg = expression op = '&&' rarg = expression
    | larg = expression op = '||' rarg = expression
    | IDENTIFIER
    | LITERAL
    | expressionWithBraces
    ;

functionCall
    : IDENTIFIER '(' arguments ')'
    ;

arguments
    : ((expression ',')* expression)?
    ;

IDENTIFIER
    : '_'? [a-zA-Z_] [a-zA-Z_0-9]*
    ;

LITERAL
    : ([1-9] [0-9]*) | '0'
    ;

SKIPPED
    : (' ' | '\t' | '\r'| '\n' | '//' .*? (('\r')?'\n' | EOF)) -> skip
    ;