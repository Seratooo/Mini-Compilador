/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import exceptions.SintaxeException;
import static exceptions.SintaxeException.SintaxeException;
import java.util.ArrayList;
import lexico.scanner;
import lexico.tokens;
import static lexico.tokens.listaDeTokens;

/**
 *
 * @author abubacar-dev
 */
public class isParser {

    private scanner isScanner;
    tokens token;
    int pos = 0;
    String modifiers = "public private static abstract final protected native synchronized transient volatile";
    String types = "int boolean float double string";
    String typesFunc = "int boolean float double string void";
    String type = "";
    String OPLogicos = "> < ! = != <= >= <<< << >>";
    String OPAritmeticos = "+ - *";
    public isParser(scanner isScaner) {
        this.isScanner = isScaner;
    }

    //VERIFICANDO EXPRESSÕES
    public void E() {
        tokens tk = null;
        if (listaDeTokens.get(listaDeTokens.size() - 1).getType() == 18) {
            tk = listaDeTokens.get(listaDeTokens.size() - 1);
        }
        listaDeTokens.add(tk);
        T();
        El();
    }

    private void El() {
        token = listaDeTokens.get(pos);
        pos++;
        if (token != null) {
            OP();
            T();
            El();
        }

    }

    private void T() {
        token = listaDeTokens.get(pos);
        if (token.getType() != 0 && token.getType() != 1 && token.getType() != 2) {
            SintaxeException("Numero ou identificador experado na linha: " + token.getLine() + " : " + token.getText());

        }
        pos++;
    }

    private void OP() {
        if (token.getType() != 18 && token.getType() != 19 && token.getType() != 20 && token.getType() != 21) {
            SintaxeException("Operador inválido, na linha: " + token.getLine() + ": " + token.getText());
        }
    }

    //VERIFICANDO DECLARAÇÃO DE VARIÁVEIS
    public void VarDeclaration() {
        token = listaDeTokens.get(pos);
        if (!typesFunc.contains(token.getText())) {
            do {
                if (listaDeTokens.get(pos + 1).getType() != 9) {
                    modifier();
                } else {
                    type = token.getText();
                    TypeFunc();
                    pos++;
                }

            } while (!typesFunc.contains(listaDeTokens.get(pos).getText()));
            //  pos--;
        } else {
            type = token.getText();
        }

        Identifier();
        if (listaDeTokens.get(pos + 1).getType() == 7) {
            methodDef();
            methodBody();
        } else if (listaDeTokens.get(pos + 1).getType() == 9) {
            semicolun();
        } else if (listaDeTokens.get(pos + 1).getType() == 17) {
            attribution();
           
            if ("string".equals(type)) {
                string();
            } else if ("float".equals(type)) {
                Float();
            } else if ("int".equals(type)) {
                integer();
            }
            semicolun();
        } else {
            token = listaDeTokens.get(pos);
            SintaxeException("Esperava um ponto e vírgula na linha " + token.getLine() + "  encontrei " + token.getText());
        }
    }

    private void string() {

        do {
            pos++;
        } while (listaDeTokens.get(pos + 1).getType() == 0);

        token = listaDeTokens.get(pos);
        if (token.getType() == 1 || token.getType() == 2) {
            SintaxeException("Esperava uma string: " + token.getLine() + " : " + token.getText());
            if (listaDeTokens.get(pos + 1).getType() != 9) {
                pos--;
            }
        }
    }

