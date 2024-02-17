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

class JavaPrintableTree(val tree: ParseTree) {
    
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
        //Filepath
        val currentPath = Paths.get(".").normalize().toAbsolutePath();
        val filePath = Paths.get(currentPath.toString(), "src", "main", "assets", "Sample 3.java"); //Change File Name here

        //Get streams, set up lexer, set up parser
        val fileStream = FileInputStream(filePath.toString());
        val inputStream = CharStreams.fromStream(fileStream)

        val lexer = JavaLexer(inputStream)
        val tokens = CommonTokenStream(lexer)

        val parser = JavaParser(tokens)
        val tree = parser.compilationUnit()

        //AST cannot be printed on its own. Use a helper class to 
        val printableTree = JavaPrintableTree(tree)
        println(printableTree)
    }
    catch(err: FileNotFoundException) {
        println("File not found!")
        println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
    }
    catch(err: ArrayIndexOutOfBoundsException) {
        err.printStackTrace(System.out)
    }
}