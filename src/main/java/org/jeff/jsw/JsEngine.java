package org.jeff.jsw;

import org.jeff.jsw.builtins.PrintFunction;
import org.jeff.jsw.objs.JsBuiltinFunction;
import org.jeff.jsw.statements.ASTParser;
import org.jeff.jsw.statements.Statement;
import org.jeff.jsw.tokens.Tokenizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsEngine
{
    private JsContext _context;

    public JsEngine()
    {
        this._context = new JsContext();
        this.loadBuiltin();
    }

    public JsEngine(JsContext context)
    {
        this._context = context;
    }

    public void setGlobal(String name, Object value)
    {
        this._context.set(name, value);
    }
    public void setFunction(String name, JsBuiltinFunction func)
    {
        this._context.set(name, func);
    }
    public Object eval(String code)
    {
        Tokenizer tokenizer = new Tokenizer(code);
        ASTParser parser = new ASTParser(tokenizer.tokenize());
        Statement statement = parser.parseStatements();
        //System.out.println(statement);

        JsInterpreter interpreter = new JsInterpreter(this._context);
        return interpreter.executeProgram(statement);
    }

    public Object doFile(String filename) throws Exception
    {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        String code = new String(bytes, StandardCharsets.UTF_8);
        return this.eval(code);
    }

    private void loadBuiltin()
    {
        this.setFunction("print", new PrintFunction());
    }
}