    private void Float() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 1 && token.getType() != 2) {
            SintaxeException("Esperava um valor numerico: " + token.getLine() + " : " + token.getText());
            if (listaDeTokens.get(pos + 1).getType() != 9) {
                if (listaDeTokens.get(pos + 1).getType() == 0) {
                    do {
                        pos++;
                    } while (listaDeTokens.get(pos).getType() == 0);
                }
                pos--;
            }
        }
    }

    private void integer() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 1) {
            SintaxeException("Esperava um valor numerico do tipo inteiro: " + token.getLine() + " : " + token.getText());
            if (listaDeTokens.get(pos + 1).getType() != 9) {
                if (listaDeTokens.get(pos + 1).getType() == 0) {
                    do {
                        pos++;
                    } while (listaDeTokens.get(pos).getType() == 0);
                }
                pos--;
            }
        }
    }

    private void attribution() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 17) {
            SintaxeException("Esperava um operador de atribuição: " + token.getLine() + " : " + token.getText());
        }
    }

    private void Type() {
        token = listaDeTokens.get(pos);
        pos++;
        if (!types.contains(token.getText())) {
            SintaxeException("Tipo de dados invalido: " + token.getLine() + " : " + token.getText());
        }
    }

    private void TypeFunc() {
        if (token.getType() == 0) {
            pos--;
        }
        token = listaDeTokens.get(pos);
        pos++;
        if (!typesFunc.contains(token.getText())) {
            SintaxeException("Tipo de dados invalido na linha: " + token.getLine());
        }

    }

    private void Identifier() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 0) {
            SintaxeException("Identificador inválido na linha: " + token.getLine());

        }
    }

    String err = "";

    private void error(String msg) {
        pos++;
        token = listaDeTokens.get(pos);
        if (!err.equals(msg)) {
            SintaxeException(msg);
            err = msg;
        }
    }

    //VERIFICANDO O PACKAGE
    public void packegeFunc() {
        Package();
        pos--;
        Identifier();
        semicolun();
    }

    private void Package() {

        token = listaDeTokens.get(pos);
        pos++;
        if (!"package".equals(token.getText())) {
            SintaxeException("Esperava a declaração do pacote na linha " + token.getLine());
        }
    }

    private void semicolun() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 9) {
            SintaxeException("Esperava um ponto e vírgula no final na linha " + (token.getLine()-1) );
        }
    }

    private void comma() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 11) {
            SintaxeException("Esperava um vírgula na linha " + token.getLine());
        }
    }

    private void point() {

        token = listaDeTokens.get(pos);
        pos++;
        if (token.getType() != 10) {
            SintaxeException("Esperava um ponto na linha " + token.getLine());
        }
    }

    //Verificando IMPORT
    public void importFunc() {
        Import();
        ImportIdentifier();

        if (listaDeTokens.get(pos + 1).getType() == 9) {
            semicolun();
        } else {
            SintaxeException("Esperava um ponto e virgula na linha " + token.getLine());
        }

    }

    private void Import() {

        token = listaDeTokens.get(pos);
        pos++;
        if (!"import".equals(token.getText())) {
            SintaxeException("Esperava a declaração do import na linha " + token.getLine());
        }
    }

    private void ImportIdentifier() {
        token = listaDeTokens.get(pos);
        if (token.getType() == 0) {
            pos--;
            Identifier();
        } else {
            SintaxeException("Esperava um identificador na linha " + token.getLine());

        }
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() == 10) {
            point();
            ImportIdentifier();
        } else {
            pos--;
        }
    }
    //Verificando MODIFICADORES

    public void Modifiers() {
        modifier();
        ml();
        TypeFunc();
    }

    private void modifier() {
        token = listaDeTokens.get(pos);
        if (!typesFunc.contains(token.getText()) && !token.getText().equals("class")) {
            if (!modifiers.contains(token.getText())) {
                SintaxeException("existe modificador invalido na linha " + token.getLine());
                pos++;
            } else {
                pos++;
            }
        }

    }

    private void ml() {
        token = listaDeTokens.get(pos);
        if (!typesFunc.contains(token.getText()) && token.getType() != 0) {
            modifier();
            ml();
        }

    }

    //Verificando Interface
    public void interfaceFunc() {
        token = listaDeTokens.get(pos);
        if (modifiers.contains(token.getText())) {
            Modifiers();
            pos--;
        }
        Interface();
    }

    private void Interface() {
        token = listaDeTokens.get(pos);
        if (!token.getText().equals("interface")) {
            SintaxeException("Esperava a delcaracao de interface na linha " + token.getLine());
            pos++;
        } else {
            pos++;
        }
        pos--;
        Identifier();
        if (listaDeTokens.get(pos + 1).getText().equals("extends")) {
            pos++;
            Extends();
        }
        interfaceBody();

    }

    private void interfaceBody() {
        String escopoInterface = "";
        abrirChaves();

        while (listaDeTokens.get(pos + 1).getType() != 13) {
            //METODOS COM MODIFICADORES

            if ((modifiers.contains(listaDeTokens.get(pos + 1).getText()) && listaDeTokens.get(pos + 2).getType() == 0 && listaDeTokens.get(pos + 3).getType() == 7)
                    || (listaDeTokens.get(pos + 1).getType() == 0 && listaDeTokens.get(pos + 2).getType() == 7)) {
                pos++;
                //ESCOPO
                int i = pos;
                do {
                    i++;
                } while (listaDeTokens.get(i).getType() != 0);

                if (!escopoInterface.contains(listaDeTokens.get(i).getText())) {
                    escopoInterface = " " + listaDeTokens.get(i).getText();

                } else {

                    SintaxeException("Variável declarada novamente no mesmo escopo na linha " + listaDeTokens.get(i).getLine() + " palavra -> " + listaDeTokens.get(i).getText());
                }

                constructFunc();
            } else {
                pos++;
                //ESCOPO
                int i = pos;
                do {
                    i++;
                } while (listaDeTokens.get(i).getType() != 0);

                if (!escopoInterface.contains(listaDeTokens.get(i).getText())) {
                    escopoInterface = " " + listaDeTokens.get(i).getText();

                } else {

                    SintaxeException("Variável declarada novamente no mesmo escopo na linha " + listaDeTokens.get(i).getLine() + " palavra -> " + listaDeTokens.get(i).getText());
                }

                VarDeclaration();
            }
        }

        fecharChaves();
    }

    private void abrirChaves() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 12) {
            SintaxeException("Esperava abrir chaves na linha " + token.getLine());
        }
    }

    private void fecharChaves() {
        pos++;
        if (pos < listaDeTokens.size()) {
            token = listaDeTokens.get(pos);
            if (token.getType() != 13) {
                SintaxeException("Esperava fechar chaves na linha " + token.getLine());

            }
        } else {
            SintaxeException("Esperava fechar chaves na linha " + token.getLine());
        }
    }

    private void Extends() {
        token = listaDeTokens.get(pos);
        if (!token.getText().equals("extends")) {
            SintaxeException("Esperava extends na linha " + token.getLine());
            pos++;
        } else {
            pos++;
        }
        pos--;
        Identifier();
    }

    public void Implements() {
        token = listaDeTokens.get(pos);
        if (!token.getText().equals("implements")) {
            SintaxeException("Esperava implements na linha " + token.getLine());
            pos++;
        } else {
            pos++;
        }
        pos--;
        Identifier();
    }
    
     private void DoWhileFunc(){
       DoWhile();
       bodyStatment(); 
       
       While();
       AbrirParenteses();
       LogicExpress();
       FecharParentes();
       semicolun();      
       
    }
    private void DoWhile(){
        pos++;
        token = listaDeTokens.get(pos);
        if (!"do".equals(token.getText())) {
            SintaxeException("Espera uma condição " + token.getLine());
        }
    }
    
    private void WhileFunc(){
       While();
       AbrirParenteses();
       LogicExpress();
       FecharParentes();
       bodyStatment();
       
    }
  
    private void While() {
        pos++;
        token = listaDeTokens.get(pos);
        if (!"while".equals(token.getText())) {
            SintaxeException("Espera uma condição " + token.getLine());
        }
    }

    private void IfFunc() {
        IF();
        AbrirParenteses();
        LogicExpress();
        FecharParentes();
        bodyStatment();

    }

    private void IF() {
        pos++;

        token = listaDeTokens.get(pos);
        if (!"if".equals(token.getText())) {
            SintaxeException("Espera uma condição " + token.getLine());
        }
    }

    private void operator() {
        pos++;
        token = listaDeTokens.get(pos);
        if (!OPLogicos.contains(listaDeTokens.get(pos + 1).getText())) {
              if (listaDeTokens.get(pos + 1).getType() != 1 && listaDeTokens.get(pos + 1).getType() != 2 && listaDeTokens.get(pos + 1).getType() != 0 ){
                            SintaxeException("Esperava um operador " + token.getLine());
               }
        }
    }

    private void LogicExpress() {
        //SE FOR NUMERO
        ConstExpr();
    }

    private void bodyStatment() {
        abrirChaves();
        fecharChaves();
    }

    //EXPRESSOES
    private void ConstExpr() {
        Expr();
    }

    private void Expr() {
        AssignExpr();
    }

    private void AssignExpr() {
        Assign();
    }

    private void Assign() {

       
        AssignOp();
        
        LHS();

        if (listaDeTokens.get(pos + 1).getType() != 8) {
            AssignExpr();
        }
    }

    private void LHS() {
        //NOME
        if (listaDeTokens.get(pos + 1).getType() != 1 && listaDeTokens.get(pos + 1).getType() != 2 && listaDeTokens.get(pos + 1).getType() != 0) {
            SintaxeException("Esperava um identificador " + token.getLine());

        } else {
             if(listaDeTokens.get(pos + 1).getType() != 0){
                 Float();
             }else{
                 string();
             }
        }

    }

    private void AssignOp() {
        //OPEDARODERS
        
        if (!OPLogicos.contains(listaDeTokens.get(pos + 1).getText())) {
             if (listaDeTokens.get(pos + 1).getType() != 1 && listaDeTokens.get(pos + 1).getType() != 2 && listaDeTokens.get(pos + 1).getType() != 0 ){
                            SintaxeException("Esperava um operador " + token.getLine());
               }
        } else {
            
              operator();
        }

    }

    //VERIFICANDO CLASS
    public void classFunc() {
        modifier();
        Class();
    }

    private void Class() {
        token = listaDeTokens.get(pos);
        if (!token.getText().equals("class")) {
            SintaxeException("Esperava class na linha " + token.getLine());
        }
        Identifier();

        if (listaDeTokens.get(pos + 1).getText().equals("extends")) {
            pos++;
            Extends();
        }
        if (listaDeTokens.get(pos + 1).getText().equals("implements")) {
            pos++;
            Implements();
        }

        ClassBody();
    }

    private void ClassBody() {
        String classInterface = "";
        abrirChaves();

        while (listaDeTokens.get(pos + 1).getType() != 13) {
            //METODOS COM MODIFICADORES

            if ((modifiers.contains(listaDeTokens.get(pos + 1).getText()) && listaDeTokens.get(pos + 2).getType() == 0 && listaDeTokens.get(pos + 3).getType() == 7)
                    || (listaDeTokens.get(pos + 1).getType() == 0 && listaDeTokens.get(pos + 2).getType() == 7)) {
                pos++;
                //ESCOPO
                int i = pos;
                do {
                    i++;
                } while (listaDeTokens.get(i).getType() != 0);

                if (!classInterface.contains(listaDeTokens.get(i).getText())) {
                    classInterface = " " + listaDeTokens.get(i).getText();

                } else {

                    SintaxeException("Variável declarada novamente no mesmo escopo na linha " + listaDeTokens.get(i).getLine() + " palavra -> " + listaDeTokens.get(i).getText());
                }

                constructFunc();
            } else {
                pos++;
                //ESCOPO
                int i = pos;
                do {
                    i++;
                } while (listaDeTokens.get(i).getType() != 0);

                if (!classInterface.contains(listaDeTokens.get(i).getText())) {
                    classInterface = " " + listaDeTokens.get(i).getText();

                } else {

                    SintaxeException("Variável declarada novamente no mesmo escopo na linha " + listaDeTokens.get(i).getLine() + " palavra -> " + listaDeTokens.get(i).getText());
                }

                VarDeclaration();
            }
        }

        fecharChaves();
    }

    //Verificando MÉTODOS
    public void MethodFunc() {
        token = listaDeTokens.get(pos);
        if (modifiers.contains(token.getText())) {
            Modifiers();
            pos--;
        }

        method();
        methodBody();
    }

    private void method() {
        TypeFunc();
        methodDef();
        if ("throws".equals(listaDeTokens.get(pos + 1).getText())) {
            pos++;
            Throws();
        }
    }

    private void Throws() {
        token = listaDeTokens.get(pos);
        if (!token.getText().equals("throws")) {
            SintaxeException("Esperava throws na linha " + token.getLine());
            pos++;
        }
        pos--;
        Identifier();
    }

    private void methodBody() {
        String escopoMethodBody = "";
        if (listaDeTokens.get(pos + 1).getType() == 9) {
            semicolun();
        } else {

            abrirChaves();
            while (listaDeTokens.get(pos + 1).getType() != 13) {
                //METODOS COM MODIFICADORES

                if ("if".equals(listaDeTokens.get(pos + 1).getText())) {
                    IfFunc();
                }
                
                if ("while".equals(listaDeTokens.get(pos + 1).getText())) {
                    WhileFunc();
                }
                
                if ("do".equals(listaDeTokens.get(pos + 1).getText())) {
                    DoWhileFunc();
                }
                
                if ((modifiers.contains(listaDeTokens.get(pos + 1).getText()) && listaDeTokens.get(pos + 2).getType() == 0 && listaDeTokens.get(pos + 3).getType() == 7)
                        || (listaDeTokens.get(pos + 1).getType() == 0 && listaDeTokens.get(pos + 2).getType() == 7)) {
                    pos++;
                    //ESCOPO
                    int i = pos;
                    do {
                        i++;
                    } while (listaDeTokens.get(i).getType() != 0);

                    if (!escopoMethodBody.contains(listaDeTokens.get(i).getText())) {
                        escopoMethodBody = " " + listaDeTokens.get(i).getText();

                    } else {

                        SintaxeException("Variável declarada novamente no mesmo escopo na linha " + listaDeTokens.get(i).getLine() + " palavra -> " + listaDeTokens.get(i).getText());
                    }

                    constructFunc();
                } else {
                    pos++;

                    //ESCOPO
                    int i = pos;
                    do {
                        i++;
                    } while (listaDeTokens.get(i).getType() != 0);

                    if (!escopoMethodBody.contains(listaDeTokens.get(i).getText())) {
                        escopoMethodBody = " " + listaDeTokens.get(i).getText();

                    } else {

                        SintaxeException("Variável declarada novamente no mesmo escopo na linha " + listaDeTokens.get(i).getLine() + " palavra -> " + listaDeTokens.get(i).getText());
                    }
                    VarDeclaration();
                }
            }

            fecharChaves();
        }

    }

    private void methodDef() {
        pos--;
        Identifier();
        AbrirParenteses();
        if (listaDeTokens.get(pos + 1).getType() == 5) {
            pos++;
            ParamList();
        }
        FecharParentes();

    }

    private void AbrirParenteses() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 7) {
            SintaxeException("Esperava abrir parenteses na linha " + token.getLine());

        }
    }

    private void FecharParentes() {
        pos++;
        token = listaDeTokens.get(pos);
        if (token.getType() != 8) {
            SintaxeException("Esperava fechar parenteses na linha " + token.getLine());

        }
    }

    private void ParamList() {

        Param();
        ParamListLinha();
    }

    private void ParamListLinha() {
        if (listaDeTokens.get(pos).getType() == 11) {
            pos++;
            Param();
            ParamListLinha();
        } else {
            pos--;
        }
    }

    private void Param() {
        Type();
        pos--;
        Identifier();
        pos++;
    }

    //VERIFICANDO CONSTRUTOR
    public void constructFunc() {
        token = listaDeTokens.get(pos);
        if (modifiers.contains(listaDeTokens.get(pos).getText()) && listaDeTokens.get(pos + 1).getType() == 0) {
            modifier();
            Construct();
            if ("throws".equals(listaDeTokens.get(pos + 2).getText())) {
                pos++;
                Throws();
            }

            constructBody();
        } else if (listaDeTokens.get(pos).getType() == 0 && listaDeTokens.get(pos + 1).getType() == 7) {
            Construct();
            if ("throws".equals(listaDeTokens.get(pos + 1).getText())) {
                pos++;
                Throws();
            }

            constructBody();
        } else {
            SintaxeException("Sintaxe para construtor errada na linha " + token.getLine());
        }

    }

    private void Construct() {
        pos--;
        Identifier();
        AbrirParenteses();
        if (listaDeTokens.get(pos + 1).getType() == 5) {
            pos++;
            ParamList();
        }
        FecharParentes();
    }

    private void constructBody() {
        abrirChaves();
        fecharChaves();
    }

    private void callTheNext() {
        pos++;
        token = listaDeTokens.get(pos);
    }

    String escopoInterface = " ", escopoClass = "";

    public void VerifySintaxe() {
        token = listaDeTokens.get(pos);

        while (true) {

            if (token.getText().contains("package")) {
                do {
                    packegeFunc();
                } while (token.getType() != 9);
                callTheNext();
            } else if (token.getText().contains("import")) {
                do {
                    importFunc();
                } while (token.getType() != 9);
                callTheNext();
            } else if (modifiers.contains(token.getText())) {
                if (listaDeTokens.get(pos + 1).getText().equals("class")) {

                    if (!escopoClass.contains(listaDeTokens.get(pos + 1).getText())) {
                        escopoClass += " " + listaDeTokens.get(pos + 1).getText();
                    } else {
                        SintaxeException("Mais de uma classe no programa: " + listaDeTokens.get(pos + 1).getText());
                    }

                    do {
                        classFunc();
                    } while (token.getType() != 13);
                    callTheNext();
                } else {
                    classFunc();
                }

            } else if (token.getText().contains("interface")) {
                //System.out.print("INTERFACE NAME: "+);
                if (!escopoInterface.contains(listaDeTokens.get(pos + 1).getText())) {
                    escopoInterface += " " + listaDeTokens.get(pos + 1).getText();
                } else {
                    SintaxeException("Interfaces declaradas com o mesmo nome: " + listaDeTokens.get(pos + 1).getText());
                }

                do {
                    interfaceFunc();
                } while (token.getType() != 13);
                callTheNext();
            } else {
                pos--;
                error("Palavra não reconhecida na linha " + token.getLine());
                callTheNext();
            }

        }
    }

}
