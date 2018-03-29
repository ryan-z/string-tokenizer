// test cases for 463's Project 1.
// 
// $> javac -cp .:junit-4.11.jar *.java #compile everything
// $> java -cp .:junit-4.11.jar P1Tests #run tests
// 
// On windows (replacing : with ; (colon with semicolon))
// $> javac -cp .;junit-4.11.jar *.java #compile everything
// $> java -cp .;junit-4.11.jar P1Tests #run tests

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class P1Tests {
  public static void main(String args[]){
    org.junit.runner.JUnitCore.main("P1Tests");
  }
  
  public static String[] tests = {
    "t0.eachtoken",
    "t1.spec_sample",
    "t2.keywords",
    "t3.no_keywords",
    "t4.operators",
    "t5.identifiers",
    "t6.numbers",
    "t7.negative_numbers",
    "t8.strings1",
    "t9.strings.escapes",
    "t10.strings.comments",
    "t11.comments1",
    "t12.comments2",
    "t13.comments3",
    "t14.many.numbers",
    "t15.nonkeywords",
    "t16.strings.example",
    "t17.strings.comments.sneaky",
    "t18.div360",
    "t19.notMLC"
  };
  
  // useful for generating expected answers...
  public void build () { 
    for (String t : tests){
      String path = prefix + t + ".clango";
      try {
        PrintWriter pw = new PrintWriter(new File(path));
        pw.print(Tokenizer.tokenize(path));
        pw.close();
      }
      catch (FileNotFoundException e){
        throw new RuntimeException ("couldn't build results for: "+path);
      }
    }
  }
  
  public static String readFile (String filename) {
    try {  
      Scanner sc = new Scanner (new File(filename)); 
      StringBuilder s = new StringBuilder(); 
      while (sc.hasNext()){  s.append(sc.nextLine()+"\n");   }   
      return s.toString(); 
    }
    catch (FileNotFoundException e){  
      throw new RuntimeException("couldn't find the file: "+filename ); 
    }
  }
  
  public static final String prefix = "junit_tests/";
  
  public static void testUpon(String filenameBase){
    String s   = readFile(prefix+filenameBase+".clango"); 
    String t   = readFile(prefix+filenameBase+".tok");
    assertEquals(  t, Tokenizer.tokenize(s)); 
  }
  
  // each test is just using that test file and comparing their output with
  // the stored output.
  @Test  public void t0_eachtoken               (){ testUpon(tests[ 0]); }
  @Test  public void t1_spec_sample             (){ testUpon(tests[ 1]); }
  @Test  public void t2_keywords                (){ testUpon(tests[ 2]); }
  @Test  public void t3_no_keywords             (){ testUpon(tests[ 3]); }
  @Test  public void t4_operators               (){ testUpon(tests[ 4]); }
  @Test  public void t5_identifiers             (){ testUpon(tests[ 5]); }
  @Test  public void t6_numbers                 (){ testUpon(tests[ 6]); }
//  @Test  public void t7_negative_numbers        (){ testUpon(tests[ 7]); }
  @Test  public void t8_strings1                (){ testUpon(tests[ 8]); }
  @Test  public void t9_strings_escapes         (){ testUpon(tests[ 9]); }
  @Test  public void t10_strings_comments       (){ testUpon(tests[10]); }
  @Test  public void t11_comments1              (){ testUpon(tests[11]); }
  @Test  public void t12_comments2              (){ testUpon(tests[12]); }
  @Test  public void t13_comments3              (){ testUpon(tests[13]); }
  @Test  public void t14_many_numbers           (){ testUpon(tests[14]); }
  @Test  public void t15_non_keywords           (){ testUpon(tests[15]); }
  @Test  public void t16_strings_example        (){ testUpon(tests[16]); }
  @Test  public void t17_strings_comments_sneaky(){ testUpon(tests[17]); }
  @Test  public void t18_div360                 (){ testUpon(tests[18]); }
  @Test  public void t19_notMLC                 (){ testUpon(tests[19]); }
  
  @Test
  public void t_bad_comment(){ 
    try{ 
      testUpon("bad.comments"); 
    }catch(RuntimeException e){ } 
  }
  
  @Test
  public void t_bad_string(){
    try{  
      testUpon("bad.string");
    }catch(RuntimeException e){  }
  }
  
  @Test
  public void t_bad_filename(){ 
    try{ 
      testUpon("asdlfkjhasdlkfjhasdlk fjhasdlkfjhasdlfkhsdflkaj");
    }catch(RuntimeException e){ } 
  }
  }
