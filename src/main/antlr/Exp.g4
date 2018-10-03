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
    | '(' expression ')'
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

COMMENT
    : '//' ~[\r\n] -> skip
    ;

WHITESPACE
    : (' ' | '\t' | '\r'| '\n') -> skip
    ;