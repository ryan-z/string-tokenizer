/*
 * Ryan Zaki
 * 2/19/2014
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Tokenizer
{
    
    public static enum states
    {
        state_none,
        state_doublequote,
        state_asterisk,
        state_singleslash,
        state_linecomment,
        state_blockcomment,
        state_blockcomment_star,
        state_blockcomment_slash,
        state_identifier,
        state_equal,
        state_or,
        state_and,
        state_lt,
        state_gt,
        state_not,
        state_num,
        state_stringescape,
    };

    private static states switchstate = states.state_none;
    private static StringBuilder output = new StringBuilder();
    private static String current_token = "";
    public static String tokenize (String input)
    {
        
        
        for (char c : input.toCharArray())
        {
            
            ProcessChar(c);
            
        }
        
        return output.toString();
        
    }


    public static String tokenizeFile(String fileIn)
    {//buffered reader
        File file = new File(fileIn);
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
            int content;
            while ((content = fis.read()) != -1)
            {
                ProcessChar((char)content);
            }
            
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("IO Exception!");
            e.printStackTrace();
        }
        ProcessChar(-1);
        
        return output.toString();
    }


    public static void tokenizeFile2File(String fileIn, String fileOut)
    {

        File file = new File(fileIn);
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
            
            int content;
            while ((content = fis.read()) != -1)
            {
                ProcessChar((char)content);
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("IO Exception!");
            e.printStackTrace();
        }
        ProcessChar(-1);
        
        ///////FileOutput:
        FileOutputStream fop = null;
        File file2;

        try {

            file2 = new File(fileOut);
            fop = new FileOutputStream(file2);

            // if file doesnt exists, then create it
            if (!file2.exists())
            {
                file2.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = output.toString().getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fop != null)
                {
                    fop.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        
    }

    static int block_counter = 0;
    static final int eof = -1;
    private static void ProcessChar(int c)
    {
        
        switch(switchstate)
        {
            case state_none:
                switch(c)
                {
                    case ' ':
                    case '\r':
                    case '\t'://todo?
                    case eof:
                        break;
                    case '\n':
                        break;
                    case '"':
                        switchstate = states.state_doublequote;
                        break;
                    case '/':
                        current_token += (char)c;
                        switchstate = states.state_singleslash;
                        break;
                    case '*':
                        current_token += (char)c;
                        switchstate = states.state_asterisk;
                        break;
                    case '=':
                        current_token += (char)c;
                        switchstate = states.state_equal;
                        break;
                    case '|':
                        current_token += (char)c;
                        switchstate = states.state_or;
                        break;
                    case '&':
                        current_token += (char)c;
                        switchstate = states.state_and;
                        break;
                    case '(':
                        output.append("ParenOpen\n");
                        break;
                    case ')':
                        output.append("ParenClose\n");
                        break;
                    case '+':
                        output.append("OpAdd\n");
                        break;
                    case '-':
                        output.append("OpSub\n");
                        break;
                    case ';':
                        output.append("Semicolon\n");
                        break;
                    case '{':
                        output.append("BraceOpen\n");
                        break;
                    case '}':
                        output.append("BraceClose\n");
                        break;
                    case '!':
                        current_token += (char)c;
                        switchstate = states.state_not;
                        break;
                    case '<':
                        current_token += (char)c;
                        switchstate = states.state_lt;
                        break;
                    case '>':
                        current_token += (char)c;
                        switchstate = states.state_gt;
                        break;
                    default:
                        if(Character.isJavaIdentifierStart(c) && (c != '$'))
                        {
                            switchstate = states.state_identifier;
                            current_token += (char)c;
                        }
                        else if(Character.isDigit(c))
                        {
                            switchstate = states.state_num;
                            current_token += (char)c;
                        }
                        else
                            output.append((char)c);
                        break;
                }
                break;


            case state_and:
                if(c == '&')
                    output.append("OpAnd\n");
                else
                    throw new RuntimeException("& character not suppoted by CLANG0. Perhaps you meant &&.");
                switchstate = states.state_none;
                break;
            
            case state_or:
                if(c == '|')
                    output.append("OpOr\n");
                else
                    throw new RuntimeException("| character not suppoted by CLANG0. Perhaps you meant ||.");
                switchstate = states.state_none;
                break;
            
            case state_doublequote:
                if(c == '\\')//string not done. found an escape char
                {
                    current_token += (char)c;
                    switchstate = states.state_stringescape;
                    
                }
                else if (c == '\"')//string finished
                {
                    switchstate = states.state_none;
                    output.append("String " + current_token + "\n");//might not need the \"
                    current_token = "";
                }
                else
                {
                    current_token += (char)c;
                    //todo: throw runtime error
                    if(c == -1)
                        throw new RuntimeException("Unmatched double quotation mark.");
                }
                break;
            
            case state_stringescape:
                if(c == '\"')
                {
                    switchstate = states.state_doublequote;
                    current_token += "\"";
                }
                else if(c == '\\')
                {
                    switchstate = states.state_doublequote;
                    current_token += "\\";
                }
                else if(c == 'n')
                {
                    switchstate = states.state_doublequote;
                    current_token += "n";
                }
                else if(c == 't')
                {
                    switchstate = states.state_doublequote;
                    current_token += "t";
                }
                else if(c == '\'')
                {
                    switchstate = states.state_doublequote;
                    current_token += "'";
                }
                else if(c == -1)
                    throw new RuntimeException("Escape character not specified");
                break;
            
            case state_equal:
                
                if (c == '=')
                {
                    switchstate = states.state_none;
                    current_token = "";
                    output.append("OpEq\n");
                }
                else
                {
                    switchstate = states.state_none;
                    current_token = "";
                    output.append("Equal\n");
                }
                break;
            
            case state_asterisk:
                if (c == '*')
                {
                    switchstate = states.state_none;
                    current_token = "";
                    output.append("OpPow\n");
                }
                else
                {
                    switchstate = states.state_none;
                    current_token = "";//flush
                    output.append("OpMul\n");
                    ProcessChar(c);
                }
                break;
            case state_not:
                if (c == '=')
                {
                    switchstate = states.state_none;
                    current_token = "";
                    output.append("OpNeq\n");
                }
                else
                {
                    switchstate = states.state_none;
                    current_token = "";//flush
                    output.append("OpNot\n");
                    ProcessChar(c);
                }
                break;
            case state_lt:
                if (c == '=')
                {
                    switchstate = states.state_none;
                    current_token = "";
                    output.append("OpLE\n");
                }
                else
                {
                    switchstate = states.state_none;
                    current_token = "";//flush
                    output.append("OpL\n");
                    ProcessChar(c);
                }
                break;
            case state_gt:
                if (c == '=')
                {
                    switchstate = states.state_none;
                    current_token = "";
                    output.append("OpGE\n");
                }
                else
                {
                    switchstate = states.state_none;
                    current_token = "";//flush
                    output.append("OpG\n");
                    ProcessChar(c);
                }
                break;
            
            
            case state_singleslash:
                
                if(c == '/')// line comment
                {
                    current_token = "";
                    switchstate = states.state_linecomment;
                }
                else if(c == '*')//first block comment
                {
                    current_token = "";
                    switchstate = states.state_blockcomment;
                    block_counter++;    //counter @ 1
                }
                else//oh no! it's not a comment, it's an OpDiv!
                {
                    switchstate = states.state_none;
                    current_token = "";//flush
                    output.append("OpDiv\n");
                    ProcessChar(c);
                    
                }
                break;
            
            case state_linecomment:
                if(c == '\n')
                    switchstate = states.state_none;
                break;
            case state_blockcomment:
                if(c == '*')
                    switchstate = states.state_blockcomment_star;
                else if(c == '/')
                    switchstate = states.state_blockcomment_slash;
                else if(c == eof)
                    throw new RuntimeException("Runtime Exception! Block comment left unterminated.");
                break;
            case state_blockcomment_star://in the midst of a /*, and just saw a *
                if(c == '/')
                {
                    block_counter--;
                    switchstate = states.state_blockcomment;
                    if(block_counter == 0)
                        switchstate = states.state_none;
                }
                else if (c != '*')
                    switchstate = states.state_blockcomment;
                
                break;
            
            case state_blockcomment_slash:
                if (c == '*')
                {
                    block_counter++;//we just nested block comments. @ first #2
                    switchstate = states.state_blockcomment;
                }
                break;
            
            case state_identifier:
                if(Character.isJavaIdentifierPart(c))//$ allowed?
                    current_token += (char)c;
                else
                {
                    int count_elems = 0;
                    String current_ident = current_token;
                    if (current_token.equals("BEGIN"))
                        output.append("Begin");
                    else if (current_token.equals("END"))
                        output.append("End");
                    else if (current_token.equals("if"))
                        output.append("If");
                    else if (current_token.equals("fi"))
                        output.append("Fi");
                    else if (current_token.equals("else"))
                        output.append("Else");
                    else if (current_token.equals("for"))
                        output.append("For");
                    else if (current_token.equals("from"))
                        output.append("From");
                    else if (current_token.equals("to"))
                        output.append("To");
                    else if (current_token.equals("while"))
                        output.append("While");
                    else if (current_token.equals("break"))
                        output.append("Break");
                    else if (current_token.equals("continue"))
                        output.append("Continue");
                    else if (current_token.equals("print"))
                        output.append("Print");
                    else if (current_token.equals("true"))
                        output.append("True");
                    else if (current_token.equals("false"))
                        output.append("False");
                    
                    else//it must be an identifier at this point:
                        output.append("Ident " + current_token);
                    output.append("\n");
                    current_token = "";
                    switchstate = states.state_none;
                    ProcessChar(c);
                }
                break;
            case state_num:
                if(Character.isDigit(c))
                    current_token += (char)c;
                else
                {
                    switchstate = states.state_none;
                    output.append("Num " + current_token + "\n");
                    current_token = "";
                    ProcessChar(c);
                }
                break;
        }
        
    }
}
