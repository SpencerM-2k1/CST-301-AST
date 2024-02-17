/*
 * Spencer Meren
 * February 16, 2024
 * CST-301
 */

/*
 * References
 * "Creating a Parser Using ANTLR4 and Gradle" by Introspective Thinker
 * https://www.youtube.com/watch?v=FCfiCPIeE2Y
 */

package org.example

import JavaLexer
import JavaParser 
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

import java.io.FileInputStream
import java.io.FileNotFoundException

import java.nio.file.Paths
import java.nio.file.Path
import java.io.File
import java.io.FileWriter

class JavaPrintableTree(val tree: ParseTree, val writer: FileWriter) {
    
    override fun toString() = treeString(tree, "")

    //Recursive tree generator
    private fun treeString(node: ParseTree, prefix: String): String {
        
        if (node is TerminalNode) return visitTerminal(node)
        if (node !is RuleNode) return "ERROR"

        //is RuleNode
        val name = JavaParser.ruleNames[node.ruleContext.ruleIndex]
        val builder = StringBuilder(name)

        for (i in 0..(node.childCount - 1)) {
            val atEnd = (i == node.childCount - 1)
            val symbol = if(atEnd) "└──" else "├──"

            val child = node.getChild(i)
            val childSymbol = if(atEnd) " " else "|"
            val childStr = treeString(child,  "$prefix$childSymbol   ")

            builder.append("\n$prefix$symbol $childStr")
        }
        
        return "$builder"
    }

    private fun visitTerminal(node: TerminalNode): String {
        //Used for EOF, prevents out of index error
        if(node.symbol.type < 1) return "P'$node'"

        //Set prefix
        val id = JavaParser.ruleNames[node.symbol.type - 1].let { if("T__" in it) "P" else it }

        return "$id'$node'"
    }
}

fun main() {
    
    try {
        //Filepaths
        val currentPath = Paths.get(".").normalize().toAbsolutePath();
        val loadPath1 = Paths.get(currentPath.toString(), "src", "main", "assets", "Sample 1.java"); //Change File Name here
        val loadPath2 = Paths.get(currentPath.toString(), "src", "main", "assets", "Sample 2.java"); //Change File Name here
        val loadPath3 = Paths.get(currentPath.toString(), "src", "main", "assets", "Sample 3.java"); //Change File Name here
        val savePath = Paths.get(currentPath.toString(), "Output Log.txt")

        //Set up FileWriter for output log
        val writer = FileWriter(savePath.toString());
        writer.write("")

        //Get streams, set up lexer, set up parser
        val fileStream1 = FileInputStream(loadPath1.toString());
        val fileStream2 = FileInputStream(loadPath2.toString());
        val fileStream3 = FileInputStream(loadPath3.toString());
        val inputStream1 = CharStreams.fromStream(fileStream1)
        val inputStream2 = CharStreams.fromStream(fileStream2)
        val inputStream3 = CharStreams.fromStream(fileStream3)

        val lexer1 = JavaLexer(inputStream1)
        val lexer2 = JavaLexer(inputStream2)
        val lexer3 = JavaLexer(inputStream3)
        val tokens1 = CommonTokenStream(lexer1)
        val tokens2 = CommonTokenStream(lexer2)
        val tokens3 = CommonTokenStream(lexer3)

        val parser1 = JavaParser(tokens1)
        val parser2 = JavaParser(tokens2)
        val parser3 = JavaParser(tokens3)
        val tree1 = parser1.compilationUnit()
        val tree2 = parser2.compilationUnit()
        val tree3 = parser3.compilationUnit()

        //AST cannot be printed on its own. Use a helper class to 
        val printableTree1 = JavaPrintableTree(tree1, writer)
        val printableTree2 = JavaPrintableTree(tree2, writer)
        val printableTree3 = JavaPrintableTree(tree3, writer)
        val treeString1 = printableTree1.toString()
        val treeString2 = printableTree1.toString()
        val treeString3 = printableTree1.toString()
        
        //Print to console & log
        println(treeString1)
        println("")
        println(treeString2)
        println("")
        println(treeString3)
        println("")

        writer.append(treeString1)
        writer.append("\n\n")
        writer.append(treeString2)
        writer.append("\n\n")
        writer.append(treeString3)
        writer.append("\n\n")

        //Cleanup
        writer.close()
    }
    catch(err: FileNotFoundException) {
        println("File not found!")
        println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
    }
    catch(err: ArrayIndexOutOfBoundsException) {
        err.printStackTrace(System.out)
    }
}